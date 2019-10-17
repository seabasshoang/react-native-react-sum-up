package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.sumup.merchant.Models.TransactionInfo;
import com.sumup.merchant.api.SumUpAPI;
import com.sumup.merchant.api.SumUpLogin;
import com.sumup.merchant.api.SumUpPayment;
import com.sumup.merchant.CoreState;
import com.sumup.merchant.Models.UserModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class ReactSumUpModule extends ReactContextBaseJavaModule {
    private static final int REQUEST_CODE_LOGIN = 1;
    private static final int REQUEST_CODE_PAYMENT = 2;
    private static final int REQUEST_CODE_PAYMENT_SETTINGS = 3;
    private static final int TRANSACTION_SUCCESSFUL = 1;

    private Promise mSumUpPromise;

    public ReactSumUpModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(mActivityEventListener);
    }

    @Override
    public String getName() {
        return "SumUpBridge";
    }

    @ReactMethod
    public void presentLoginFromViewController(String affiliateKey, Promise promise) {
        mSumUpPromise = promise;
        SumUpLogin sumupLogin = SumUpLogin.builder(affiliateKey).build();
        SumUpAPI.openLoginActivity(getCurrentActivity(), sumupLogin, REQUEST_CODE_LOGIN);
    }

    @ReactMethod
    public void loginToSumUpWithToken(String affiliateKey, String token, Promise promise) {
        mSumUpPromise = promise;
        SumUpLogin sumupLogin = SumUpLogin.builder(affiliateKey).accessToken(token).build();
        SumUpAPI.openLoginActivity(getCurrentActivity(), sumupLogin, REQUEST_CODE_LOGIN);
    }

    @ReactMethod
    public void preparePaymentCheckout(Promise promise) {
        mSumUpPromise = promise;
        SumUpAPI.prepareForCheckout();
    }

    @ReactMethod
    public void logout(Promise promise) {
        mSumUpPromise = promise;
        SumUpAPI.logout();
        mSumUpPromise.resolve(true);
    }



    @ReactMethod
    public void paymentCheckout(ReadableMap request, Promise promise) {
        // TODO: replace foreignTransactionId for transaction UUID sent by user.
        mSumUpPromise = promise;
        try {
            String foreignTransactionId = UUID.randomUUID().toString();
            if (request.getString("foreignTransactionId") != null) {
                foreignTransactionId = request.getString("foreignTransactionId");
            }
            SumUpPayment payment = SumUpPayment.builder()
                    .total(new BigDecimal(request.getString("totalAmount")).setScale(2, RoundingMode.HALF_EVEN))
                    .currency(SumUpPayment.Currency.EUR)
                    .title(request.getString("title"))
                    .foreignTransactionId(foreignTransactionId)
                    .skipSuccessScreen()
                    .build();
            SumUpAPI.checkout(getCurrentActivity(), payment, REQUEST_CODE_PAYMENT);
        } catch (Exception ex) {
            mSumUpPromise.reject(ex);
            mSumUpPromise = null;
        }
    }

    @ReactMethod
    public void preferences(Promise promise) {
        mSumUpPromise = promise;
        SumUpAPI.openPaymentSettingsActivity(getCurrentActivity(), REQUEST_CODE_PAYMENT_SETTINGS);
    }

    @ReactMethod
    public void isLoggedIn(Promise promise) {
        WritableMap map = Arguments.createMap();
        map.putBoolean("isLoggedIn", SumUpAPI.isLoggedIn());
        promise.resolve(map);
    }

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {

        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            switch (requestCode) {
                case REQUEST_CODE_LOGIN:
                    if (data != null) {
                        Bundle extra = data.getExtras();
                        if (extra.getInt(SumUpAPI.Response.RESULT_CODE) == REQUEST_CODE_LOGIN) {
                            WritableMap map = Arguments.createMap();
                            map.putBoolean("success", true);

                            UserModel userInfo = CoreState.Instance().get(UserModel.class);
                            WritableMap userAdditionalInfo = Arguments.createMap();
                            userAdditionalInfo.putString("merchantCode", userInfo.getBusiness().getMerchantCode());
                            userAdditionalInfo.putString("currencyCode", userInfo.getBusiness().getCountry().getCurrency().getCode());
                            map.putMap("userAdditionalInfo", userAdditionalInfo);

                            mSumUpPromise.resolve(map);
                        } else {
                            mSumUpPromise.reject(extra.getString(SumUpAPI.Response.RESULT_CODE), extra.getString(SumUpAPI.Response.MESSAGE));
                        }
                    }
                    break;

                case REQUEST_CODE_PAYMENT:
                    if (data != null) {
                        Bundle extra = data.getExtras();
                        if (mSumUpPromise != null) {
                            if (extra.getInt(SumUpAPI.Response.RESULT_CODE) == TRANSACTION_SUCCESSFUL) {
                                WritableMap map = Arguments.createMap();
                                map.putBoolean("success", true);
                                map.putString("transactionCode", extra.getString(SumUpAPI.Response.TX_CODE));

                                TransactionInfo transactionInfo = extra.getParcelable(SumUpAPI.Response.TX_INFO);
                                WritableMap additionalInfo = Arguments.createMap();
                                additionalInfo.putString("cardType", transactionInfo.getCard().getType());
                                additionalInfo.putString("cardLast4Digits", transactionInfo.getCard().getLast4Digits());
                                additionalInfo.putInt("installments", transactionInfo.getInstallments());
                                map.putMap("additionalInfo", additionalInfo);

                                mSumUpPromise.resolve(map);
                            }else
                                mSumUpPromise.reject(extra.getString(SumUpAPI.Response.RESULT_CODE), extra.getString(SumUpAPI.Response.MESSAGE));
                        }
                    }
                    break;
                case REQUEST_CODE_PAYMENT_SETTINGS:
                    WritableMap map = Arguments.createMap();
                    map.putBoolean("success", true);
                    mSumUpPromise.resolve(map);
                    break;
                default:
                    break;
            }
        }

    };
}

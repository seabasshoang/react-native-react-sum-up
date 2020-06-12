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

    private Promise sumUpPromise;

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
        sumUpPromise = promise;
        SumUpLogin sumupLogin = SumUpLogin.builder(affiliateKey).build();
        SumUpAPI.openLoginActivity(getCurrentActivity(), sumupLogin, REQUEST_CODE_LOGIN);
    }

    @ReactMethod
    public void loginToSumUpWithToken(String affiliateKey, String token, Promise promise) {
       if (SumUpAPI.isLoggedIn()) {
            WritableMap map = Arguments.createMap();
            map.putBoolean("success", true);
            promise.resolve(map);
        } else {
            sumUpPromise = promise;
            SumUpLogin sumupLogin = SumUpLogin.builder(affiliateKey).accessToken(token).build();
            SumUpAPI.openLoginActivity(getCurrentActivity(), sumupLogin, REQUEST_CODE_LOGIN);
        }
    }

    @ReactMethod
    public void preparePaymentCheckout(Promise promise) {
        sumUpPromise = promise;
        SumUpAPI.prepareForCheckout();
    }

    @ReactMethod
    public void logout(Promise promise) {
        sumUpPromise = promise;
        SumUpAPI.logout();
        sumUpPromise.resolve(true);
    }

     private SumUpPayment.Currency getCurrency(String currency) {
        switch (currency) {
            case "BGN": return SumUpPayment.Currency.BGN;
            case "BRL": return SumUpPayment.Currency.BRL;
            case "CHF": return SumUpPayment.Currency.CHF;
            case "CZK": return SumUpPayment.Currency.CZK;
            case "DKK": return SumUpPayment.Currency.DKK;
            case "EUR": return SumUpPayment.Currency.EUR;
            case "GBP": return SumUpPayment.Currency.GBP;
            case "NOK": return SumUpPayment.Currency.NOK;
            case "PLN": return SumUpPayment.Currency.PLN;
            case "SEK": return SumUpPayment.Currency.SEK;
            case "CLP": return SumUpPayment.Currency.CLP;
            default:  return SumUpPayment.Currency.CLP;
        }
    }


    @ReactMethod
    public void paymentCheckout(ReadableMap request, Promise promise) {

        sumUpPromise = promise;
        try {
            String foreignTransactionId = "";
            if (request.getString("foreignID") != null) {
                foreignTransactionId = request.getString("foreignID");
            }
            SumUpPayment.Currency currencyCode = this.getCurrency(request.getString("currencyCode"));
            SumUpPayment payment;
            if(request.getString("skipScreenOptions") == "true" ) {
               payment = SumUpPayment.builder()
                        .total(new BigDecimal(request.getString("totalAmount")).setScale(2, RoundingMode.HALF_EVEN))
                        .currency(currencyCode)
                        .title(request.getString("title"))
                        .foreignTransactionId(foreignTransactionId)
                        .skipSuccessScreen()
                        .build();
            }else {
                payment = SumUpPayment.builder()
                        .total(new BigDecimal(request.getString("totalAmount")).setScale(2, RoundingMode.HALF_EVEN))
                        .currency(currencyCode)
                        .title(request.getString("title"))
                        .foreignTransactionId(foreignTransactionId)
                        .build();
            }

            SumUpAPI.checkout(getCurrentActivity(), payment, REQUEST_CODE_PAYMENT);
        } catch (Exception ex) {
            sumUpPromise.reject(ex);
            sumUpPromise = null;
        }
    }

    @ReactMethod
    public void preferences(Promise promise) {
        sumUpPromise = promise;
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
                            sumUpPromise.resolve(map);
                        } else {
                            sumUpPromise.reject(extra.getString(SumUpAPI.Response.RESULT_CODE), extra.getString(SumUpAPI.Response.MESSAGE));
                        }
                    }
                    break;

                case REQUEST_CODE_PAYMENT:
                    if (data != null) {
                        Bundle extra = data.getExtras();
                        if (sumUpPromise != null) {
                            if (extra.getInt(SumUpAPI.Response.RESULT_CODE) == TRANSACTION_SUCCESSFUL) {
                                WritableMap map = Arguments.createMap();
                                map.putBoolean("success", true);
                                map.putString("transactionCode", extra.getString(SumUpAPI.Response.TX_CODE));
                                sumUpPromise.resolve(map);
                            }else
                                sumUpPromise.reject(extra.getString(SumUpAPI.Response.RESULT_CODE), extra.getString(SumUpAPI.Response.MESSAGE));
                        }
                    }
                    break;
                case REQUEST_CODE_PAYMENT_SETTINGS:
                    WritableMap map = Arguments.createMap();
                    map.putBoolean("success", true);
                    sumUpPromise.resolve(map);
                    break;
                default:
                    break;
            }
        }

    };
}

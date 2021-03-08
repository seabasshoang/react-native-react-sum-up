package com.ami3go.sumupinterface;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.sumup.merchant.reader.api.SumUpAPI;
import com.sumup.merchant.reader.api.SumUpLogin;
import com.sumup.merchant.reader.api.SumUpPayment;
import com.sumup.merchant.reader.api.SumUpState;
import com.sumup.merchant.reader.models.TransactionInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReactSumUpModule extends ReactContextBaseJavaModule {

    private static final int REQUEST_CODE_LOGIN = 1;
    private static final int REQUEST_CODE_PAYMENT = 2;
    private static final int REQUEST_CODE_PAYMENT_SETTINGS = 3;
    private static final int TRANSACTION_SUCCESSFUL = 1;

    private static final String SMPCurrencyCodeBGN = "SMPCurrencyCodeBGN";
    private static final String SMPCurrencyCodeBRL = "SMPCurrencyCodeBRL";
    private static final String SMPCurrencyCodeCHF = "SMPCurrencyCodeCHF";
    private static final String SMPCurrencyCodeCLP = "SMPCurrencyCodeCLP";
    private static final String SMPCurrencyCodeCZK = "SMPCurrencyCodeCZK";
    private static final String SMPCurrencyCodeDKK = "SMPCurrencyCodeDKK";
    private static final String SMPCurrencyCodeEUR = "SMPCurrencyCodeEUR";
    private static final String SMPCurrencyCodeGBP = "SMPCurrencyCodeGBP";
    private static final String SMPCurrencyCodeHUF = "SMPCurrencyCodeHUF";
    private static final String SMPCurrencyCodeNOK = "SMPCurrencyCodeNOK";
    private static final String SMPCurrencyCodePLN = "SMPCurrencyCodePLN";
    private static final String SMPCurrencyCodeRON = "SMPCurrencyCodeRON";
    private static final String SMPCurrencyCodeSEK = "SMPCurrencyCodeSEK";
    private static final String SMPCurrencyCodeUSD = "SMPCurrencyCodeUSD";

    private Promise _sumUpPromise;

    public ReactSumUpModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(mActivityEventListener);
    }

    public static void initSumUp(Context context) {
        SumUpState.init(context);
    }

    @Override
    public String getName() {
        return "SumUpBridge";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("SMPCurrencyCodeBGN", SMPCurrencyCodeBGN);
        constants.put("SMPCurrencyCodeBRL", SMPCurrencyCodeBRL);
        constants.put("SMPCurrencyCodeCHF", SMPCurrencyCodeCHF);
        constants.put("SMPCurrencyCodeCLP", SMPCurrencyCodeCLP);
        constants.put("SMPCurrencyCodeCZK", SMPCurrencyCodeCZK);
        constants.put("SMPCurrencyCodeDKK", SMPCurrencyCodeDKK);
        constants.put("SMPCurrencyCodeEUR", SMPCurrencyCodeEUR);
        constants.put("SMPCurrencyCodeGBP", SMPCurrencyCodeGBP);
        constants.put("SMPCurrencyCodeHUF", SMPCurrencyCodeHUF);
        constants.put("SMPCurrencyCodeNOK", SMPCurrencyCodeNOK);
        constants.put("SMPCurrencyCodePLN", SMPCurrencyCodePLN);
        constants.put("SMPCurrencyCodeRON", SMPCurrencyCodeRON);
        constants.put("SMPCurrencyCodeSEK", SMPCurrencyCodeSEK);
        constants.put("SMPCurrencyCodeUSD", SMPCurrencyCodeUSD);
        return constants;
    }

    @ReactMethod
    public void authenticate(String affiliateKey, Promise promise) {
        _sumUpPromise = promise;
        SumUpLogin sumupLogin = SumUpLogin.builder(affiliateKey).build();
        SumUpAPI.openLoginActivity(getCurrentActivity(), sumupLogin, REQUEST_CODE_LOGIN);
    }

    @ReactMethod
    public void authenticateWithToken(String affiliateKey, String token, Promise promise) {
        _sumUpPromise = promise;
        SumUpLogin sumupLogin = SumUpLogin.builder(affiliateKey).accessToken(token).build();
        SumUpAPI.openLoginActivity(getCurrentActivity(), sumupLogin, REQUEST_CODE_LOGIN);
    }

    @ReactMethod
    public void prepareForCheckout(Promise promise) {
        _sumUpPromise = promise;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                SumUpAPI.prepareForCheckout();
                _sumUpPromise.resolve(true);
            }
        });
    }

    @ReactMethod
    public void logout(Promise promise) {
        _sumUpPromise = promise;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                SumUpAPI.logout();
                _sumUpPromise.resolve(true);
            }
        });
    }

    private SumUpPayment.Currency getCurrency(String currency) {
        switch (currency) {
            case SMPCurrencyCodeBGN: return SumUpPayment.Currency.BGN;
            case SMPCurrencyCodeBRL: return SumUpPayment.Currency.BRL;
            case SMPCurrencyCodeCHF: return SumUpPayment.Currency.CHF;
            case SMPCurrencyCodeCLP: return SumUpPayment.Currency.CLP;
            case SMPCurrencyCodeCZK: return SumUpPayment.Currency.CZK;
            case SMPCurrencyCodeDKK: return SumUpPayment.Currency.DKK;
            case SMPCurrencyCodeEUR: return SumUpPayment.Currency.EUR;
            case SMPCurrencyCodeGBP: return SumUpPayment.Currency.GBP;
            case SMPCurrencyCodeHUF: return SumUpPayment.Currency.HUF;
            case SMPCurrencyCodeNOK: return SumUpPayment.Currency.NOK;
            case SMPCurrencyCodePLN: return SumUpPayment.Currency.PLN;
            case SMPCurrencyCodeRON: return SumUpPayment.Currency.RON;
            case SMPCurrencyCodeSEK: return SumUpPayment.Currency.SEK;
            default: case SMPCurrencyCodeUSD: return SumUpPayment.Currency.USD;
        }
    }

    @ReactMethod
    public void checkout(ReadableMap request, Promise promise) {
        _sumUpPromise = promise;
        try {
            String foreignTransactionId = UUID.randomUUID().toString();
            if (request.getString("foreignTransactionId") != null) {
                foreignTransactionId = request.getString("foreignTransactionId");
            }

            SumUpPayment.Currency currencyCode = this.getCurrency(request.getString("currencyCode"));
            SumUpPayment payment = SumUpPayment.builder()
                    .total(new BigDecimal(request.getDouble("totalAmount")).setScale(2, RoundingMode.HALF_EVEN))
                    .currency(currencyCode)
                    .title(request.getString("title"))
                    .foreignTransactionId(foreignTransactionId)
                    .skipSuccessScreen()
                    .build();
            SumUpAPI.checkout(getCurrentActivity(), payment, REQUEST_CODE_PAYMENT);
        } catch (Exception ex) {
            _sumUpPromise.reject(ex);
            _sumUpPromise = null;
        }
    }

    @ReactMethod
    public void preferences(Promise promise) {
        _sumUpPromise = promise;
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

                            _sumUpPromise.resolve(map);
                        } else {
                            _sumUpPromise.reject(extra.getString(SumUpAPI.Response.RESULT_CODE), extra.getString(SumUpAPI.Response.MESSAGE));
                        }
                    }
                    break;

                case REQUEST_CODE_PAYMENT:
                    if (data != null) {
                        Bundle extra = data.getExtras();
                        if (_sumUpPromise != null) {
                            if (extra.getInt(SumUpAPI.Response.RESULT_CODE) == TRANSACTION_SUCCESSFUL) {
                                TransactionInfo transactionInfo = extra.getParcelable(SumUpAPI.Response.TX_INFO);

                                WritableMap map = Arguments.createMap();
                                map.putBoolean("success", true);
                                map.putInt("resultCode", extra.getInt(SumUpAPI.Response.RESULT_CODE));
                                map.putString("message", extra.getString(SumUpAPI.Response.MESSAGE));
                                map.putString("transactionCode", extra.getString(SumUpAPI.Response.TX_CODE));
                                map.putString("cardType", transactionInfo.getCard().getType());
                                map.putString("cardLast4Digits", transactionInfo.getCard().getLast4Digits());
                                map.putInt("installments", transactionInfo.getInstallments());

                                WritableMap additionalInfo = Arguments.createMap();
                                map.putMap("additionalInfo", additionalInfo);

                                _sumUpPromise.resolve(map);
                            }else
                                _sumUpPromise.reject(extra.getString(SumUpAPI.Response.RESULT_CODE), extra.getString(SumUpAPI.Response.MESSAGE));
                        }
                    }
                    break;
                case REQUEST_CODE_PAYMENT_SETTINGS:
                    WritableMap map = Arguments.createMap();
                    map.putBoolean("success", true);
                    _sumUpPromise.resolve(map);
                    break;
                default:
                    break;
            }
        }

    };
}
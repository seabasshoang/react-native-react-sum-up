import { NativeModules, Platform } from 'react-native';

const { SumUpBridge } = NativeModules;

export enum CurrencyCodes
{
    BGN = SumUpBridge.SMPCurrencyCodeBGN,
    BRL = SumUpBridge.SMPCurrencyCodeBRL,
    CHF = SumUpBridge.SMPCurrencyCodeCHF,
    CLP = (Platform.OS === 'android') ? SumUpBridge.SMPCurrencyCodeCLP : null, // iOS SDK version currently doesn't supports this currency
    CZK = SumUpBridge.SMPCurrencyCodeCZK,
    DKK = SumUpBridge.SMPCurrencyCodeDKK,
    EUR = SumUpBridge.SMPCurrencyCodeEUR,
    GBP = SumUpBridge.SMPCurrencyCodeGBP,
    HUF = SumUpBridge.SMPCurrencyCodeHUF,
    NOK = SumUpBridge.SMPCurrencyCodeNOK,
    PLN = SumUpBridge.SMPCurrencyCodePLN,
    RON = SumUpBridge.SMPCurrencyCodeRON,
    SEK = SumUpBridge.SMPCurrencyCodeSEK,
    USD = SumUpBridge.SMPCurrencyCodeUSD,
}

interface LoginResult
{
    isLoggedIn: boolean
}

interface SumUpResult
{
    success: boolean
}

export interface CheckoutResult extends SumUpResult
{
    resultCode: number,
    message: string,
    transactionCode: string,
    cardType: string,
    cardLast4Digits: string,
    installments: number,
    additionalInfo: any
}

class SumUpSDK
{
    private static _apiKey: string = '';

    public static init(key: string)
    {
        SumUpSDK._apiKey = key;

        if (Platform.OS === 'ios')
            SumUpBridge.setup(key);
    }

    public authenticate(): Promise<SumUpResult>
    {
        return (Platform.OS === 'ios') ? SumUpBridge.authenticate() : SumUpBridge.authenticate(SumUpSDK._apiKey);
    }

    public authenticateWithToken(token): Promise<SumUpResult>
    {
        return (Platform.OS === 'ios') ? SumUpBridge.authenticateWithToken(token) : SumUpBridge.authenticateWithToken(SumUpSDK._apiKey, token);
    }

    public isLoggedIn(): Promise<LoginResult>
    {
        return SumUpBridge.isLoggedIn();
    }

    public logout(): Promise<boolean>
    {
        return SumUpBridge.logout();
    }

    public prepareForCheckout(): Promise<boolean>
    {
        return SumUpBridge.prepareForCheckout();
    }

    public checkout(
        title: string,
        totalAmount: number,
        currencyCode = CurrencyCodes.GBP,
        foreignTransactionId = "",
        token: string = null): Promise<CheckoutResult>
    {
        const request = {
            'title': title,
            'totalAmount': totalAmount,
            'currencyCode': currencyCode,
            "foreignTransactionId": foreignTransactionId
        };

        return this.isLoggedIn().then(async (result) =>
        {
            if (!result.isLoggedIn)
            {
                if (!token)
                    throw new Error('Not logged in. Call "authenticate" or pass the token to this method');

                const authResult = await this.authenticateWithToken(token);

                if (!authResult.success)
                    throw new Error(`Error while authenticating:${JSON.stringify(authResult)}`);
            }

            return SumUpBridge.checkout(request);
        });
    };

    public preferences(): Promise<SumUpResult>
    {
        return SumUpBridge.preferences();
    }
};

export default SumUpSDK;

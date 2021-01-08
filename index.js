import { NativeModules, Platform } from 'react-native';

const { SumUpBridge } = NativeModules;

const SumUpSDK = {
    apiKey: '',

    paymentOptionAny: (Platform.OS === 'ios') ? SumUpBridge.SMPPaymentOptionAny : null,
    paymentOptionCardReader: (Platform.OS === 'ios') ? SumUpBridge.SMPPaymentOptionCardReader : null,
    paymentOptionMobilePayment: (Platform.OS === 'ios') ? SumUpBridge.SMPPaymentOptionMobilePayment : null,

    SMPCurrencyCodeBGN: SumUpBridge.SMPCurrencyCodeBGN,
    SMPCurrencyCodeBRL: SumUpBridge.SMPCurrencyCodeBRL,
    SMPCurrencyCodeCHF: SumUpBridge.SMPCurrencyCodeCHF,
    SMPCurrencyCodeCLP: (Platform.OS === 'android') ? SumUpBridge.SMPCurrencyCodeCLP : null, // iOS SDK version currently doesn't supports this currency
    SMPCurrencyCodeCZK: SumUpBridge.SMPCurrencyCodeCZK,
    SMPCurrencyCodeDKK: SumUpBridge.SMPCurrencyCodeDKK,
    SMPCurrencyCodeEUR: SumUpBridge.SMPCurrencyCodeEUR,
    SMPCurrencyCodeGBP: SumUpBridge.SMPCurrencyCodeGBP,
    SMPCurrencyCodeHUF: SumUpBridge.SMPCurrencyCodeHUF,
    SMPCurrencyCodeNOK: SumUpBridge.SMPCurrencyCodeNOK,
    SMPCurrencyCodePLN: SumUpBridge.SMPCurrencyCodePLN,
    SMPCurrencyCodeRON: SumUpBridge.SMPCurrencyCodeRON,
    SMPCurrencyCodeSEK: SumUpBridge.SMPCurrencyCodeSEK,
    SMPCurrencyCodeUSD: SumUpBridge.SMPCurrencyCodeUSD,

    setup(key)
    {
        this.apiKey = key;
        if (Platform.OS === 'ios')
        {
            SumUpBridge.setup(key);
        }
    },

    authenticate()
    {
        return (Platform.OS === 'ios') ? SumUpBridge.authenticate() : SumUpBridge.authenticate(this.apiKey);
    },

    authenticateWithToken(token)
    {
        return (Platform.OS === 'ios') ? SumUpBridge.authenticateWithToken(token) : SumUpBridge.authenticateWithToken(this.apiKey, token);
    },

    isLoggedIn()
    {
        return SumUpBridge.isLoggedIn();
    },

    logout()
    {
        return SumUpBridge.logout();
    },

    prepareForCheckout()
    {
        return SumUpBridge.prepareForCheckout();
    },

    checkout(title, totalAmount, currencyCode = SMPCurrencyCodeGBP, foreignTransactionId = "", token = null)
    {
        const request = {
            'title': title,
            'totalAmount': totalAmount,
            'currencyCode': currencyCode,
            "foreignTransactionId": foreignTransactionId
        };

        return this.isLoggedIn().then((result) =>
        {
            if (!result.isLoggedIn)
            {
                if (!token)
                    throw new Error('Not logged in. Call "authenticate" or pass the token to this method');

                const authWithToken = new Promise(async resolve =>
                {
                    await this.authenticateWithToken(token);
                    resolve();
                });

                return authWithToken.then(() => SumUpBridge.checkout(request));
            }

            return SumUpBridge.checkout(request);
        });
    },

    preferences()
    {
        return SumUpBridge.preferences();
    }
};

export default SumUpSDK;

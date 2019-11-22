import { NativeModules, Platform } from 'react-native';

const { SumUpBridge } = NativeModules;

const SumUpSDK = {

    setApiKey(apikey) {
        if(Platform.OS === 'ios'){
            return SumUpBridge.setupAPIKey(apikey)
        }
    },

    showLoginViewController(apikey=""){
        if(Platform.OS === 'ios'){
            SumUpBridge.presentLoginFromViewController()
        }else {
            SumUpBridge.presentLoginFromViewController(apikey)
        }
    },

    loginWithToken(token, apikey="") {
        if(Platform.OS === 'ios'){
        return SumUpBridge.loginToSumUpWithToken(token)
        }else{
            return SumUpBridge.loginToSumUpWithToken(apikey,token)
        }
    },

    checkIsLoggedIn(){
        return SumUpBridge.isLoggedIn()
    },

    logout() {
        return SumUpBridge.logout()
    },

    checkout(title, sum, currencyCode, skip="false",foreignTrID="") {
        const request = {
            'title' : title,
            'totalAmount' : sum,
            'currencyCode' : currencyCode,
            'skipScreenOptions' : skip,
            "foreignID" : foreignTrID
        }
        return SumUpBridge.paymentCheckout(request)
    }
}

export default SumUpSDK;

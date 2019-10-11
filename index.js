import { NativeModules } from 'react-native';

const { SumUpBridge } = NativeModules;

const SumUpSDK = {

    setApiKey(apikey) {
        return SumUpBridge.setupAPIKey(apikey)
    },

    showLoginViewController(){
            return SumUpBridge.presentLoginFromViewController()
    },

    loginWithToken(token) {
        return SumUpBridge.loginToSumUpWithToken(token)
    },

    checkIsLoggedIn(){
        return SumUpBridge.isLoggedIn()
    },

    logout() {
        return SumUpBridge.logout()
    },

    checkout(title, sum, skip="false",foreignTrID) {
        const request = {
            'title' : title,
            'totalAmount' : sum,
            'skipScreenOptions' : skip,
            "foreignID" : foreignTrID
        }
        return SumUpBridge.paymentCheckout(request)
    }
}

export default SumUpSDK;

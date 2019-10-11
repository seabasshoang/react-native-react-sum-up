import { NativeModules } from 'react-native';

const { ReactSumUp } = NativeModules;

const SumUpSDK = {

    setApiKey(apikey) {
        return ReactSumUp.setupAPIKey(apikey)
    },

    showLoginViewController(){
            return ReactSumUp.presentLoginFromViewController()
    },

    loginWithToken(token) {
        return ReactSumUp.loginToSumUpWithToken(token)
    },

    checkIsLoggedIn(){
        return ReactSumUp.isLoggedIn()
    },

    logout() {
        return ReactSumUp.logout()
    },

    checkout(title, sum, skip="false",foreignTrID) {
        const request = {
            'title' : title,
            'totalAmount' : sum,
            'skipScreenOptions' : skip,
            "foreignID" : foreignTrID
        }
        return ReactSumUp.paymentCheckout(request)
    }
}

export default SumUpSDK;

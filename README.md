# react-native-sumup-interface

## Getting started

1. `$ npm install react-native-sumup-interface --save`
2. `$ react-native link react-native-sumup-interface`

### Installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-sumup-interface` and add `ReactSumUp.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libReactSumUp.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

> Please note that iOS is untested. Please feel free to contribute to the project if updates are required

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.ami3go.sumupinterface.ReactSumUpModule;` to the imports at the top of the file
  - In the `onCreate()` method, add `ReactSumUpModule.initSumUp(this);` to the end
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-sumup-interface'
  	project(':react-native-sumup-interface').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-sumup-interface/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
    ```
    compile project(':react-native-sumup-interface')
  	```


## Usage
```typescript
import ReactSumUp from 'react-native-sumup-interface';

SumUpSDK.init('YOUR_API_KEY');

const sumUpSdk = new SumUpSDK();
```

To authenticate with a token (see https://developer.sumup.com/rest-api/#section/Authentication)...
```typescript
await sumUpSdk.authenticateWithToken('YOUR_TOKEN_HERE');
```

To authenticate with SumUp credentials...
```typescript
await sumUpSdk.authenticate();
```

To wake up the device...
```typescript
await sumUpSdk.prepareForCheckout();
```

To begin the checkout process...
```typescript
import { CurrencyCodes } from 'react-native-sumup-interface';

await sumUpSdk.checkout(
    'ABC', /* the transaction title */
    1.23, /* the transaction amount */);
// OPTIONAL FIELDS:
// currencyCode = CurrencyCodes.GBP (defaulted to GBP)
// foreignTransactionId = '' (foreign transaction ID, must be unique)
// token = null (authentication token. Only required if authentication hasn't already been done)
```

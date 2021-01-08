# @ami3goltd/react-native-sumup-interface

## Getting started

`$ npm install @ami3goltd/react-native-sumup-interface --save`

### Mostly automatic installation

`$ react-native link @ami3goltd/react-native-sumup-interface`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `@ami3goltd/react-native-sumup-interface` and add `ReactSumUp.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libReactSumUp.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.reactlibrary.ReactSumUpPackage;` to the imports at the top of the file
  - Add `new ReactSumUpPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':@ami3goltd/react-native-sumup-interface'
  	project(':@ami3goltd/react-native-sumup-interface').projectDir = new File(rootProject.projectDir, 	'../node_modules/@ami3goltd/react-native-sumup-interface/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':@ami3goltd/react-native-sumup-interface')
  	```


## Usage
```javascript
import ReactSumUp from '@ami3goltd/react-native-sumup-interface';

// TODO: What to do with the module?
SumUpSDK;
```

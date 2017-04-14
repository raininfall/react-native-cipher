
# react-native-cipher

## Getting started

`$ npm install react-native-cipher --save`

### Mostly automatic installation

`$ react-native link react-native-cipher`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-cipher` and add `RNCipher.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNCipher.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import net.power51.react.RNCipherPackage;` to the imports at the top of the file
  - Add `new RNCipherPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-cipher'
  	project(':react-native-cipher').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-cipher/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-cipher')
  	```

#### Windows
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `RNCipher.sln` in `node_modules/react-native-cipher/windows/RNCipher.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Com.Reactlibrary.RNCipher;` to the usings at the top of the file
  - Add `new RNCipherPackage()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import RNCipher from 'react-native-cipher';

// TODO: What to do with the module?
RNCipher;
```
  
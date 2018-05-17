
# react-native-google-appauth

## Getting started

`$ npm install react-native-google-appauth --save`

### Mostly automatic installation

`$ react-native link react-native-google-appauth`

### Manual installation


#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.bidchat.reactnative.appauth.RNGoogleAppauthPackage;` to the imports at the top of the file
  - Add `new RNGoogleAppauthPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-google-appauth'
  	project(':react-native-google-appauth').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-google-appauth/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-google-appauth')
  	```


## Usage
```javascript
import RNGoogleAppauth from 'react-native-google-appauth';

// TODO: What to do with the module?

processGoogleSignIn = async () => {
  try {
      const gtt = await GoogleAppauth.signIn();
      console.log('Success response', JSON.parse(gtt));
    } catch (err) {
      console.log('ERROR ', err);
    }
}

.....

render() {
  return (
    .....
    <Button
      onPress={this.processGoogleSignIn}
      title="SignIn"
      color="#841584"
      accessibilityLabel="Click Me to signIn"
    />
  )
}

```
  
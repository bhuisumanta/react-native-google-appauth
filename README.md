
# react-native-google-appauth

## Getting started

`$ npm install react-native-google-appauth --save`

### Mostly automatic installation

`$ react-native link react-native-google-appauth`

Add this in your `android/app/src/main/AndroidManifest.xml`

```
<activity android:name="com.bidchat.reactnative.appauth.NewMainActivity"
    android:theme = "@android:style/Theme.Translucent.NoTitleBar"
    android:launchMode="singleTask">
    <intent-filter>
        <action android:name="com.google.codelabs.appauth.HANDLE_AUTHORIZATION_RESPONSE"/>
        <category android:name="android.intent.category.DEFAULT"/>
    </intent-filter>
</activity>

<activity android:name="net.openid.appauth.RedirectUriReceiverActivity">
    <intent-filter>
        <action android:name="android.intent.action.VIEW"/>
        <category android:name="android.intent.category.DEFAULT"/>
        <category android:name="android.intent.category.BROWSABLE"/>
        <data android:scheme="com.bidchat.reactnative.appauth"/>
    </intent-filter>
</activity>
```

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
4. Add this in your `android/app/src/main/AndroidManifest.xml`
```
<activity android:name="com.bidchat.reactnative.appauth.NewMainActivity"
    android:theme = "@android:style/Theme.Translucent.NoTitleBar"
    android:launchMode="singleTask">
    <intent-filter>
        <action android:name="com.google.codelabs.appauth.HANDLE_AUTHORIZATION_RESPONSE"/>
        <category android:name="android.intent.category.DEFAULT"/>
    </intent-filter>
</activity>

<activity android:name="net.openid.appauth.RedirectUriReceiverActivity">
    <intent-filter>
        <action android:name="android.intent.action.VIEW"/>
        <category android:name="android.intent.category.DEFAULT"/>
        <category android:name="android.intent.category.BROWSABLE"/>
        <data android:scheme="com.bidchat.reactnative.appauth"/>
    </intent-filter>
</activity>
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
  

# react-native-google-appauth
This is a react native library for `Android` to integrate google sign in with youtube brand account using AppAuth

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
import { configure, signIn, signOut } from 'react-native-google-appauth';

componentDidMount() {
  const authInstance = configure({
    redirectUrl: "com.bidchat.reactnative.appauth:/oauth2callback",
    clientId: "[YOUR_CLIENT_ID]", // Generate from https://console.developers.google.com/ if you don't have
    scopes: ["https://www.googleapis.com/auth/youtube.readonly", "openid", "email", "profile"],
    additionalParameters: {} // Comming soon
  });
  authInstance.then((data) => {
    console.log("User LoggedIn", data);
    if (data && (typeof data === "object") && data.id_token) {
      // this.setState({ signedIn: true, user: data });
      // DO SOMETHING WITH USER INFO
    } else {
      // USER NOT LOGGED IN OR SESSION EXPIRED
    }
  })
  .catch((err) => {
    console.log("LoggedInError", err);
  });
}

handleLogin = async () => {
  try {
    const data = await signIn();
    console.log('Login success ----', data);
    // if (data && (typeof data === "object") && data.id_token) {
    //   this.setState({ signedIn: true, user: data });
    // }

    // DO SOMETHING WITH USER INFO
  } catch (err) {
    console.log('ERROR ----', err);
  }
}

handleLogOut = async () => {
  const logout = await signOut();
  if (logout && (typeof logout === "object") && logout.status) {
    // this.setState({ signedIn: false, user: {} });
    // USER SUCCESSFULLY LOGGEDOUT
  }
}

.....

render() {
  return (
    .....
    <Button
      onPress={this.handleLogin}
      title="Sign in by Google"
      color="#841584"
      accessibilityLabel="Click Me to login"
    />
  )
}

```

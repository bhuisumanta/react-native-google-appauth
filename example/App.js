/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  Platform,
  StyleSheet,
  Text,
  View,
  Button
} from 'react-native';
import GoogleAppauth, {signIn, signOut} from 'react-native-google-appauth';

const instructions = Platform.select({
  ios: 'Press Cmd+R to reload,\n' +
    'Cmd+D or shake for dev menu',
  android: 'Double tap R on your keyboard to reload,\n' +
    'Shake or press menu button for dev menu',
});

type Props = {};
export default class App extends Component<Props> {

  componentDidMount(){
    const authf = GoogleAppauth.configure({
      redirectUrl: "com.bidchat.reactnative.appauth:/oauth2callback",
      clientId: "455163333963-lrh581v65qbfftssu0pitb57fc4dj3mi.apps.googleusercontent.com",
      scopes: ["https://www.googleapis.com/auth/youtube.readonly", "openid", "email", "profile"],
      additionalParameters: {}
    });
    authf.then(function(data){
      console.log(data);
    })
    .catch(function(err){
      console.log(err);
    });
  }
  getName = async () => {
    try {
      const gtt = await signIn();
      console.log('console from reat-native----', JSON.parse(gtt));
    } catch (err) {
      console.log('ERROR from reat-native----', err);
    }
  }
  render() {
    return (
      <View style={styles.container}>
        <Text style={[styles.welcome, {fontWeight: 'normal'}]}>
          Welcome to React Native!
        </Text>
        <Text style={[styles.welcome, {fontWeight: '300'}]}>
          Welcome to React Native!
        </Text>
        <Text style={[styles.welcome, {fontWeight: '400'}]}>
          Welcome to React Native!
        </Text>
        <Text style={[styles.welcome, {fontWeight: '500'}]}>
          Welcome to React Native!
        </Text>
        <Text style={[styles.welcome, {fontWeight: '700'}]}>
          Welcome to React Native!
        </Text>
        <Text style={[styles.welcome, {fontWeight: 'bold'}]}>
          Welcome to React Native!
        </Text>
        <Text style={styles.instructions}>
          To get started, edit App.js
        </Text>
        <Text style={styles.instructions}>
          {instructions}
        </Text>
        <Button
          onPress={this.getName}
          title="Click Me"
          color="#841584"
          accessibilityLabel="Click Me about this purple button"
        />
        <Button
          onPress={() => {
            signOut();
          }}
          title="Sign Out"
          color="#841584"
          accessibilityLabel="Click Me about this purple button"
        />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

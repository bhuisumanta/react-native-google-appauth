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
import { configure, signIn, signOut } from 'react-native-google-appauth';

const instructions = Platform.select({
  ios: 'Press Cmd+R to reload,\n' +
    'Cmd+D or shake for dev menu',
  android: 'Double tap R on your keyboard to reload,\n' +
    'Shake or press menu button for dev menu',
});

type Props = {};
export default class App extends Component<Props> {
  state = {
    user: {},
    signedIn: false
  }

  componentDidMount() {
    const authf = configure({
      redirectUrl: "com.bidchat.reactnative.appauth:/oauth2callback",
      clientId: "455163333963-lrh581v65qbfftssu0pitb57fc4dj3mi.apps.googleusercontent.com",
      scopes: ["https://www.googleapis.com/auth/youtube.readonly", "openid", "email", "profile"],
      additionalParameters: {}
    });
    authf.then((data) => {
      console.log("reactNative---- LoggedIn", data);
      if (data && (typeof data === "object") && data.id_token) {
        this.setState({ signedIn: true, user: data });
      }
    })
      .catch((err) => {
        console.log("reactNative---- LoggedInError", err);
      });
  }
  handleLogin = async () => {
    try {
      const data = await signIn();
      console.log('console from reat-native----', data);
      if (data && (typeof data === "object") && data.id_token) {
        this.setState({ signedIn: true, user: data });
      }
    } catch (err) {
      console.log('ERROR from reat-native----', err);
    }
  }

  handleLogOut = async () => {
    const logout = await signOut();
    if (logout && (typeof logout === "object") && logout.status) {
      this.setState({ signedIn: false, user: {} });
    }
  }

  render() {
    return (
      <View style={styles.container}>
        {this.state.signedIn ? (
          <View>
            <Text style={[styles.welcome, { fontWeight: 'bold' }]}>
              {this.state.user.name}
            </Text>
            <Text style={[styles.welcome, { fontWeight: 'normal' }]}>
              {this.state.user.email}
            </Text>
            <Button
              onPress={this.handleLogOut}
              title="Sign Out"
              color="#841584"
              accessibilityLabel="Click Me about this purple button"
            />
          </View>
        ) : (
            <Button
              onPress={this.handleLogin}
              title="Sign In"
              color="#841584"
              accessibilityLabel="Click Me about this purple button"
            />
          )}

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

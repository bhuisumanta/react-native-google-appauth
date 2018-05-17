
package com.bidchat.reactnative.appauth;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import android.content.Intent;

import net.openid.appauth.AuthState;

public class RNGoogleAppauthModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  private static final String SHARED_PREFERENCES_NAME = "AuthStatePreference";
  private static final String AUTH_STATE = "AUTH_STATE";
  private static final String USED_INTENT = "USED_INTENT";
  private static final String LOGIN_HINT = "login_hint";

  protected String mLoginHint;
  public static Promise promise;

  // state
  AuthState mAuthState;

  public RNGoogleAppauthModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNGoogleAppauth";
  }

  @ReactMethod
  public void signIn(final Promise promise) {
    try {
      Intent intent = new Intent(this.reactContext, NewMainActivity.class);
      this.reactContext.startActivity(intent);
//      promise.resolve(true);
      this.promise = promise;
    } catch (Exception ex) {
      promise.reject("ERR_UNEXPECTED_EXCEPTION", ex);
    }
  }

  public String getLoginHint(){
    return mLoginHint;
  }
}
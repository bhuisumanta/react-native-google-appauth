
package com.bidchat.reactnative.appauth;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import android.content.Intent;

import net.openid.appauth.AuthState;

public class RNGoogleAppauthModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  private static final String SHARED_PREFERENCES_NAME = "AuthStatePreference";
  private static final String AUTH_STATE = "AUTH_STATE";
  private static final String USED_INTENT = "USED_INTENT";
  private static final String LOGIN_HINT = "login_hint";

  public static AuthState authState;
  protected String mLoginHint;
  public static Promise promise;
  public static String redirectUrl;
  public static String clientId;
  public static ReadableArray scopes;
  public static ReadableMap additionalParameters;

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
  public void configure(final String redirectUrl, final String clientId, final ReadableArray scopes,
      final ReadableMap additionalParameters, final Promise promise) {
    try {
      RNGoogleAppauthModule.redirectUrl = redirectUrl;
      RNGoogleAppauthModule.clientId = clientId;
      RNGoogleAppauthModule.scopes = scopes;
      RNGoogleAppauthModule.additionalParameters = additionalParameters;
      RNGoogleAppauthModule.promise = promise;

      mAuthState = restoreAuthState();
      if (mAuthState != null && mAuthState.isAuthorized()) {
        NewMainActivity.doApiCall(mAuthState, null);
      } else {
        promise.resolve("SIGNED_OUT");
      }
    } catch (Exception ex) {
      promise.reject("ERR_UNEXPECTED_EXCEPTION", ex);
    }
  }

  @ReactMethod
  public void signIn(Promise promise) {
    try {
      Intent intent = new Intent(this.reactContext, NewMainActivity.class);
      this.reactContext.startActivity(intent);
    } catch (Exception ex) {
      promise.reject("ERR_UNEXPECTED_EXCEPTION", ex);
    }
  }

  private void clearAuthState() {
    getSharedPreferences(SHARED_PREFERENCES_NAME, reactContext.MODE_PRIVATE).edit().remove(AUTH_STATE).apply();
    RNGoogleAppauthModule.promise.resolve(true);
  }

  @Nullable
  private AuthState restoreAuthState() {
    String jsonString = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getString(AUTH_STATE, null);
    if (!TextUtils.isEmpty(jsonString)) {
      try {
        return AuthState.fromJson(jsonString);
      } catch (JSONException jsonException) {
        // should never happen
      }
    }
    return null;
  }

  @ReactMethod
  public void signOut(final Promise promise) {
    try {
      RNGoogleAppauthModule.promise = promise;
      clearAuthState();
    } catch (Exception ex) {
      promise.reject("ERR_UNEXPECTED_EXCEPTION", ex);
    }
  }

  public String getLoginHint() {
    return mLoginHint;
  }
}

package com.bidchat.reactnative.appauth;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RNGoogleAppauthModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public static final String SHARED_PREFERENCES_NAME = "AuthStatePreference";
  public static final String AUTH_STATE = "AUTH_STATE";
  public static final String USED_INTENT = "USED_INTENT";
  public static final String LOGIN_HINT = "login_hint";

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

      RNGoogleAppauthModule.authState = restoreAuthState();
      if (RNGoogleAppauthModule.authState != null && RNGoogleAppauthModule.authState.isAuthorized()) {
        Log.i("Loggedin user----", "true");
        doApiCall(RNGoogleAppauthModule.authState.getLastTokenResponse(), new AuthorizationService(reactContext));
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
      RNGoogleAppauthModule.promise = promise;
//      Intent intent = new Intent(this.reactContext, NewMainActivity.class);
//      this.reactContext.startActivity(intent);
      Activity activity = getCurrentActivity();
      if (activity != null) {
        Intent intent = new Intent(activity, NewMainActivity.class);
        activity.startActivity(intent);
      }else {
          promise.reject("ERR_UNEXPECTED_EXCEPTION", "no activity found");
      }
    } catch (Exception ex) {
      promise.reject("ERR_UNEXPECTED_EXCEPTION", ex);
    }
  }

  private void clearAuthState() {
    reactContext.getSharedPreferences(SHARED_PREFERENCES_NAME, reactContext.MODE_PRIVATE).edit().remove(AUTH_STATE)
        .apply();
    RNGoogleAppauthModule.promise.resolve("SIGNED_OUT");
  }

  @Nullable
  private AuthState restoreAuthState() {
    String jsonString = reactContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        .getString(AUTH_STATE, null);
    if (!TextUtils.isEmpty(jsonString)) {
      try {
        return AuthState.fromJson(jsonString);
      } catch (JSONException jsonException) {
        // should never happen
      }
    }
    return null;
  }

  public void doApiCall(final TokenResponse tokenResponse, final AuthorizationService service) {
    Log.i("doApiCall: ", "----called----");
    try {
      RNGoogleAppauthModule.authState.performActionWithFreshTokens(service, new AuthState.AuthStateAction() {
        @Override
        public void execute(@Nullable String accessToken, @Nullable String idToken,
            @Nullable AuthorizationException exception) {

          new AsyncTask<String, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(String... tokens) {
              Log.e("TEST API CALL", "doInBackground: " );
              OkHttpClient client = new OkHttpClient();
              Request request = new Request.Builder().url("https://www.googleapis.com/oauth2/v3/userinfo")
                  .addHeader("Authorization", String.format("Bearer %s", tokens[0])).build();

              try {
                Response response = client.newCall(request).execute();
//                Log.e("TEST API CALL", "doInBackground: "+response );
//                Log.e("TEST API CALL", "doInBackground: "+response.body() );
//                Log.e("TEST API CALL", "doInBackground: "+response.body().toString());
//                Log.e("TEST API CALL", "doInBackground: "+response.body().string());
                if (response.isSuccessful()){
                  String jsonBody = response.body().string();
//                  Log.i("test", String.format("User Info Response %s", jsonBody));
                  return new JSONObject(jsonBody);
                }else {
                  return null;
                }
              } catch (Exception exception) {
                Log.w("test", exception);
              }
              return null;
            }

            @Override
            protected void onPostExecute(JSONObject userInfo) {
              Log.e("TEST API CALL", "onPostExecute: "+userInfo );
              if (userInfo != null) {
                JSONObject jsonObject = new JSONObject();
                try {
                  jsonObject.put("token_type", tokenResponse.tokenType);
                  jsonObject.put("access_token", tokenResponse.accessToken);
                  jsonObject.put("expires_at", tokenResponse.accessTokenExpirationTime);
                  jsonObject.put("id_token", tokenResponse.idToken);
                  jsonObject.put("refresh_token", tokenResponse.refreshToken);
                  jsonObject.put("user_id", userInfo.optString("sub"));
                  jsonObject.put("name", userInfo.optString("name"));
                  jsonObject.put("given_name", userInfo.optString("given_name"));
                  jsonObject.put("family_name", userInfo.optString("family_name"));
                  jsonObject.put("picture", userInfo.optString("picture"));
                  jsonObject.put("email", userInfo.optString("email"));
                  jsonObject.put("email_verified", userInfo.optString("email_verified"));
                  jsonObject.put("locale", userInfo.optString("locale"));

                  Log.i("JSON---", jsonObject.toString());
                } catch (Exception excJson) {

                }
                if (RNGoogleAppauthModule.promise != null) {
                  RNGoogleAppauthModule.promise.resolve(jsonObject.toString());
                }
                if (service != null) {
                  service.dispose();
                }
              } else {
                if (RNGoogleAppauthModule.promise != null) {
                  RNGoogleAppauthModule.promise.resolve(tokenResponse.toJsonString());
                }
                if (service != null) {
                  service.dispose();
                }
              }
            }
          }.execute(accessToken);
        }
      });
    } catch (Exception exp) {
      Log.e("TEST API CALL", "Exception: "+exp.getMessage() );
      if (RNGoogleAppauthModule.promise != null) {
        RNGoogleAppauthModule.promise.resolve(tokenResponse.toJsonString());
      }
      if (service != null) {
        service.dispose();
      }
    }
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
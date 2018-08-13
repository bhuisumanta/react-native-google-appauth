
package com.bidchat.reactnative.appauth;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.ReactActivity;

import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.RestrictionsManager;
import android.net.Uri;
import android.os.AsyncTask;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewMainActivity extends ReactActivity {

  private static final String USED_INTENT = "USED_INTENT";
  private boolean activityOpened = false;
  private AuthorizationService service = null;
  private AuthorizationService authorizationService = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.e("Test", "onCreate: ");
    googleSignin();
  }

  @Override
  protected void onResume() {
    if(activityOpened == true) {
      Log.e("Test", "onResume FINISH: ");
      activityOpened = false;
      RNGoogleAppauthModule.promise.reject("ERR_UNEXPECTED_EXCEPTION",
              "Google authentication cancelled");
      finish();
      super.onResume();
    } else {
      Log.e("Test", "onResume RESUME: ");
      super.onResume();
      activityOpened = true;
    }
  }

  @Override
  public void onNewIntent(Intent intent) {
    activityOpened = false;
    super.onNewIntent(intent);
    Log.e("Test", "onNewIntent: ");
    if (intent != null) {
      String action = intent.getAction();
      Log.i("Back To APP", "onNewIntent: "+action);
      if(action != null) {
        switch (action) {
          case "com.google.codelabs.appauth.HANDLE_AUTHORIZATION_RESPONSE":
            if (!intent.hasExtra(USED_INTENT)) {
              handleAuthorizationResponse(intent);
              intent.putExtra(USED_INTENT, true);
            }
            break;
          default:
            // do nothing
        }
      }
    }
  }

  void googleSignin() {
    try {
      AuthorizationServiceConfiguration serviceConfiguration = new AuthorizationServiceConfiguration(
          Uri.parse("https://accounts.google.com/o/oauth2/v2/auth") /* auth endpoint */,
          Uri.parse("https://www.googleapis.com/oauth2/v4/token") /* token endpoint */
      );
      authorizationService = new AuthorizationService(NewMainActivity.this);
      String clientId = RNGoogleAppauthModule.clientId;
      Uri redirectUri = Uri.parse(RNGoogleAppauthModule.redirectUrl);
      AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(serviceConfiguration, clientId,
          AuthorizationRequest.RESPONSE_TYPE_CODE, redirectUri);
      builder.setScopes(arrayToString(RNGoogleAppauthModule.scopes));
      Map mapA = new HashMap();
      mapA.put("access_type", "offline");
      builder.setAdditionalParameters(mapA);

      // if (mMainActivity.getLoginHint() != null) {
      // Map loginHintMap = new HashMap<String, String>();
      // loginHintMap.put(LOGIN_HINT, mMainActivity.getLoginHint());
      // builder.setAdditionalParameters(loginHintMap);

      // Log.i(LOG_TAG, String.format("login_hint: %s",
      // mMainActivity.getLoginHint()));
      // }

      AuthorizationRequest request = builder.build();
      String action = "com.google.codelabs.appauth.HANDLE_AUTHORIZATION_RESPONSE";
      Intent postAuthorizationIntent = new Intent(action);
      PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), request.hashCode(),
          postAuthorizationIntent, 0);
      authorizationService.performAuthorizationRequest(request, pendingIntent);
    } catch (Exception exception) {
      if (RNGoogleAppauthModule.promise != null) {
        RNGoogleAppauthModule.promise.reject("ERR_UNEXPECTED_EXCEPTION", "Error with Google authentication : "+exception.getMessage());
      }
      finish();
    }
  }

  private String[] arrayToString(ReadableArray array) {
    String[] str = new String[array.size()];

    for(int i=0; i<array.size(); i++) {
      str[i] = array.getString(i);
    }

    return str;
    // StringBuilder strBuilder = new StringBuilder();
    // for (int i = 0; i < array.size(); i++) {
    //     if (i != 0) {
    //         strBuilder.append(' ');
    //     }
    //     strBuilder.append(array.getString(i));
    // }
    // return strBuilder.toString();
  }

  public void doApiCall(final TokenResponse tokenResponse, final AuthorizationService service) {
    try {
      RNGoogleAppauthModule.authState.performActionWithFreshTokens(service, new AuthState.AuthStateAction() {
        @Override
        public void execute(@Nullable String accessToken, @Nullable String idToken,
            @Nullable AuthorizationException exception) {
          new AsyncTask<String, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(String... tokens) {
              OkHttpClient client = new OkHttpClient();
              Request request = new Request.Builder().url("https://www.googleapis.com/oauth2/v3/userinfo")
                  .addHeader("Authorization", String.format("Bearer %s", tokens[0])).build();

              try {
                Response response = client.newCall(request).execute();
                String jsonBody = response.body().string();
                Log.i("test", String.format("User Info Response %s", jsonBody));
                return new JSONObject(jsonBody);
              } catch (Exception exception) {
                Log.w("test", exception);
              }
              return null;
            }

            @Override
            protected void onPostExecute(JSONObject userInfo) {
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
                } catch (Exception excJson) {

                }
                if (RNGoogleAppauthModule.promise != null) {
                  RNGoogleAppauthModule.promise.resolve(jsonObject.toString());
                }
                if (service != null) {
                  service.dispose();
                }
                finish();
              } else {
                if (RNGoogleAppauthModule.promise != null) {
                  RNGoogleAppauthModule.promise.resolve(tokenResponse.toJsonString());
                }
                if (service != null) {
                  service.dispose();
                }
                finish();
              }
            }
          }.execute(accessToken);
        }
      });
    } catch (Exception exp) {
      if (RNGoogleAppauthModule.promise != null) {
        RNGoogleAppauthModule.promise.resolve(tokenResponse.toJsonString());
      }
      if (service != null) {
        service.dispose();
      }
      finish();
    }
  }

  private void persistAuthState(@NonNull AuthState authState) {
    getSharedPreferences(RNGoogleAppauthModule.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
            .putString(RNGoogleAppauthModule.AUTH_STATE, authState.toJsonString())
            .commit();
  }

  private void handleAuthorizationResponse(@NonNull Intent intent) {
    AuthorizationResponse response = AuthorizationResponse.fromIntent(intent);
    AuthorizationException error = AuthorizationException.fromIntent(intent);
    RNGoogleAppauthModule.authState = new AuthState(response, error);
    if (response != null) {
      Log.i("Test", String.format("Handled Authorization Response %s ", RNGoogleAppauthModule.authState.toJsonString()));
      service = new AuthorizationService(this);
      service.performTokenRequest(response.createTokenExchangeRequest(),
          new AuthorizationService.TokenResponseCallback() {
            @Override
            public void onTokenRequestCompleted(@Nullable final TokenResponse tokenResponse,
                @Nullable AuthorizationException exception) {
              if (exception != null) {
                Log.w("Test", "Token Exchange failed", exception);
              } else {
                if (tokenResponse != null) {
                  RNGoogleAppauthModule.authState.update(tokenResponse, exception);
                  persistAuthState(RNGoogleAppauthModule.authState);
                  Log.i("xyz", tokenResponse.toJsonString());
                  Log.i("Test", String.format("Token Response [ Access Token: %s, ID Token: %s ]",
                      tokenResponse.accessToken, tokenResponse.refreshToken));

                  doApiCall(tokenResponse, service);
                } else {
                  if (RNGoogleAppauthModule.promise != null) {
                    RNGoogleAppauthModule.promise.reject("ERR_UNEXPECTED_EXCEPTION",
                        "Error with Google authentication");
                  }
                  if (service != null) {
                    service.dispose();
                  }
                  finish();
                }
              }
            }
          });
    }
  }

  @Override
  protected void onDestroy() {
    Log.e("Test", "onDestroy: ");
    if (service != null){
      service.dispose();
      service = null;
    }

    if (authorizationService != null){
      authorizationService.dispose();
    }
    super.onDestroy();
  }
}
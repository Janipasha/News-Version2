package in.thesoup.thesoup.Activities;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleBrowserClientRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import in.thesoup.thesoup.NetworkCalls.NetworkUtilsLogin;
import in.thesoup.thesoup.PreferencesFbAuth.PrefUtil;
import in.thesoup.thesoup.R;
import in.thesoup.thesoup.SoupContract;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.R.attr.data;

/**
 * Created by Jani on 24-05-2017.
 */

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private CallbackManager callbackmanager;
    private LoginButton loginButton;
    PrefUtil prefUtil;
    private String StoryId, activityId;
    private HashMap<String, String> params;
    private Intent intent1;
    private Tracker mTracker;
    private static final int RC_SIGN_IN = 007;
    private static final String TAGI = "handle request response";
    // private AnalyticsApplication application;
    private SharedPreferences pref;
    private Button fb, google;
    private SignInButton signInButton;
    public static final String TAG = "ServerAuthCodeActivity";
    private static final int RC_GET_AUTH_CODE = 9003;

    private GoogleApiClient mGoogleApiClient;
    private TextView mAuthCodeTextView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        if (TextUtils.isEmpty(pref.getString("auth_token", null))) {


            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(this);

            View decorView = getWindow().getDecorView();
// Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

            setContentView(R.layout.login);

            CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                    .setDefaultFontPath("fonts/OpenSans-Semibolditalic.ttf")
                    .setFontAttrId(R.attr.fontPath)
                    .build()
            );
            validateServerClientID();

            String serverClientId = getString(R.string.server_client_id);

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(new Scope(Scopes.PROFILE))
                    .requestEmail()
                    .requestIdToken(getString(R.string.server_client_id))
                    .build();


            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            signInButton = (SignInButton) findViewById(R.id.sign_in_button);
            //findViewById(R.id.sign_in_button).setOnClickListener(this);


            callbackmanager = CallbackManager.Factory.create();
            prefUtil = new PrefUtil(this);

            fb = (Button) findViewById(R.id.facebook);
            google = (Button) findViewById(R.id.google_button);

            loginButton = (LoginButton) findViewById(R.id.login_button);

            loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));

            loginButton.registerCallback(callbackmanager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {


                            String getScopes = loginResult.getAccessToken().getPermissions().toString();
                            Log.d("Scopes", getScopes);


                            System.out.println("Success");
                            Log.d("Acess Token", loginResult.getAccessToken().getToken().toString());


                            String accessToken = loginResult.getAccessToken().getToken();
                            prefUtil.saveAccessToken(accessToken);

                            GraphRequest request = GraphRequest.newMeRequest(
                                    loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject jsonobject, GraphResponse response) {

                                            Bundle facebookData = getFacebookData(jsonobject);


                                            params = new HashMap<>();
                                            params.put(SoupContract.SOCIAL_NAME, "fb");
                                            params.put(SoupContract.SOCIAL_TOKEN, prefUtil.getToken());
                                            params.put(SoupContract.SOCIAL_ID, prefUtil.getId());
                                            params.put("grantedScopes", prefUtil.getPermissions());
                                            params.put(SoupContract.FIREBASEID,prefUtil.getFirebaseID());
                                            params.put(SoupContract.FIRST_NAME, prefUtil.getFirstname());
                                            params.put(SoupContract.LAST_NAME, prefUtil.getLastname());
                                            params.put(SoupContract.EMAIL_ID, prefUtil.getEmail());
                                            params.put(SoupContract.AGE_MIN, prefUtil.getAgeMin());
                                            params.put(SoupContract.GENDER, prefUtil.getGender());
                                            params.put(SoupContract.AGE_MAX, prefUtil.getAgeMax());
                                            //params.put("dob",);//send dob as null for future
                                            params.put(SoupContract.IMAGE_URL, prefUtil.getPictureUrl());


                                            // Log.d("prefUtilemail",prefUtil.getEmail());

                                            for (String name : params.keySet()) {

                                                String key = name;


                                                String value = params.get(key);
                                                Log.d("param values", key + " " + value);


                                            }


                                            NetworkUtilsLogin loginRequest = new NetworkUtilsLogin(LoginActivity.this, params);
                                            loginRequest.loginvolleyRequestfromMain();


                                            if (response.getError() != null) {
                                                // handle error
                                                System.out.println("ERROR");
                                            } else {
                                                System.out.println("Success");

                                            }
                                        }

                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,first_name,last_name,email,gender,age_range");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }


                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onError(FacebookException error) {
                            Log.d("fbloginerror",": "+error.toString());

                        }

                    });
        } else {

            startActivityMD();
        }


    }

    /*@Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }*/

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        callbackmanager.onActivityResult(requestCode, resultCode, data);
        // super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            int statuscode = result.getStatus().getStatusCode();
            Log.d("statuscode", String.valueOf(statuscode));
            handleSignInResult(result);

            //Person person = Plus.PeopleApi.
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAGI, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            // Signed in successfully, show authenticated UI.


            if (result != null) {

                GoogleSignInAccount acct = result.getSignInAccount();


                String authCode = acct.getServerAuthCode();


                acct.getGrantedScopes();

                prefUtil.saveUserInfo(acct.getGivenName(), acct.getFamilyName(), acct.getEmail(), "", acct.getPhotoUrl().toString(), acct.getId(), "", "");
                prefUtil.saveAccessToken(acct.getIdToken());

                Log.d("acesstoken",": "+acct.getIdToken());

                params = new HashMap<>();
                params.put(SoupContract.SOCIAL_NAME, "gplus");
                params.put(SoupContract.SOCIAL_TOKEN, prefUtil.getToken());
                params.put(SoupContract.SOCIAL_ID, prefUtil.getId());
                params.put(SoupContract.FIRST_NAME, prefUtil.getFirstname());
                params.put(SoupContract.FIREBASEID,prefUtil.getFirebaseID());
                params.put(SoupContract.LAST_NAME, prefUtil.getLastname());
                params.put(SoupContract.EMAIL_ID, prefUtil.getEmail());
                params.put(SoupContract.AGE_MIN, prefUtil.getAgeMin());
                params.put(SoupContract.GENDER, prefUtil.getGender());
                params.put(SoupContract.AGE_MAX, prefUtil.getAgeMax());
                //params.put("dob",);//send dob as null for future
                params.put(SoupContract.IMAGE_URL, prefUtil.getPictureUrl());

                for (String name : params.keySet()) {

                    String key = name;


                    String value = params.get(key);
                    Log.d("param values", key + " " + value);


                }

                NetworkUtilsLogin loginRequest = new NetworkUtilsLogin(LoginActivity.this, params);
                loginRequest.loginvolleyRequestfromMain();


                Log.d("result", result.getSignInAccount().getGrantedScopes().toString());
                Log.d("email google", acct.getEmail() + "\n" + acct.getIdToken() + "\n" + acct.getDisplayName() + "\n");
            }

            //TODO login google thing
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void startActivityMD() {

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        finish();
        startActivity(intent);

    }

    private Bundle getFacebookData(JSONObject object) {
        Bundle bundle = new Bundle();

        try {
            String id = object.getString("id");
            URL profile_pic;
            try {
                profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString(SoupContract.IMAGE_URL, profile_pic.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString(SoupContract.SOCIAL_ID, id);
            if (object.has(SoupContract.FIRST_NAME))
                bundle.putString(SoupContract.FIRST_NAME, object.getString("first_name"));
            if (object.has(SoupContract.LAST_NAME))
                bundle.putString(SoupContract.LAST_NAME, object.getString("last_name"));
            if (object.has(SoupContract.EMAIL_ID))
                bundle.putString(SoupContract.EMAIL_ID, object.getString("email"));
            if (object.has(SoupContract.GENDER))
                bundle.putString(SoupContract.GENDER, object.getString("gender"));

            Log.d("Bundle", bundle.toString());

            String age_min = object.getJSONObject("age_range").getString("min");
            Log.d("age min1 ", age_min);

            JSONObject agerange = object.getJSONObject("age_range");
            String age_max = null;
            if (agerange.has("max")) {
                age_max = object.getJSONObject("age_range").getString("max");
            }

            //Log.d("age max",age_max);


            prefUtil.saveUserInfo(object.getString(SoupContract.FIRST_NAME),
                    object.getString(SoupContract.LAST_NAME),
                    object.getString("email"),
                    object.getString(SoupContract.GENDER),
                    profile_pic.toString(),
                    object.getString("id"),
                    age_min, age_max);


            //SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            //Log.d("Shared Preference ",pref.getString("email","") + " "+ pref.getString("first_name","")+" "+pref.getString("age_min"," "));

        } catch (Exception e) {
            Log.d("BUNDLE Exception : ", e.toString());
        }

        return bundle;
    }

    public void onClickSocial(View v) {
        if (v == fb) {
            loginButton.performClick();

        } else if (v == google) {

            signInButton.performClick();
            signIn();


        }
    }

    private void validateServerClientID() {
        String serverClientId = getString(R.string.server_client_id);
        String suffix = ".apps.googleusercontent.com";
        if (!serverClientId.trim().endsWith(suffix)) {
            String message = "Invalid server client ID in strings.xml, must end with " + suffix;

            Log.w(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    public void setup() {


    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}


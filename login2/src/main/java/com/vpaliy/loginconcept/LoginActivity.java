package com.vpaliy.loginconcept;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.mukesh.countrypicker.CountryPicker;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient
        .OnConnectionFailedListener, AuthFragment.UserLoginWithEmail {
    protected List<ImageView> sharedElements;
    protected CallbackManager mCallbackManager;
    protected FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private TwitterAuthClient mTwitterAuthClient;
    private final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        initGoogleLogin();
        if (preferences.getBoolean("login", false)) {
            try {
                startActivity(new Intent(this, Class.forName("com.cliplay.ui" +
                        ".activity.HomePage")));
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return;
        }
        setContentView(R.layout.activity_login);
        CountryPicker countryPicker =
                new CountryPicker.Builder().with(this)
                        .listener(country ->
                                Toast.makeText(LoginActivity.this, country.getDialCode(), Toast
                                        .LENGTH_SHORT)
                                        .show()).build();
        countryPicker.showDialog(getSupportFragmentManager());
        sharedElements = new ArrayList<>();
        ImageView logo = findViewById(R.id.logo);
        ImageView first = findViewById(R.id.first);
        ImageView second = findViewById(R.id.second);
        ImageView last = findViewById(R.id.last);
        sharedElements.add(logo);
        sharedElements.add(first);
        sharedElements.add(second);
        sharedElements.add(last);
        final AnimatedViewPager pager = findViewById(R.id.pager);
        final ImageView background = findViewById(R.id.scrolling_background);
        int[] screenSize = screenSize();
        for (ImageView element : sharedElements) {
            @ColorRes int color = element.getId() != R.id.logo ? R.color.white_transparent : R
                    .color.color_logo_log_in;
            DrawableCompat.setTint(element.getDrawable(), ContextCompat.getColor(this, color));
        }
        //load a very big image and resize it, so it fits our needs
        Glide.with(this).asBitmap()
                .load(R.drawable.table)
                .apply(new RequestOptions().override(screenSize[0] * 2, screenSize[1]))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap>
                            target, DataSource dataSource, boolean isFirstResource) {
                        background.setImageBitmap(resource);
                        background.post(() -> {
                            //we need to scroll to the very left edge of the image
                            //fire the scale animation
                            background.scrollTo(-background.getWidth() / 2, 0);
                            ObjectAnimator xAnimator = ObjectAnimator.ofFloat(background, View
                                    .SCALE_X, 4f, background.getScaleX());
                            ObjectAnimator yAnimator = ObjectAnimator.ofFloat(background, View
                                    .SCALE_Y, 4f, background.getScaleY());
                            AnimatorSet set = new AnimatorSet();
                            set.playTogether(xAnimator, yAnimator);
                            set.setDuration(getResources().getInteger(R.integer.duration));
                            set.start();
                        });
                        pager.post(() -> {
                            AuthAdapter adapter = new AuthAdapter(getSupportFragmentManager(),
                                    pager, background, sharedElements);
                            pager.setAdapter(adapter);
                        });
                        return true;
                    }
                })
                .into(new ImageViewTarget<Bitmap>(background) {
                    @Override
                    protected void setResource(Bitmap resource) {
                    }
                });
    }

    private int[] screenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return new int[]{size.x, size.y};
    }

    public void facebookClick(View v) {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(
                "email", "public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new
                FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "facebook:onError", error);
                    }
                });
    }

    public void googlePlusClick(View v) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        showProgressDialog();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        if (mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(this,
                    task -> handleFirebaseAuthResult(task, false));
        } else {
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, task -> handleFirebaseAuthResult(task, false));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null && !currentUser.isAnonymous()) {
            try {
                SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
                preferences.edit().putBoolean("login", true).apply();
                startActivity(new Intent(this, Class.forName("com.cliplay.ui" +
                        ".activity.HomePage")));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            finish();
        }
    }

    private void initGoogleLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions
                .DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener
                 */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                updateUI(null);
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
            if (mTwitterAuthClient != null) {
                mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        showProgressDialog();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        if (mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(this,
                    task -> handleFirebaseAuthResult(task, false));
        } else {
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, task -> handleFirebaseAuthResult(task, false));
        }
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        mProgressDialog = ProgressDialog.show(this, "", "Please wait...", true, true);
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();
        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                status -> updateUI(null));
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();
        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                status -> updateUI(null));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void signIn(String email, String password, boolean login) {
        showProgressDialog();
        Task<AuthResult> resultTask;
        final boolean isEmailVerificationNeeded;
        if (login) {
            isEmailVerificationNeeded = false;
            resultTask = mAuth.signInWithEmailAndPassword(email, password);
        } else {
            if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isAnonymous()) {
                AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                resultTask = mAuth.getCurrentUser().linkWithCredential(credential);
            } else {
                resultTask = mAuth.createUserWithEmailAndPassword(email, password);
            }
            isEmailVerificationNeeded = true;
        }
        resultTask.addOnCompleteListener(this, task -> handleFirebaseAuthResult(task,
                isEmailVerificationNeeded));
    }

    public void twitterLogin(View view) {
        TwitterAuthConfig twitterAuthConfig = new TwitterAuthConfig("nJFhGfpnBAv5JlWR52UKyBJU3",
                "7R3AOMRCQtdF30srlagJ8k1ZbCqdmaCtEm5Z5xehMHkBjJ7v0D");
        TwitterConfig.Builder twitterConfig = new TwitterConfig.Builder(this);
        TwitterConfig config = twitterConfig.twitterAuthConfig(twitterAuthConfig).build();
        Twitter.initialize(config);
        mTwitterAuthClient = new TwitterAuthClient();
        mTwitterAuthClient.authorize(this, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                handleTwitterSession(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.w(TAG, "twitterLogin:failure", exception);
                updateUI(null);
            }
        });
    }

    private void handleTwitterSession(TwitterSession session) {
        showProgressDialog();
        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);
        if (mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(this,
                    task -> handleFirebaseAuthResult(task, false));
        } else {
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, task -> handleFirebaseAuthResult(task, false));
        }
    }

    private void handleFirebaseAuthResult(Task<AuthResult> task, boolean isEmail) {
        hideProgressDialog();
        if (task.isSuccessful()) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (isEmail && user != null) {
                user.sendEmailVerification().addOnCompleteListener(task1 -> Toast.makeText(
                        LoginActivity.this, R.string.verify_email,
                        Toast.LENGTH_LONG).show());
            }
            updateUI(user);
        } else {
            Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(),
                    Toast.LENGTH_LONG).show();
            updateUI(null);
            if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isAnonymous()) {
                Toast.makeText(LoginActivity.this, R.string.try_again,
                        Toast.LENGTH_SHORT).show();
                mAuth.signOut();
            }
        }
    }

    @Override
    public void onLogin(String email, String password, boolean login) {
        signIn(email, password, login);
    }
}

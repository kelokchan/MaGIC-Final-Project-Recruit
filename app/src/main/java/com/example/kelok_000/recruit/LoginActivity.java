package com.example.kelok_000.recruit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kelok_000.recruit.server.CirclesServer;
import com.example.kelok_000.recruit.utils.CallChannel;
import com.soundcloud.android.crop.Crop;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A login screen that offers login via emailView/passwordView.
 */
public class LoginActivity extends AppCompatActivity {
    static final String TAG = "LoginActivity";
    
    private UserLoginTask mAuthTask = null;

    // UI references.
    @Bind(R.id.email) EditText emailView;
    @Bind(R.id.logo) ImageView logo;
    @Bind(R.id.password) EditText passwordView;
    @Bind(R.id.am_create_guest_button) Button signinButton;
    @Bind(R.id.content) View mLoginFormView;
    @Bind(R.id.login_progress) ProgressBar mProgressView;
    @Bind(R.id.profile) ImageView profileView;
    @Bind(R.id.typeContent) View typeContent;

    public final CallChannel channel = new CallChannel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        ButterKnife.bind(this);
        channel.open(this);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                logo.setVisibility(View.VISIBLE);
            }
        },500);

        SharedPreferences preferences = getSharedPreferences("main_preferences", MODE_PRIVATE);
        String username = preferences.getString("username", null);
        String password = preferences.getString("password", null);


        if(username == null || password == null) {
            // Not yet created any account, show login form
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    typeContent.setVisibility(View.VISIBLE);
                }
            }, 2000);
        }
        else {
            // User has previously created an account, so just login
            netLoginUsingPassword(username, password);
        }



        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.email || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
    }

    @OnClick({R.id.employeeBtn, R.id.employerBtn})
    public void showLogin(){
        typeContent.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLoginFormView.setVisibility(View.VISIBLE);
            }
        }, 100);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid emailView, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    @OnClick (R.id.am_create_guest_button)
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        emailView.setError(null);
        passwordView.setError(null);

        // Store values at the time of the login attempt.
        String email = emailView.getEditableText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid passwordView, if the user entered one.
        if (!isPasswordViewValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        // Check for a valid emailView address.
        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        } else if (!isEmailViewValid(email)) {
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailViewValid(String emailView) {
        //TODO: Replace this with your own logic
        return !emailView.isEmpty();
    }

    private boolean isPasswordViewValid(String passwordView) {
        //TODO: Replace this with your own logic
        return !passwordView.isEmpty();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(View.GONE);
                }
            });

        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String memailView;
        private final String mpasswordView;
        ProgressDialog ProgressDialog;

        UserLoginTask(String emailView, String passwordView) {
            memailView = emailView;
            mpasswordView = passwordView;
        }

        @Override
        protected void onPreExecute() {
            ProgressDialog  = ProgressDialog.show(LoginActivity.this, null, "Logging in", true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                // Simulate network access.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return false;
            }

            CirclesServer.api.createGuest(memailView, mpasswordView, "secret")
                    .forResponse("netReceivedGuestLogin")
                    .forFailure("netFailure")
                    .call(channel);

            return true;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                ProgressDialog.dismiss();
            } else {
                passwordView.setError(getString(R.string.error_incorrect_password));
                passwordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public void netReceivedGuestLogin(CirclesServer.UserModel model) {
        // We have the username here, save the username and password to storage
        String username = model.email;
        String password = "secret";

        SharedPreferences.Editor editor = getSharedPreferences("main_preferences", MODE_PRIVATE).edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();        // save

        Toast.makeText(this, "Received username for this account: " + username, Toast.LENGTH_SHORT).show();

        netLoginUsingPassword(username, password);
    }

    public void netLoginUsingPassword(String username, String password) {
        CirclesServer.api.getTokenUsingPassword(
                "password",
                username,
                password,
                "circlesnearme_android",
                "magic1234",
                "readwall writewall profile_picture_edit"
        ).forResponse("netReceivedToken").forFailure("netFailure").call(channel);
    }

    public void netReceivedToken(CirclesServer.TokenModel token) {
        Toast.makeText(this, "Received token: " + token.access_token, Toast.LENGTH_SHORT).show();

        // Show user content activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();           // dont show the login activity anymore
    }

    public void netFailure(Throwable e) {
        Log.d(TAG, "netFailure", e);
        mLoginFormView.setVisibility(View.VISIBLE);
        Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.profile_container)
    public void onClickProfile() {
        // Ask user to select an image
        Crop.pickImage(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Crop.REQUEST_PICK) {
            // Check if user has picked the image
            if(resultCode != RESULT_OK)
                return;        // user cancelled the activity (pressed back or didnt select any pic)
            Uri imageLocation = data.getData();

            // Lets crop the image
            Uri croppedLocation = Uri.fromFile(new File(getCacheDir(), "cropped.jpg"));
            Crop.of(imageLocation, croppedLocation)
                    .asSquare()
                    .withMaxSize(128, 128)
                    .start(this);

            return;
        }
        else if(requestCode == Crop.REQUEST_CROP) {
            // Check if user has cropped the image
            if(resultCode != RESULT_OK)
                return;         // user cancelled or did not crop

            // Get cropped image
            Uri croppedImage = Crop.getOutput(data);

            // Replace the profile image with this one
            profileView.setImageURI(null);
            profileView.setImageURI(croppedImage);

            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}


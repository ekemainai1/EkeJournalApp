package com.example.ekemini.journalapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ekemini.journalapp.databinding.ActivityEkeMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EkeMainActivity extends AppCompatActivity implements View.OnClickListener{

    // Declare data binding activity
    ActivityEkeMainBinding activityMainEke;

    // Buttons for clicks
    Button signIn;
    Button signUp;
    SignInButton googleSignIn;
    CircleImageView profilePic;
    private ProgressDialog eProgressDialog;

    // Views
    View view;
    EditText email;
    EditText password;


    private static final String TAG = EkeMainActivity.class.getCanonicalName();
    private static final int GG_SIGN_IN = 100;
    private static final int CC_SIGN_IN = 200;

    // Declare firebase authentication
    FirebaseAuth ekeAuth;
    GoogleSignInClient ekeGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainEke = DataBindingUtil.setContentView(this, R.layout.activity_eke_main);

        //Store the field now if you'd like without any need for casting
        signIn = activityMainEke.primarySignin.buttonSignIn;
        signUp = activityMainEke.primarySignin.buttonSignUp;
        googleSignIn = activityMainEke.primarySignin.btnGoogleLogin;
        email = activityMainEke.primarySignin.editTextEmail;
        password = activityMainEke.primarySignin.editTextPassword;
        profilePic = activityMainEke.primarySignin.profileImage;

        // set click listeners for all buttons
        signIn.setOnClickListener(this);
        signUp.setOnClickListener(this);
        googleSignIn.setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        ekeGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Initialize authentication
        ekeAuth = FirebaseAuth.getInstance();

        if(ekeAuth.getCurrentUser() != null) {
            Uri uri = ekeAuth.getCurrentUser().getPhotoUrl();
            // Load user image or profile
            Picasso.get()
                    .load(uri)
                    .placeholder(R.drawable.ic_user_icon)
                    .resize(80, 80)
                    .centerCrop()
                    .into(profilePic);
            profilePic.setVisibility(View.VISIBLE);
        }else {
            profilePic.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == signIn.getId()) {
            mainSignIn();
        } else if (i == signUp.getId()) {
            startSignUpActivity();
        } else if (i == googleSignIn.getId()) {
            signIn();
        }
    }

    private void mainSignIn() {
        showProgressDialog();
        if(ekeAuth.getCurrentUser() != null) {
            signIn();
            email.getText().clear();
            password.getText().clear();
        }else {
            signInNewUser(email.getText().toString(),
                    password.getText().toString());
            email.getText().clear();
            password.getText().clear();
        }
        hideProgressDialog();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check auth on Activity start
        if (ekeAuth.getCurrentUser() != null) {
            onEkeGoogleSuccess();
        }
    }

    private  void onEkeGoogleSuccess() {
        // Go to MainActivity

        FirebaseUser user = ekeAuth.getCurrentUser();
        Uri uri;
        String userName;
        String email;
        if(user != null) {
           Intent intent;
           uri = user.getPhotoUrl();
           userName = user.getDisplayName();
           email = user.getEmail();
            intent = new Intent(EkeMainActivity.this,
                    EkeJournalActivity.class);
            intent.setAction(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("user", userName);
            intent.putExtra("email", email);
            intent.setData(uri);
            startActivity(intent);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GG_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                //onEkeGoogleSuccess();

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                //updateUI(null);

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        ekeAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = ekeAuth.getCurrentUser();
                            onEkeGoogleSuccess();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(view, "Authentication Failed.",
                                    Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                        hideProgressDialog();
                    }
                });
    }


    private void signIn() {
        if(ekeCheckNetworkAccess(getApplicationContext())) {
            if (ekeAuth.getCurrentUser() != null) {
                onEkeGoogleSuccess();
            } else {
                Intent signInIntent = ekeGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, GG_SIGN_IN);
            }
        }else {
            Toast.makeText(getApplicationContext(),
                    "No network, please check your connection",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void signInNewUser(String email, String password){
        if(ekeCheckNetworkAccess(getApplicationContext())) {
            showProgressDialog();
            Log.d(TAG, "signIn");
            if (!validateSignInForm()) {
                return;
            }

            ekeAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in: success
                                //updateUI(user);
                                onEkeGoogleSuccess();
                                hideProgressDialog();
                            } else {
                                // Sign in: fail
                                Log.e(TAG, "Authentication failed!");
                                //updateUI(null);
                                Toast.makeText(getApplicationContext(), "Email or password failed",
                                        Toast.LENGTH_LONG).show();
                            }

                            // ...
                        }
                    });
        }else{
            Toast.makeText(getApplicationContext(),
                    "No network, please check your connection",
                    Toast.LENGTH_LONG).show();
        }
    }

    // check to see if the device is connected to network
    public static boolean ekeCheckNetworkAccess(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private void startSignUpActivity(){
        Intent intent = new Intent(EkeMainActivity.this,
                EkeSignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void showProgressDialog() {
        if (eProgressDialog == null) {
            eProgressDialog = new ProgressDialog(this);
            eProgressDialog.setMessage(getString(R.string.loading));
            eProgressDialog.setIndeterminate(true);
        }
        eProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (eProgressDialog != null && eProgressDialog.isShowing()) {
            eProgressDialog.dismiss();
        }
    }

    private boolean validateSignInForm(){
        boolean result = true;
        if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError("Required");
            result = false;
        } else {
            email.setError(null);
        }

        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError("Required");
            result = false;
        } else {
            password.setError(null);
        }

        return result;
    }
}

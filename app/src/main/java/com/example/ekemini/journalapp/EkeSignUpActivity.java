package com.example.ekemini.journalapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ekemini.journalapp.databinding.ActivityEkeSignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.ekemini.journalapp.EkeMainActivity.ekeCheckNetworkAccess;

public class EkeSignUpActivity extends AppCompatActivity implements View.OnClickListener{

    ActivityEkeSignUpBinding activityEkeSignUpBinding;
    private static final String TAG = EkeSignUpActivity.class.getCanonicalName();
    private static final int EKE_SIGN_UP = 300;

    EditText eUsername;
    EditText eEmail;
    EditText ePassword;
    Button eSignUp;
    private ProgressDialog eProgressDialog;

    // Declare firebase authentication
    FirebaseAuth ekeAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEkeSignUpBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_eke_sign_up);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ekeAuth = FirebaseAuth.getInstance();

        // Store handles of views
        eUsername = activityEkeSignUpBinding.editTextUser;
        eEmail = activityEkeSignUpBinding.editTextEmail;
        ePassword = activityEkeSignUpBinding.editTextPassword;
        eSignUp = activityEkeSignUpBinding.signUpPageButton;

        // Set onClickListener
        eSignUp.setOnClickListener(this);

    }

    private boolean validateSignUpForm(){
        boolean result = true;
        if (TextUtils.isEmpty(eUsername.getText().toString())) {
            eUsername.setError("Required");
            result = false;
        } else {
            eUsername.setError(null);
        }
        if (TextUtils.isEmpty(eEmail.getText().toString())) {
            eEmail.setError("Required");
            result = false;
        } else {
            eEmail.setError(null);
        }

        if (TextUtils.isEmpty(ePassword.getText().toString())) {
            ePassword.setError("Required");
            result = false;
        } else {
            ePassword.setError(null);
        }

        return result;
    }

    private void ekeSignUp() {
        Log.d(TAG, "signUp");
        if (!validateSignUpForm()) {
            return;
        }

        showProgressDialog();

        String email = eEmail.getText().toString();
        String password = ePassword.getText().toString();

        ekeAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onEkeAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(EkeSignUpActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

    private void onEkeAuthSuccess(FirebaseUser user) {

        String username = user.getDisplayName();
        Uri uri = user.getPhotoUrl();
        String email = user.getEmail();

        Intent intent = new Intent(EkeSignUpActivity.this,
                EkeJournalActivity.class);
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, username);
        intent.putExtra(Intent.EXTRA_TEXT, email);
        intent.setData(uri);
        // Go to MainActivity journal
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        if(ekeCheckNetworkAccess(getApplicationContext())) {
            ekeSignUp();
        }else {
            Toast.makeText(getApplicationContext(),
                    "No network, please check your connection",
                    Toast.LENGTH_LONG).show();
        }
    }
}

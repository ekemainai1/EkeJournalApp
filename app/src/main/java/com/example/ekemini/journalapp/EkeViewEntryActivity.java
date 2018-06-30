package com.example.ekemini.journalapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ekemini.journalapp.databinding.ActivityEkeViewEntryBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EkeViewEntryActivity extends AppCompatActivity implements View.OnClickListener{

    ActivityEkeViewEntryBinding activityEkeViewEntryBinding;

    CircleImageView userImage;
    TextView userName;
    TextView userEmail;
    Button buttonLogOut;
    TextView postEntry;

    // Declare firebase authentication
    FirebaseAuth ekeAuth;
    GoogleSignInClient ekeGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEkeViewEntryBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_eke_view_entry);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        // Store current user info
        userImage = activityEkeViewEntryBinding.profileImage;
        userName = activityEkeViewEntryBinding.showUser;
        userEmail = activityEkeViewEntryBinding.showEmail;
        buttonLogOut = activityEkeViewEntryBinding.buttonSignOut;
        postEntry = activityEkeViewEntryBinding.items;

        // Set click listener for the sign out button
        buttonLogOut.setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        ekeGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize authentication
        ekeAuth = FirebaseAuth.getInstance();

        String user = ekeAuth.getCurrentUser().getDisplayName();
        String email = ekeAuth.getCurrentUser().getEmail();
        Uri uri = ekeAuth.getCurrentUser().getPhotoUrl();

        // Load user image or profile
        Picasso.get()
                .load(uri)
                .placeholder(R.drawable.ic_user_icon)
                .resize(80, 80)
                .centerCrop()
                .into(userImage);

        userName.setText(user);
        userEmail.setText(email);

        // Retrieve data from incoming intent
        Intent intent = getIntent();
        String action = intent.getAction();
        if(Intent.ACTION_SEND.equals(action)){
            String userSelectedPost = intent.getStringExtra("post");
            postEntry.setText(userSelectedPost);

        }
    }

    public void signOut() {
        // Firebase sign out
        ekeAuth.signOut();
        // Google sign out
        ekeGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //updateUI(null);
                        startActivity(new Intent(EkeViewEntryActivity.this,
                                EkeMainActivity.class));
                    }
                });
    }

    @Override
    public void onClick(View v) {
        signOut();
    }



}

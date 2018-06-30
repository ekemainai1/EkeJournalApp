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
import android.widget.EditText;
import android.widget.TextView;

import com.example.ekemini.journalapp.databinding.ActivityEkeEditPostBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EkeEditPostActivity extends AppCompatActivity implements View.OnClickListener{

    ActivityEkeEditPostBinding activityEkeEditPostBinding;

    CircleImageView userImage;
    TextView userName;
    TextView userEmail;
    Button buttonLogOut;
    Button buttonSavePost;
    EditText userItem;

    // Declare firebase authentication
    FirebaseAuth ekeAuth;
    GoogleSignInClient ekeGoogleSignInClient;

    DatabaseReference ekeEditPostDatabaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEkeEditPostBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_eke_edit_post);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        // Store current user info
        userImage = activityEkeEditPostBinding.profileImage;
        userName = activityEkeEditPostBinding.showUser;
        userEmail = activityEkeEditPostBinding.showEmail;
        buttonLogOut = activityEkeEditPostBinding.buttonSignOut;
        buttonSavePost = activityEkeEditPostBinding.buttonSavePost;
        userItem = activityEkeEditPostBinding.useritem;

        // Set click listener for the sign out button
        buttonLogOut.setOnClickListener(this);
        buttonSavePost.setOnClickListener(this);

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
            String mypost = intent.getStringExtra("myPost");
            userItem.setText(mypost);
        }

        ekeEditPostDatabaseRef = FirebaseDatabase.getInstance().getReference("user");

    }


    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onStop() {
        super.onStop();

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
                        startActivity(new Intent(EkeEditPostActivity.this,
                                EkeMainActivity.class));
                    }
                });
    }

    private void updatePost(){
        String updateUserPost = userItem.getText().toString();
        EkePrefUtils ekePostId = new EkePrefUtils(getApplicationContext());
        String postId = ekePostId.loadSelectedDatabaseId();
        ekeEditPostDatabaseRef.child(ekeAuth.getCurrentUser().getUid())
                              .child(postId)
                              .child("userPost")
                              .setValue(updateUserPost);
        // Close edit activity
        finish();

    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == buttonLogOut.getId()) {
                signOut();
        } else if (i == buttonSavePost.getId()) {
            updatePost();
        }

    }

}

package com.example.ekemini.journalapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ekemini.journalapp.databinding.ActivityEkeJournalBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EkeJournalActivity extends AppCompatActivity implements View.OnClickListener{

    ActivityEkeJournalBinding activityEkeJournalBinding;

    // Initialize views
    CircleImageView userImage;
    TextView userName;
    TextView userEmail;
    EditText commentText;
    ImageView sendComment;
    Button buttonLogOut;

    // Declare firebase authentication
    FirebaseAuth ekeAuth;
    GoogleSignInClient ekeGoogleSignInClient;

    // Database Reference
    DatabaseReference ekeDatabaseReference;

    List<User> postListings;
    RecyclerView ekeRecyclerView;
    EkeRecyclerViewAdapter ekeAdapter;
    LayoutManager layoutManager;

    // Id for database child nodes for broadcast
    String idPost;

    // IntentFilter for broadcast of delete item position
    public IntentFilter intentFilterDeletePost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEkeJournalBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_eke_journal);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        // Store current user info
        userImage = activityEkeJournalBinding.profileImage;
        userName = activityEkeJournalBinding.showUser;
        userEmail = activityEkeJournalBinding.showEmail;
        commentText = activityEkeJournalBinding.useritem;
        sendComment = activityEkeJournalBinding.sendItem;
        buttonLogOut = activityEkeJournalBinding.buttonSignOut;
        ekeRecyclerView = activityEkeJournalBinding.recyclerview;

        // Set click listener for the sign out button
        buttonLogOut.setOnClickListener(this);
        sendComment.setOnClickListener(this);

        // Retrieve data from incoming intent
        Intent intent = getIntent();
        String action = intent.getAction();
        if(Intent.ACTION_SEND.equals(action)){
            String user = intent.getStringExtra("user");
            String email = intent.getStringExtra("email");
            //Log.d("email", email);
            Uri uri = intent.getData();
            if (uri != null) {
                Log.d("photo", uri.toString());
            }
            // Load user image or profile
            Picasso.get()
                    .load(uri)
                    .placeholder(R.drawable.ic_user_icon)
                    .resize(80, 80)
                    .centerCrop()
                    .into(userImage);

            userName.setText(user);
            userEmail.setText(email);

        }

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        ekeGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Initialize authentication
        ekeAuth = FirebaseAuth.getInstance();

        // Initialize database object reference
        ekeDatabaseReference = FirebaseDatabase.getInstance().getReference("user");

        postListings = new ArrayList<>();
        ekeRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(EkeJournalActivity.this);
        ekeRecyclerView.setLayoutManager(layoutManager);
        ekeRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Initialize and add action for edit post broadcast intent filter
        intentFilterDeletePost = new IntentFilter();
        intentFilterDeletePost.addAction(EkeDialogActivity.Broadcast_DELETE_POST);

    }

    @Override
    protected void onStart() {
        super.onStart();
        ekeDatabaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postListings.clear();
                String uses = ekeAuth.getCurrentUser().getUid();
                DataSnapshot userSnapshot = dataSnapshot.child(uses);
                Iterable<DataSnapshot> userChildren = userSnapshot.getChildren();
                for(DataSnapshot userSnap: userChildren){
                    User user = userSnap.getValue(User.class);
                    postListings.add(user);

                }
                ekeAdapter = new EkeRecyclerViewAdapter(
                        EkeJournalActivity.this, postListings);
                ekeRecyclerView.setAdapter(ekeAdapter);
                Log.d("ta", ""+postListings.size());
                Toast.makeText(getApplicationContext(),
                        "data:"+postListings.size(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        // Register receiver in on start of the edit post activity
        this.registerReceiver(receiverDeletePost, intentFilterDeletePost);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister broadcast receive for post editing
        unregisterReceiver(receiverDeletePost);
    }

    // Register song share broadcast receiver
    private BroadcastReceiver receiverDeletePost = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(EkeDialogActivity.Broadcast_DELETE_POST))
               idPost = intent.getStringExtra("positionId");
               deletePost(idPost);
        }

    };

    public void signOut() {
        // Firebase sign out
        ekeAuth.signOut();
        // Google sign out
        ekeGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //updateUI(null);
                        startActivity(new Intent(EkeJournalActivity.this,
                                EkeMainActivity.class));
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.setting:
                //newGame();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Add post on thoughts by users
    private void addUserAndPost(){
        FirebaseUser user = ekeAuth.getCurrentUser();
        String userID = user.getUid();
        String username = user.getDisplayName();
        String useremail = user.getEmail();
        String userpost = commentText.getText().toString();

        if(!TextUtils.isEmpty(userpost)){
            String id = ekeDatabaseReference.push().getKey();
            User newUser = new User(userID, id, username, useremail, userpost);
            ekeDatabaseReference.child(userID).child(id).setValue(newUser);
            // Show successful storage
            Toast.makeText(getApplicationContext(), "Post added ...",
                    Toast.LENGTH_LONG).show();
            commentText.getText().clear();
        }else {
            Toast.makeText(getApplicationContext(), "Express your thoughts",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void deletePost(String id){
        ekeDatabaseReference.child(ekeAuth.getCurrentUser().getUid())
                .child(id)
                .setValue(null);
        // Close edit activity
        finish();
    }



    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == buttonLogOut.getId()) {
            signOut();
        }else if(i == sendComment.getId()){
            addUserAndPost();
        }
    }


}

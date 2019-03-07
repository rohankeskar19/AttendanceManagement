package com.example.rohanspc.attendancemanagement.Account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.rohanspc.attendancemanagement.Login.LoginActivity;
import com.example.rohanspc.attendancemanagement.R;
import com.example.rohanspc.attendancemanagement.Utilities.FireBaseMethods;
import com.example.rohanspc.attendancemanagement.Utilities.bottomNavigationViewHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class AccountActivity extends AppCompatActivity {
    private static final String TAG = "AccountActivity";
    private static final int ACTIVITY_NUMBER = 3;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    ProgressBar mProgressBar;
    private Button mButton;
    private CardView mErrorCardView;
    private FirebaseUser user1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Log.d(TAG, "onCreate: Starting");
        mProgressBar = findViewById(R.id.progress_bar);
        mButton = findViewById(R.id.signout_button);
        mErrorCardView = findViewById(R.id.account_emailnot_verified_card);
        mProgressBar.setVisibility(View.GONE);
        setupBottomNavigationView();
        setupFirebaseAuth();
        btnSignOut();

    }

    private void btnSignOut(){

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);

                mAuth.signOut();
                mProgressBar.setVisibility(View.GONE);
                AccountActivity.this.finish();
            }
        });

    }

    /*
     * Bottom Navigation view setup
     * */

    private void setupBottomNavigationView(){

        Log.d(TAG, "setupBottomNavigationView: setting up bottom nav view");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom_nav_bar);

        bottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        bottomNavigationViewHelper.enableNavigation(AccountActivity.this,bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


        if(intent.getFlags() == (Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION)){
            overridePendingTransition(0,0);
        }
    }

    /*
    * --------------------------------------------------Firebase-----------------------------------------------------------------------------------------------
    * */

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: Starting");
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if(user != null){
                    Log.d(TAG, "onAuthStateChanged: Signed_in " + user.getUid());
                    if(user.isEmailVerified()){
                        mErrorCardView.setVisibility(View.GONE);
                    }
                    else{
                        setEmailNotVerifiedPrompt();
                    }
                }
                else{
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                    Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        Log.d(TAG, "onStart: starting");

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }


    public void setEmailNotVerifiedPrompt() {
            mErrorCardView.setVisibility(View.VISIBLE);
            Log.d(TAG, "setEmailNotVerifiedPrompt: Not Verified");
        }
}


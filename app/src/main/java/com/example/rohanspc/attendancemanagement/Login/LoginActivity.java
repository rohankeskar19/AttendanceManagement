package com.example.rohanspc.attendancemanagement.Login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rohanspc.attendancemanagement.Home.MainActivity;
import com.example.rohanspc.attendancemanagement.R;
import com.example.rohanspc.attendancemanagement.Utilities.FireBaseMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;



public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FireBaseMethods fireBaseMethods;

    private ProgressBar mProgressBar;
    private EditText mEmailEditText,mPasswordEditText;
    private TextView mTextView;
    private Button btnLogin;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fireBaseMethods = new FireBaseMethods(LoginActivity.this);
        setContentView(R.layout.activity_login);
        mProgressBar = findViewById(R.id.progress_bar);
        mEmailEditText = findViewById(R.id.input_email_login);
        mPasswordEditText = findViewById(R.id.input_password_login);
        mTextView = findViewById(R.id.login_textview);

//        mEmailInputLayout = findViewById(R.id.input_email_login_layout);
//        mPasswordInputLayout = findViewById(R.id.input_password_login_layout);

        Log.d(TAG, "onCreate: Starting");
        mProgressBar.setVisibility(View.GONE);
        setupFirebaseAuth();
        init();

    }



    private boolean checkString(String string){
        if(string.equals("")){
            return false;
        }
        else return true;
    }
/*
* -------------------------------------------------------------------Firebase--------------------------------------------------------------------------------
* */

    private void init(){

        btnLogin = findViewById(R.id.input_button_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mEmailEditText.getText().toString().trim();
                String password = mPasswordEditText.getText().toString().trim();
                if(!checkString(email) || !checkString(password)) {
                    if (!checkString(email)) {
                        mEmailEditText.setError(getString(R.string.email_error));
                    }
                    if (!checkString(password)) {
                        mPasswordEditText.setError(getString(R.string.password_error));

                    }
                }
                else if(email.contains(" ") || password.contains(" ")){
                    if(email.contains(" ")){
                        mEmailEditText.setError(getString(R.string.empty_space_error));
                    }
                    if(password.contains(" ")){
                        mPasswordEditText.setError(getString(R.string.empty_space_error));
                    }
                }
                else{

                    mProgressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");

                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);




                                    } else {
                                        // If sign in fails, display a message to the user.

                                        Log.w(TAG, "signInWithEmail:failure", task.getException());

                                        if(task.getException() instanceof FirebaseAuthInvalidUserException){
                                            mEmailEditText.setError(getString(R.string.invalid_user));
                                        }
                                        if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                            mPasswordEditText.setError(getString(R.string.invalid_password));
                                        }


                                    mProgressBar.setVisibility(View.GONE);
                                }


                                }
                            });
                }
            }
        });

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
            }
        });
    }


    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: Starting");
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    Log.d(TAG, "onAuthStateChanged: Signed_in" + user.getUid());
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                else{
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }

}

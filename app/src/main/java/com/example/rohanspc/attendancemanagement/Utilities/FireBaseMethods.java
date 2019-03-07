package com.example.rohanspc.attendancemanagement.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.rohanspc.attendancemanagement.Home.MainActivity;
import com.example.rohanspc.attendancemanagement.Login.LoginActivity;
import com.example.rohanspc.attendancemanagement.Login.SignupActivity;
import com.example.rohanspc.attendancemanagement.Models.User;
import com.example.rohanspc.attendancemanagement.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FireBaseMethods {
    private static final String TAG = "FireBaseMethods";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mRef;
    private CollectionReference usersRef;


    private DocumentReference documentReference;



    private Context mContext;
    private String userID;




    public FireBaseMethods(Context context){
        mAuth = FirebaseAuth.getInstance();
        mContext = context;

        mRef = FirebaseFirestore.getInstance();
        usersRef = mRef.collection("users");
        documentReference = mRef.collection("users").document();

        setupFirebaseAuth();


        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }


    /*
    * Send verification email
    * */
    public void sendVerificationEmail(){
        FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(mContext,"Verification email sent",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(mContext,"Couldn't send verification email",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }


    }


    /*
    * Signup new user
    * */
    public void registerNewEmail(final String email, String password, final EditText mEmailEditText, final EditText mPasswordEditText, final String firstName, final String lastName,final ProgressBar mProgressBar,final String accountType){


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "register user : starting ");
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            assert user != null;
                            userID = user.getUid();

                            Log.d(TAG, "UserID: " + userID);
                            Toast.makeText(mContext,"Sign Up was Successful", Toast.LENGTH_SHORT).show();

                            sendVerificationEmail();
                            addNewUser(firstName,lastName,email,userID,accountType);






                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                mEmailEditText.setError(mContext.getString(R.string.email_already_used_error));
                            }
                            if(task.getException() instanceof FirebaseAuthWeakPasswordException){
                                mPasswordEditText.setError(mContext.getString(R.string.weak_password));

                            }
                            mProgressBar.setVisibility(View.GONE);
                        }


                    }
                });


    }

    /*
    * Add new usser to Database
    * */
    private void addNewUser(String firstName,String lastName,String email,String userID,String accountType){
        String name = firstName + " " + lastName;
        String account_type = accountType;
        User user = new User(name,email,userID,account_type,null);
        Log.d(TAG, "addNewUser: " + "userID: " + userID);
        mRef.collection(mContext.getString(R.string.DB_users)).document(userID).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "onComplete: addNewUser Success");

                    mAuth.signOut();
                    Intent intent = new Intent(mContext,LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    mContext.startActivity(intent);
                    ((Activity)mContext).finish();

                }
                else {
                    Log.d(TAG, "onComplete: addNewUser Failed" + task.getException());

                }
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
                    Log.d(TAG, "onAuthStateChanged: signed_in" + user.getUid());
                }
                else{
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                }
            }
        };

    }




}



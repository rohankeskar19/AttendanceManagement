package com.example.rohanspc.attendancemanagement.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rohanspc.attendancemanagement.Models.User;
import com.example.rohanspc.attendancemanagement.R;
import com.example.rohanspc.attendancemanagement.Utilities.FireBaseMethods;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private EditText mEditTextFirstName,mEditTextLastName,mEditTextEmail,mEditTextPassword,mEditTextConfirmPassword;

    private String email,password,firstName,lastName,password1,accountType;
    private ProgressBar mProgressBar;

    private Button mSignupButton;
    private CheckBox mStudentCheckBox,mTeacherCheckbox;


    private FireBaseMethods fireBaseMethods;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;







    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Edit Text
        mEditTextFirstName = findViewById(R.id.input_firstname_signup);
        mEditTextLastName = findViewById(R.id.input_lastname_signup);
        mEditTextEmail = findViewById(R.id.input_email_signup);
        mEditTextPassword = findViewById(R.id.input_password_signup);
        mProgressBar = findViewById(R.id.signup_progress_bar);
        mEditTextConfirmPassword = findViewById(R.id.input_password_confirm_signup);

        //Layouts

        mStudentCheckBox = findViewById(R.id.student_checkbox);
        mTeacherCheckbox = findViewById(R.id.teacher_checkbox);
        mSignupButton = findViewById(R.id.input_button_signup);

        //Firebase
        fireBaseMethods = new FireBaseMethods(SignupActivity.this);



        Log.d(TAG, "onCreate: Starting");
        mProgressBar.setVisibility(View.GONE);
        setupFirebaseAuth();
        checkBoxListeners();
        init();



    }

    private boolean checkString(String string){
        if(string.equals("")){
            return false;
        }
        else return true;
    }

    private void init(){

        Log.d(TAG, "init: Starting");
        mSignupButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                email = mEditTextEmail.getText().toString().trim();
                password = mEditTextPassword.getText().toString().trim();
                firstName = mEditTextFirstName.getText().toString().trim();
                lastName = mEditTextLastName.getText().toString().trim();
                password1 = mEditTextConfirmPassword.getText().toString().trim();


                if(!checkString(email) || !checkString(password) || !checkString(firstName) || !checkString(lastName) || !checkString(password1) ||
                        email.contains(" ") || password.contains(" ") || firstName.contains(" ") || lastName.contains(" ") || password1.contains(" ") || !password.equals(password1) || !mStudentCheckBox.isChecked() && !mTeacherCheckbox.isChecked()){
                    if(!checkString(email)){
                        mEditTextEmail.setError(getString(R.string.email_error));
                    }
                    if(!checkString(password)){
                        mEditTextPassword.setError(getString(R.string.password_error));
                    }
                    if(!checkString(firstName)){
                        mEditTextFirstName.setError(getString(R.string.first_name_error));
                    }
                    if(!checkString(lastName)){
                       mEditTextLastName.setError(getString(R.string.last_name_error));
                    }
                    if(!checkString(password1)){
                        mEditTextConfirmPassword.setError(getString(R.string.password_error));
                    }


                    if(email.contains(" ")){
                        mEditTextEmail.setError(getString(R.string.empty_space_error));
                    }
                    if(email.contains(" ")){
                        mEditTextConfirmPassword.setError(getString(R.string.empty_space_error));
                    }


                    if(password.contains(" ")){
                        mEditTextPassword.setError(getString(R.string.empty_space_error));
                    }
                    if(firstName.contains(" ")){
                        mEditTextFirstName.setError(getString(R.string.empty_space_error));
                    }
                    if(lastName.contains(" ")){
                        mEditTextLastName.setError(getString(R.string.empty_space_error));
                    }

                    if(!password.equals(password1)){
                        mEditTextConfirmPassword.setError(getString(R.string.passwords_not_match_signup));
                        mEditTextPassword.setError(getString(R.string.passwords_not_match_signup));
                    }

                    if(!mStudentCheckBox.isChecked() && !mTeacherCheckbox.isChecked()){
                        Toast.makeText(SignupActivity.this,"You must select atleast one option",Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    Log.d(TAG, "onClick: acount creation started");
                    mProgressBar.setVisibility(View.VISIBLE);
                    fireBaseMethods.registerNewEmail(email,password,mEditTextEmail,mEditTextPassword,firstName,lastName,mProgressBar,accountType);




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
                    Log.d(TAG, "onAuthStateChanged: Signed_in" + user.getUid());


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


    private void checkBoxListeners(){
        mStudentCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mTeacherCheckbox.isChecked()) {
                    mTeacherCheckbox.setChecked(false);
                }

                accountType = "student";
            }
        });

        mTeacherCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mStudentCheckBox.isChecked()) {
                    mStudentCheckBox.setChecked(false);
                }

                accountType = "teacher";
            }
        });


    }

}

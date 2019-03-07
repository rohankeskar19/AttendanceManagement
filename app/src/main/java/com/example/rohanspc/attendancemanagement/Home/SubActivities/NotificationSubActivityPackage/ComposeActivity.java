package com.example.rohanspc.attendancemanagement.Home.SubActivities.NotificationSubActivityPackage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.example.rohanspc.attendancemanagement.Models.Classrooms;
import com.example.rohanspc.attendancemanagement.Models.Notification;
import com.example.rohanspc.attendancemanagement.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class ComposeActivity extends AppCompatActivity {

    private EditText titleEditText,descriptionEditText;
    private MultiAutoCompleteTextView multiAutoCompleteTextView;

    private ArrayList<Classrooms> mClassrooms;
    private ArrayList<String> classroomsString;
    private ArrayList<String> classroomID;
    private String[] classroomNames;
    private Classrooms[] mclassrooms;
    private String classrooms;
    private String title,description;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mRef;
    private CollectionReference classroomReference;


    private static final String TAG = "ComposeActivity";
    private FloatingActionButton floatingActionButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        mClassrooms = new ArrayList<>();
        classroomsString = new ArrayList<>();
        classroomID = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseFirestore.getInstance();
        classroomReference = mRef.collection(getString(R.string.DB_classrooms));

        multiAutoCompleteTextView = findViewById(R.id.classroomEditText);
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        floatingActionButton = findViewById(R.id.send_notification);


        Intent intent = getIntent();




            Bundle bundle = intent.getBundleExtra("classroomBundle");
            mClassrooms = (ArrayList<Classrooms>) bundle.getSerializable("classrooms");
            mclassrooms = mClassrooms.toArray(new Classrooms[mClassrooms.size()]);


        setupFirebaseAuth();
        Log.d(TAG, "onCreate: " + mClassrooms.toString());
        init();
    }

    private void setupFirebaseAuth(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                
                
                if(user != null){
                    Log.d(TAG, "onAuthStateChanged: signed in" + user.getUid() );
                }
                else{
                    Log.d(TAG, "onAuthStateChanged: signed out");
                }
            }
        };

    }


    private void init(){
        for(Classrooms classrooms : mClassrooms){
            classroomsString.add(classrooms.getName());
        }

        Log.d(TAG, "init: " + classroomsString);

        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,classroomsString);
        multiAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        multiAutoCompleteTextView.setAdapter(mAdapter);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                classrooms = multiAutoCompleteTextView.getText().toString().trim();

                title = titleEditText.getText().toString().trim();
                description = descriptionEditText.getText().toString().trim();
                if(classrooms.contains(",")) {
                    classroomNames = classrooms.split(",");
                    Log.d(TAG, "onClick: classroomNames" + Arrays.toString(classroomNames));
                    Log.d(TAG, "onClick: classrooms" + Arrays.toString(mclassrooms));
                }
                else {
                    classroomNames = new String[1];
                    classroomNames[0] = classrooms;

                }
                if(!title.equals("") || !description.equals("") || !classrooms.equals("")){
                    Log.d(TAG, "onClick: sending data");
                    sendData();
                }
                else{
                    if(title.equals("")){
                        titleEditText.setError("Field can't be empty");
                    }
                    if(description.equals("")){
                        descriptionEditText.setError("Field can't be empty");
                    }
                    if(classrooms.equals("")){
                        multiAutoCompleteTextView.setError("Field can't be empty");
                    }
                }




            }
        });

    }


    private void sendData(){


        for(int i = 0; i < classroomNames.length; i++){
            Log.d(TAG, "sendData: classroomNames" + classroomNames[i]);
            for(int j = 0; j < mclassrooms.length; j++){
                Log.d(TAG, "sendData: mclassrooms names " + mclassrooms[j].getName());
                if (classroomNames[i].trim().equals(mclassrooms[j].getName())) {
                    Log.d(TAG, "sendData: matched");
                    classroomID.add(mclassrooms[j].getId());
                }
            }
        }

        Log.d(TAG, "sendData: classroomID " + classroomID.toString());
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        String Date = DateFormat.getDateInstance().format(new Date());
        Notification notification = new Notification(title,description,hour,min,Date);
        Log.d(TAG, "sendData: classroomID " + classroomID.toString());
        for (String id : classroomID){
            Log.d(TAG, "sendData: id " + id);
                classroomReference.document(id).collection("notifications").document().set(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: Notification sent");
                        Toast.makeText(ComposeActivity.this,"Notification sent",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        Log.d(TAG, "onComplete: Failed to send notification");
                        Toast.makeText(ComposeActivity.this,"Failed to send notification",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });

        }

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

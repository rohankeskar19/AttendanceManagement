package com.example.rohanspc.attendancemanagement.Home.SubActivities.AnalyticsActivityPackage;

import android.content.Intent;
import android.icu.lang.UScript;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.TextView;

import com.example.rohanspc.attendancemanagement.Models.Classrooms;
import com.example.rohanspc.attendancemanagement.Models.User;
import com.example.rohanspc.attendancemanagement.R;
import com.example.rohanspc.attendancemanagement.RecyclerViewClassrooms;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AnalyticsActivity extends AppCompatActivity {

    private static final String TAG = "AnalyticsActivity";

    private RecyclerViewClassrooms recyclerViewClassrooms;
    private TextView mTextView;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mRef;
    private CollectionReference collectionReference;

    private User user;
    private ArrayList<Classrooms> mClassrooms;
    private ArrayList<String> classroomID;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        mTextView = findViewById(R.id.textMessage);
        recyclerViewClassrooms = findViewById(R.id.recycler_view);



        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseFirestore.getInstance();
        collectionReference = mRef.collection(getString(R.string.DB_classrooms));

        mClassrooms = new ArrayList<>();

        Intent intent = getIntent();

        if(intent.hasExtra("user_info")){
            user = new User();
            user = (User) intent.getSerializableExtra("user_info");
        }
        classroomID = user.getClassroomID();

        recyclerViewClassrooms.hideIfNotEmpty(mTextView);
        recyclerViewClassrooms.showIfEmpty(mTextView);
        setupFirebaseAuth();

    }


    private void getData(){
        mClassrooms.clear();
        Log.d(TAG, "getData: Starting");
        if(classroomID != null) {

            for (String id : classroomID) {
                if (id != null) {
                    Query query = collectionReference.whereEqualTo("id",id);

                    query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                          if(e != null){
                              Log.d(TAG, "onEvent: Exception occured" + e.getMessage());
                          }
                          else{
                              for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                                  if(queryDocumentSnapshot != null) {
                                      mClassrooms.add(queryDocumentSnapshot.toObject(Classrooms.class));
                                  }
                                  else{
                                      Log.d(TAG, "onEvent: Failed to retrieve data");
                                  }
                              }
                              setupRecyclerView();
                          }
                        }
                    });


                }
            }
        }


    }

    private void setupFirebaseAuth(){

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                
                if(user != null){
                    Log.d(TAG, "onAuthStateChanged: Signed in");
                    getData();

                }
                else{
                    Log.d(TAG, "onAuthStateChanged: Signed out");
                }
            }
        };


    }

    private void setupRecyclerView(){
        Log.d(TAG, "setupRecyclerView: mClassrooms" + mClassrooms);
        RecyclerViewAnalyticsAdapter mAdapter = new RecyclerViewAnalyticsAdapter(AnalyticsActivity.this,mClassrooms);
        recyclerViewClassrooms.setAdapter(mAdapter);
        recyclerViewClassrooms.setLayoutManager(new LinearLayoutManager(this));
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

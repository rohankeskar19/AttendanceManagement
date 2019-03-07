package com.example.rohanspc.attendancemanagement.Home.SubActivities.AttendanceActivityPackage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rohanspc.attendancemanagement.Home.MainActivity;
import com.example.rohanspc.attendancemanagement.Models.Classrooms;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

public class AttendanceActivity extends AppCompatActivity {

    private static final String TAG = "AttendanceActivity";
    
    LinearLayout linearLayout;


    private FirebaseFirestore mRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CollectionReference classroomRef,usersRef;

    private ArrayList<Classrooms> mClassrooms;
    private ArrayList<String> classroomID;
    private RecyclerViewClassrooms recyclerViewClassrooms;
    private TextView mTextView;

    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);


        //Firebase
        mRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        classroomRef = mRef.collection(getString(R.string.DB_classrooms));
        usersRef = mRef.collection(getString(R.string.DB_users));

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        linearLayout = findViewById(R.id.checkboxLinearLayout);
        mClassrooms = new ArrayList<>();
        recyclerViewClassrooms = findViewById(R.id.recycler_view);
        mTextView = findViewById(R.id.no_classroom_text);


        Intent intent = getIntent();

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryAttendance,R.color.colorPrimaryDarkAttendance,R.color.colorAccentAttendance);

        if(intent.hasExtra("classroomID")){
            classroomID = intent.getStringArrayListExtra("classroomID");
        }


        Log.d(TAG, "onCreate: " + classroomID);

        recyclerViewClassrooms.hideIfNotEmpty(mTextView);
        recyclerViewClassrooms.showIfEmpty(mTextView);


        setupFirebaseAuth();


        init();


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


    private void getData(){
        mClassrooms.clear();
        Log.d(TAG, "getData: Starting");
        if(classroomID != null) {

            for (String id : classroomID) {
                if (id != null) {
                    Query query = classroomRef.whereEqualTo("id",id);

                    query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            if(e != null){
                                Log.d(TAG, "onEvent: " + e.getMessage());

                            }
                            else{
                                for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                                    if(queryDocumentSnapshot != null){
                                        mClassrooms.add(queryDocumentSnapshot.toObject(Classrooms.class));

                                    }
                                    else{
                                        Log.d(TAG, "onEvent: Data retrieval failed");
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




    private void init(){

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getData();
            }
        });
    }

    private void setupRecyclerView(){

        Log.d(TAG, "setupRecyclerView: " + mClassrooms);
        RecyclerViewAttendanceAdapter adapter = new RecyclerViewAttendanceAdapter(AttendanceActivity.this,mClassrooms);
        recyclerViewClassrooms.setAdapter(adapter);
        recyclerViewClassrooms.setLayoutManager(new LinearLayoutManager(this));
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
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

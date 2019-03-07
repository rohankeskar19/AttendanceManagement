package com.example.rohanspc.attendancemanagement.Home.SubActivities.NotificationSubActivityPackage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;


import com.example.rohanspc.attendancemanagement.Models.Classrooms;
import com.example.rohanspc.attendancemanagement.Models.Notification;
import com.example.rohanspc.attendancemanagement.R;
import com.example.rohanspc.attendancemanagement.RecyclerViewClassrooms;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationSubActivity extends AppCompatActivity{

    private ExpandableListView expandableListView;
    private ArrayList<Notification> mNotifications;
    private ArrayList<String> classroomID;
    private ArrayList<Classrooms> mClassrooms;



    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mRef;
    private CollectionReference collectionReference,notificationReference;


    private TextView mTextView;
    private FloatingActionButton floatingActionButton;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final String TAG = "NotificationSubActivity";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_notifications);



        floatingActionButton = findViewById(R.id.send_notification);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryNotifications,R.color.colorPrimaryDarkNotifications,R.color.colorAccentNotifications);

        mRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        collectionReference = mRef.collection(getString(R.string.DB_classrooms));
        notificationReference = mRef.collection("notifications");
        mTextView = findViewById(R.id.message_text_view);
        mClassrooms = new ArrayList<>();
        mNotifications = new ArrayList<>();
        classroomID = new ArrayList<>();

        expandableListView = findViewById(R.id.listView);
        setupFirebaseAuth();
        Intent intent = getIntent();

        if(intent.hasExtra("classrooms")){
            classroomID =  intent.getStringArrayListExtra("classrooms");


            get();
        }

        init();

    }

    private void init(){
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(NotificationSubActivity.this,ComposeActivity.class);
                Bundle bundle = new Bundle();

                bundle.putSerializable("classrooms",mClassrooms);
                intent.putExtra("classroomBundle",bundle);
                startActivity(intent);
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotifications();
            }
        });
    }




    private void get() {

        if (classroomID != null) {
            if (mClassrooms.isEmpty()) {
                for (String id : classroomID) {
                    collectionReference.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                Classrooms classrooms = task.getResult().toObject(Classrooms.class);
                                mClassrooms.add(classrooms);
                                Log.d(TAG, "onComplete: mClassrooms: " + mClassrooms);
                            } else {
                                Log.d(TAG, "onComplete: " + task.getException().toString());
                            }
                        }
                    });

                }
            }

        }
    }


    private void getNotifications() {
        if (classroomID != null) {
            mNotifications.clear();
            for (String id : classroomID) {
                if(id != null){

                    collectionReference.document(id).collection("notifications").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();

                                for (DocumentSnapshot doc : documentSnapshots) {
                                    mNotifications.add(doc.toObject(Notification.class));

                                }
                                setupRecyclerView();
                                Log.d(TAG, "onComplete: Data retrieval succesful");
                            } else {
                                Log.d(TAG, "onComplete: Data retrieval failed");
                            }
                        }
                    });






                }




            }

        }
    }



    private void setupRecyclerView(){

        ExpandableListAdapter mAdapter = new ExpandableListAdapter(this,mNotifications);
        if(mAdapter.isEmpty()){
            expandableListView.setVisibility(View.GONE);

        }
        else{
            expandableListView.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.INVISIBLE);
        }

        expandableListView.setAdapter(mAdapter);
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
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
                    getNotifications();
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

package com.example.rohanspc.attendancemanagement.Notification;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.rohanspc.attendancemanagement.RecyclerViewClassrooms;
import com.example.rohanspc.attendancemanagement.Models.Request;
import com.example.rohanspc.attendancemanagement.Models.User;
import com.example.rohanspc.attendancemanagement.R;
import com.example.rohanspc.attendancemanagement.Utilities.bottomNavigationViewHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class NotificationActivity extends AppCompatActivity {
    private static final String TAG = "NotificationActivity";
    private static final int ACTIVITY_NUMBER = 2;

    private ArrayList<Request> requestArrayList;
    RecyclerViewClassrooms recyclerViewClassrooms;
    SwipeRefreshLayout swipeRefreshLayout;



    //Firebase
    private FirebaseFirestore mRef;
    private CollectionReference requestCollectionReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private User user;
    private String userID;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mRef = FirebaseFirestore.getInstance();
        requestCollectionReference = mRef.collection(getString(R.string.DB_requests));
        requestArrayList = new ArrayList<>();

        recyclerViewClassrooms = findViewById(R.id.notification_recyclerview);
        swipeRefreshLayout = findViewById(R.id.refresh_layout);


        Log.d(TAG, "onCreate: Starting");
        setupBottomNavigationView();
        setupFirebaseAuth();

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary1,R.color.colorPrimaryDark,R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRequests();
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


        if(intent.getFlags() == (Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)){
            overridePendingTransition(0,0);
        }
    }


    /*
     * Bottom Navigation view setup
     * */

    private void setupBottomNavigationView(){

        Log.d(TAG, "setupBottomNavigationView: setting up bottom nav view");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom_nav_bar);

        bottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        bottomNavigationViewHelper.enableNavigation(NotificationActivity.this,bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);


    }

    public void getRequests(){
        Log.d(TAG, "getRequests: " + requestArrayList);
        requestArrayList.clear();
        Log.d(TAG, "getRequests: After clearing "  + requestArrayList);
        Query query = requestCollectionReference.whereEqualTo("toUserID",userID);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.d(TAG, "onEvent: Exception" + e.getMessage());
                }
                else {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        if(doc != null){
                            requestArrayList.add(doc.toObject(Request.class));

                        }
                        else{
                            Log.d(TAG, "onEvent: return null");
                        }
                    }
                    Log.d(TAG, "onEvent: Requests" + requestArrayList.toString());
                    Log.d(TAG, "onEvent: count" + requestArrayList.size());
                    setupRecyclerView();

                }
            }
        });



    }




    private void setupRecyclerView(){
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(NotificationActivity.this,requestArrayList);
        recyclerViewClassrooms.setAdapter(adapter);
        recyclerViewClassrooms.setLayoutManager(new LinearLayoutManager(this));
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }




    //Firebase
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        Log.d(TAG, "onStart: starting");



    }



    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }




    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: Starting");
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged: Signed_in" + user.getUid());
                    userID = user.getUid();
                    getRequests();


                } else {
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                }
            }
        };


    }




}





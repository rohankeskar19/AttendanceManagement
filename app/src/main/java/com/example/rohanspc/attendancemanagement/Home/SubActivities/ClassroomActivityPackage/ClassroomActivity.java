package com.example.rohanspc.attendancemanagement.Home.SubActivities.ClassroomActivityPackage;

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
import android.widget.TextView;

import com.example.rohanspc.attendancemanagement.RecyclerViewClassrooms;
import com.example.rohanspc.attendancemanagement.Models.ClassroomUser;
import com.example.rohanspc.attendancemanagement.Models.Classrooms;
import com.example.rohanspc.attendancemanagement.Models.User;
import com.example.rohanspc.attendancemanagement.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ClassroomActivity extends AppCompatActivity implements FragmentDialog.OnInputListener{
    private static final String TAG = "ClassroomActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user1;
    private FirebaseFirestore mRef;
    private CollectionReference usersCol,classCol;
    private User user;



    private String classroomName;
    private String classID;

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textView;

    private FloatingActionButton mOpenButton;

    private RecyclerViewClassrooms recyclerView;
    private RecyclerViewAdapter adapter;

    private  ArrayList<String> ClassroomID;
    private ArrayList<Classrooms> mClassrooms;




    @Override
    public void sendInput(String input) {
        classroomName = input;
        addClassroom();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting");
        setContentView(R.layout.activity_classrooms);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseFirestore.getInstance();
        usersCol = mRef.collection(getString(R.string.DB_users));
        classCol = mRef.collection(getString(R.string.DB_classrooms));

        mOpenButton = findViewById(R.id.fab_add_classroom);
        textView = findViewById(R.id.no_classroom_text);

        mClassrooms = new ArrayList<Classrooms>();
        ClassroomID = new ArrayList<>();

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.hideIfNotEmpty(textView);
        recyclerView.showIfEmpty(textView);


        Intent intent = getIntent();
        if(intent.hasExtra("user_info")) {
            user = new User();
            user  = (User) intent.getSerializableExtra("user_info");
        }

        Log.d(TAG, "onCreate: " + user.toString());
        setupFirebaseAuth();
        setupRecyclerView();
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryClassrooms,R.color.colorPrimaryDarkClassrooms,R.color.colorAccentClassrooms);

        if (user.getAccountType().equals("student")){
            mOpenButton.setVisibility(View.GONE);
            Log.d(TAG, "onCreate: invisible" );
        }
        init();
        }




        private void init(){
            mOpenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentDialog fragmentDialog = new FragmentDialog();
                    fragmentDialog.show(getFragmentManager(),"FragmentDialog");
                }
            });

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    ClassroomID.clear();
                    mClassrooms.clear();
                    fetchUserData();
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
                        user1 = user;
                        getUserData();
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

            mClassrooms.clear();

        }

        @Override
        protected void onStop() {
            super.onStop();

            if(mAuthListener != null){
                mAuth.removeAuthStateListener(mAuthListener);
            }
        }



        private void getUserData(){
            Log.d(TAG, "getUserData: starting");
            if(user.getClassroomID() != null) {
                ClassroomID = user.getClassroomID();
            }
            Log.d(TAG, "getUserData: " + ClassroomID);
            getQuery();
        }


        private void fetchUserData(){
            Log.d(TAG, "fetchUserData: Inside fetch user data");
                usersCol.document(user1.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            User new_user_info = documentSnapshot.toObject(User.class);
                            Log.d(TAG, "onComplete: " + new_user_info.toString());
                            recyclerView.toggleViews();
                            if(new_user_info.getClassroomID() != null) {
                                ClassroomID = new_user_info.getClassroomID();
                                Log.d(TAG, "onComplete: " + ClassroomID);
                            }
                            getQuery();
                        }
                        else{
                            Log.d(TAG, "onComplete: get failed");
                        }

                    }
                });
            Log.d(TAG, "fetchUserData: "  + ClassroomID);


            }





        private void setupRecyclerView(){
            Log.d(TAG, "setupRecyclerView: " + mClassrooms.toString());


            adapter = new RecyclerViewAdapter(this, mClassrooms,user);

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            if(swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(false);
            }
        }


        private void addClassroom(){
            DocumentReference docRef = classCol.document();
            classID = docRef.getId().substring(0,9);
            Log.d(TAG, "addClassroom: user: " + user.getName());
            final Classrooms classrooms = new Classrooms(classroomName,classID,null,user.getUserID());
            
            classCol.document(classID).set(classrooms);


            ClassroomID.add(classID);
            user.setClassroomID(ClassroomID);
            Log.d(TAG, "addClassroom: " + user.getUserID());
            usersCol.document(user.getUserID()).set(user);


            ClassroomUser classroomUser = new ClassroomUser(user.getName(),user.getUserID(),user.getEmail(),user.getAccountType(),0,false);

            classCol.document(classID).collection(getString(R.string.DB_users)).document(classroomUser.getUserID()).set(classroomUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        mClassrooms.add(classrooms);
                        adapter.notifyDataSetChanged();
                    }
                    else{

                    }
                }
            });

        }


        private void getQuery(){
            Log.d(TAG, "getQuery: Starting");
            if(!ClassroomID.isEmpty()) {
                for (String id : ClassroomID) {
                    Log.d(TAG, "getQuery: id" + id);
                    Query query = classCol.whereEqualTo("id", id);

                    query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.d(TAG, "onEvent: Exception " + e.getMessage());
                            }
                            try {
                                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                    if (queryDocumentSnapshot != null) {
                                        mClassrooms.add(queryDocumentSnapshot.toObject(Classrooms.class));
                                        Log.d(TAG, "onEvent: Classroom " + mClassrooms.toString());
                                        Log.d(TAG, "onEvent: Size " + mClassrooms.size());
                                    } else {
                                        Log.d(TAG, "onEvent: Get Query Failed");
                                    }

                                }
                                setupRecyclerView();

                            } catch (NullPointerException i) {
                                Log.d(TAG, "onEvent: " + i.getMessage());
                            }
                        }
                    });

                }
            }


        }




}

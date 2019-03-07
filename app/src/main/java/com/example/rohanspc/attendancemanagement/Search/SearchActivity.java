package com.example.rohanspc.attendancemanagement.Search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.rohanspc.attendancemanagement.RecyclerViewClassrooms;
import com.example.rohanspc.attendancemanagement.Models.Classrooms;
import com.example.rohanspc.attendancemanagement.Models.User;
import com.example.rohanspc.attendancemanagement.R;
import com.example.rohanspc.attendancemanagement.Utilities.bottomNavigationViewHelper;
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
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;


@SuppressWarnings("deprecation")
public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private static final int ACTIVITY_NUMBER = 1;

    private EditText searchEditText;
    private RecyclerViewClassrooms recyclerViewClassrooms;


    private ArrayList<Classrooms> mClassroomsList;
    private ImageView sendRequestImage;


    //Firebase
    private FirebaseFirestore mRef;
    private CollectionReference collectionReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private User user;
    private String userID;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //Firebase
        mRef = FirebaseFirestore.getInstance();
        collectionReference = mRef.collection(getString(R.string.DB_classrooms));
        searchEditText = findViewById(R.id.search_edit_text);
        recyclerViewClassrooms = findViewById(R.id.recycler_view_search);
        sendRequestImage = findViewById(R.id.send_request_image);

        user = new User();

        Log.d(TAG, "onCreate: Starting");
        setupFirebaseAuth();
        setupBottomNavigationView();
        hideKeyBoard();
        initTextListener();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


        if (intent.getFlags() == (Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION)) {
            overridePendingTransition(0, 0);
        }
    }
    /*
     * Bottom Navigation view setup
     * */

    private void initTextListener() {
        Log.d(TAG, "initTextListener: starting");

        mClassroomsList = new ArrayList<>();


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = searchEditText.getText().toString().trim();
                searchForMatch(text);
            }
        });
    }


    private void setupBottomNavigationView() {

        Log.d(TAG, "setupBottomNavigationView: setting up bottom nav view");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom_nav_bar);

        bottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        bottomNavigationViewHelper.enableNavigation(SearchActivity.this, bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);

    }


    private void hideKeyBoard() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    private void searchForMatch(String keyWord) {
        Log.d(TAG, "searchForMatch: Searching for match : " + keyWord);
        mClassroomsList.clear();

        if (keyWord.length() == 0) {

        } else {
            Query query = collectionReference.whereEqualTo("id", keyWord);

            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.d(TAG, "onEvent: Query failed" + e.toString());
                    }
                    try {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            if (doc != null) {
                                mClassroomsList.add(doc.toObject(Classrooms.class));
                                Log.d(TAG, "onEvent: " + doc.toObject(Classrooms.class).toString());
                                //Update user List View

                            } else {
                                Log.d(TAG, "onEvent: Query returned null object");
                            }
                        }
                        updateClassroomList();
                    } catch (NullPointerException i) {
                        Log.d(TAG, "onEvent: Null pointer Exception");
                    }
                }
            });


        }
    }

    private void updateClassroomList() {
        Log.d(TAG, "updateClassroomList: updating classroom list");

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(SearchActivity.this, mClassroomsList,user);
        recyclerViewClassrooms.setAdapter(adapter);
        recyclerViewClassrooms.setLayoutManager(new LinearLayoutManager(this));

    }




    //Firebase
    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);


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
                    getUserData();


                } else {
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                }
            }
        };


    }

    private void getUserData(){

        mRef.collection(getString(R.string.DB_users)).document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    user = task.getResult().toObject(User.class);
                    Log.d(TAG, "onComplete: user data retrieved");

                }

                else{
                    Log.d(TAG, "onComplete: Data retrieval failed");
                }
            }
        });



    }



}

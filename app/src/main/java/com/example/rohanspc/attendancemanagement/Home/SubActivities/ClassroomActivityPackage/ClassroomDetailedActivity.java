package com.example.rohanspc.attendancemanagement.Home.SubActivities.ClassroomActivityPackage;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ClassroomDetailedActivity extends AppCompatActivity implements RollNoFragmentDialog.OnInputListener{

    private static final String TAG = "ClassroomDetailedActivi";
    private RecyclerViewClassrooms teacherRecyclerView;
    private RecyclerViewClassrooms studentRecyclerVIew;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user1;
    private FirebaseFirestore mRef;
    private CollectionReference usersCol,classCol,ClassroomUsers;
    private User user;
    private Classrooms classrooms;
    private ClassroomUser classroomUser;


    private Toolbar toolbar;
    private ArrayList<ClassroomUser> mStudents,mTeachers;
    
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_detailed);

        teacherRecyclerView = findViewById(R.id.teachers_recyclerview);
        studentRecyclerVIew = findViewById(R.id.students_recyclerview);
        toolbar = findViewById(R.id.toolbar);

        mStudents = new ArrayList<>();
        mTeachers = new ArrayList<>();

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseFirestore.getInstance();
        usersCol = mRef.collection(getString(R.string.DB_users));
        classCol = mRef.collection(getString(R.string.DB_classrooms));

        setSupportActionBar(toolbar);

        setupFirebaseAuth();

        Log.d(TAG, "onCreate: Starting");

        Intent intent = getIntent();

        if(intent.hasExtra("classroom") && intent.hasExtra("user")) {
            classrooms = (Classrooms) intent.getSerializableExtra("classroom");
            Log.d(TAG, "onCreate: " + classrooms.toString());
            user = (User) intent.getSerializableExtra("user");
            if(user.getAccountType().equals("student")) {
                checkRollNumber();
            }
        }

        ClassroomUsers = mRef.collection(getString(R.string.DB_classrooms)).document(classrooms.getId()).collection(getString(R.string.DB_users));
        getData();

    }


    private void checkRollNumber(){
        CollectionReference collectionReference = mRef.collection(getString(R.string.DB_classrooms)).document(classrooms.getId()).collection(getString(R.string.DB_users));
        DocumentReference documentReference = collectionReference.document(user.getUserID());

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "onComplete: data retrieved");
                     classroomUser = task.getResult().toObject(ClassroomUser.class);

                    if (classroomUser.getRollNo() == 0){
                        askRollNo();
                    }
                }
                else{
                    Log.d(TAG, "onComplete: Data retrieval failed");
                }
            }
        });

    }

    private void askRollNo(){
        RollNoFragmentDialog rollNoFragmentDialog = new RollNoFragmentDialog();
        rollNoFragmentDialog.show(getFragmentManager(),"RollNoFragmentDialog");

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




                }
                else{
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                }
            }
        };
    }


    private void getData(){
        ClassroomUsers.orderBy("rollNo").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<DocumentSnapshot> classroom_users = task.getResult().getDocuments();
                    for(DocumentSnapshot documentSnapshot : classroom_users){
                        if(documentSnapshot != null){
                            if(documentSnapshot.toObject(ClassroomUser.class).getAccountType().equals("teacher")){
                                mTeachers.add(documentSnapshot.toObject(ClassroomUser.class));
                            }
                            else {
                                mStudents.add(documentSnapshot.toObject(ClassroomUser.class));
                            }
                        }
                    }
                    setupStudentsRecyclerVIew();
                    setupTeachersRecyclerView();
                }
                else{
                    Log.d(TAG, "onComplete: failed to retrieve classroom users");
                }
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.classroom_activity_menu,menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.copy_classroom_id:
                copyClassroomID();
                Toast.makeText(ClassroomDetailedActivity.this, "Classroom id copied", Toast.LENGTH_SHORT).show();
                break;
            default:
                Log.d(TAG, "onOptionsItemSelected: Default");
                break;

        }


        return super.onOptionsItemSelected(item);
    }



    public void copyClassroomID(){
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        ClipData clipData = ClipData.newPlainText("id",classrooms.getId());

        clipboardManager.setPrimaryClip(clipData);

    }

    private void setupTeachersRecyclerView(){
        RecyclerViewClassroomAdapter recyclerViewClassroomAdapterAdapter = new RecyclerViewClassroomAdapter(this,mTeachers);
        teacherRecyclerView.setAdapter(recyclerViewClassroomAdapterAdapter);
        teacherRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupStudentsRecyclerVIew(){
        RecyclerViewClassroomAdapter recyclerViewClassroomAdapterAdapter = new RecyclerViewClassroomAdapter(this,mStudents);
        studentRecyclerVIew.setAdapter(recyclerViewClassroomAdapterAdapter);
        studentRecyclerVIew.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    public void sendInput(String input) {
        int rollNo = Integer.parseInt(input);

        setRollNo(rollNo);

    }

    private void setRollNo(int rollNo){
        CollectionReference collectionReference = mRef.collection(getString(R.string.DB_classrooms)).document(classrooms.getId()).collection(getString(R.string.DB_users));
        DocumentReference documentReference = collectionReference.document(user.getUserID());
        classroomUser.setRollNo(rollNo);

        documentReference.set(classroomUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: Roll No added");
                }
                else {
                    Log.d(TAG, "onComplete: Setting roll no failed");
                }
            }
        });
    }

}

package com.example.rohanspc.attendancemanagement.Home.SubActivities.AttendanceActivityPackage.attendanceReciever;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.rohanspc.attendancemanagement.Home.SubActivities.AttendanceActivityPackage.AttendanceActivity;
import com.example.rohanspc.attendancemanagement.Home.SubActivities.ClassroomActivityPackage.FragmentDialog;
import com.example.rohanspc.attendancemanagement.Models.Attendance;
import com.example.rohanspc.attendancemanagement.RecyclerViewClassrooms;
import com.example.rohanspc.attendancemanagement.Models.ClassroomUser;
import com.example.rohanspc.attendancemanagement.Models.Classrooms;
import com.example.rohanspc.attendancemanagement.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import android.support.v7.widget.Toolbar;
import java.sql.Time;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AttendanceRecieverActivity extends AppCompatActivity implements FragmentDialogAttendance.OnInputListener, FragmentDialog.OnInputListener {
    private static final String TAG = "AttendanceRecieverActiv";
    
    private Classrooms classroom;

    private FirebaseFirestore mRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CollectionReference usersRef,classroomRef,users;
    private DocumentReference classDoc;


    private RecyclerViewClassrooms recyclerViewClassrooms;
    private ArrayList<ClassroomUser> classroomUsers;
    private RelativeLayout relativeLayout;
    private Toolbar toolbar;
    private String subject,currentSubject;
    private Spinner mSpinner;



    private TextView textView;
    private Button mButton;

    private String classroomID;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_recieving);



        mSpinner = findViewById(R.id.spinner);


        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        Log.d(TAG, "onCreate: Starting");
        Intent intent = getIntent();
        relativeLayout = findViewById(R.id.activity_attendace);
        if(intent.hasExtra("classroom")){
            classroom = (Classrooms) intent.getSerializableExtra("classroom");
        }
        setupSpinner();

        textView = findViewById(R.id.no_student_tv);
        mButton = findViewById(R.id.btn_submit);


        classroomID = classroom.getId();

        mRef = FirebaseFirestore.getInstance();
        classroomRef = mRef.collection(getString(R.string.DB_classrooms));
        classDoc = classroomRef.document(classroomID);
        usersRef = classDoc.collection(getString(R.string.DB_users));
        users = mRef.collection(getString(R.string.DB_users));

        recyclerViewClassrooms = findViewById(R.id.recycler_view);
        classroomUsers = new ArrayList<>();


        recyclerViewClassrooms.hideIfNotEmpty(textView);
        recyclerViewClassrooms.showIfEmpty(textView);
        setupFirebaseAuth();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();
                finish();
            }
        });




    }

    private void setupSpinner(){
        if(classroom.getSubjects() != null){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, classroom.getSubjects());
            mSpinner.setAdapter(adapter);
        }

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentSubject = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                classroomUsers.clear();
                getData();
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

                        getData();
                    }
                    else{
                        Log.d(TAG, "onAuthStateChanged: signed_out");
                    }
                }
            };




        
    }


    private void getData(){
        if(classroomUsers.isEmpty()) {
            usersRef.orderBy("rollNo").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();

                        for (DocumentSnapshot doc : documentSnapshots) {
                            if (doc.toObject(ClassroomUser.class).getAccountType().equals("student")) {
                                ClassroomUser user = doc.toObject(ClassroomUser.class);
                                classroomUsers.add(user);
                            }
                        }
                        Log.d(TAG, "onComplete: " + classroomUsers);

                        setupRecyclerView();
                    } else {
                        Log.d(TAG, "onComplete: Data retrieval failed");
                    }
                }
            });
        }

    }

    private void setupRecyclerView(){
        RecyclerViewAttendanceDetailAdapter recyclerViewAttendanceDetailAdapter = new RecyclerViewAttendanceDetailAdapter(AttendanceRecieverActivity.this,classroomUsers);
        recyclerViewClassrooms.setAdapter(recyclerViewAttendanceDetailAdapter);
        recyclerViewClassrooms.setLayoutManager(new LinearLayoutManager(this));
    }


    private void sendData(){

        String Date = DateFormat.getDateInstance().format(new Date());
        for(ClassroomUser classroomUser : classroomUsers){
            Attendance mAttendance = new Attendance();
            mAttendance.setUserID(classroomUser.getUserID());
            mAttendance.setPresent(classroomUser.isPresent());
;
            mAttendance.setDate(Date);


            classroomRef.document(classroom.getId()).collection(getString(R.string.DB_users)).document(mAttendance.getUserID()).collection("attendance").document("subjects").collection(currentSubject).document().set(mAttendance).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: Data added succesfully");

                    }
                    else{
                        Log.d(TAG, "onComplete: Addition of data failed");
                    }
                }
            });


            Log.d(TAG, "sendData: " + mAttendance);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_subject_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.add_subject:
                addSubject();
                break;
            default:
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void addSubject(){
        FragmentDialogAttendance fragmentDialogAttendance = new FragmentDialogAttendance();
        fragmentDialogAttendance.show(getFragmentManager(),"FragmentDialogAttendance");
    }

    @Override
    public void sendInput(String input) {
        subject = input;
        ArrayList<String> subjects = new ArrayList<>();
        if(classroom.getSubjects() != null) {
            subjects = classroom.getSubjects();
        }
        subjects.add(subject);
        classroom.setSubjects(subjects);
        classroomRef.document(classroom.getId()).set(classroom);
        setupSpinner();
    }


}

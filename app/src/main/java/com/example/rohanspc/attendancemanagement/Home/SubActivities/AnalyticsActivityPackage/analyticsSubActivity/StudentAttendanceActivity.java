package com.example.rohanspc.attendancemanagement.Home.SubActivities.AnalyticsActivityPackage.analyticsSubActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.rohanspc.attendancemanagement.Models.Attendance;
import com.example.rohanspc.attendancemanagement.Models.ClassroomUser;
import com.example.rohanspc.attendancemanagement.Models.Classrooms;
import com.example.rohanspc.attendancemanagement.Models.GraphData;
import com.example.rohanspc.attendancemanagement.R;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class StudentAttendanceActivity extends AppCompatActivity {

    private static final String TAG = "StudentAttendanceActivi";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mRef;
    private CollectionReference collectionReference,attendanceRef;

    private String subject;
    private ClassroomUser classroomUser;
    private Classrooms classroom;

    private ArrayList<Attendance> attendances;


    private TextView nameTextView,classroomName,subjectTextView,rollnoTextView,attendanceTextView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseFirestore.getInstance();
        collectionReference = mRef.collection(getString(R.string.DB_classrooms));

        nameTextView = findViewById(R.id.student_name_textview);
        classroomName = findViewById(R.id.classroom_textview);
        attendanceTextView = findViewById(R.id.attendance_textview);
        rollnoTextView = findViewById(R.id.student_rollno_textview);
        subjectTextView = findViewById(R.id.subject_textview);

        attendances = new ArrayList<>();



        Intent intent = getIntent();

        if(intent.hasExtra("classroom_user") && intent.hasExtra("subject") && intent.hasExtra("classroom")){
            classroomUser = (ClassroomUser) intent.getSerializableExtra("classroom_user");
            subject = intent.getStringExtra("subject");
            classroom = (Classrooms) intent.getSerializableExtra("classroom");
        }

        nameTextView.setText("Name: " + classroomUser.getName());
        classroomName.setText("Classroom: " + classroom.getName());
        rollnoTextView.setText("Roll No: " + classroomUser.getRollNo());
        subjectTextView.setText("Subject: " + subject);

        attendanceRef = collectionReference.document(classroom.getId()).collection(getString(R.string.DB_users))
                .document(classroomUser.getUserID()).collection("attendance")
                .document("subjects")
                .collection(subject);


        setupFirebaseAuth();



    }

    private void getAttendance(){
        attendanceRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();

                    for(DocumentSnapshot documentSnapshot : documentSnapshots){
                        attendances.add(documentSnapshot.toObject(Attendance.class));

                    }
                    calculateAttendance();
                }
                else{

                }
            }
        });

    }

    private void calculateAttendance(){


        int total = attendances.size();
        int attended = 0;
        float totalAttendance;
        for(Attendance attendance : attendances){
            if(attendance.isPresent()){
                attended++;

            }

        }

        Log.d(TAG, "calculateAttendance: attended: " + attended);
        Log.d(TAG, "calculateAttendance: total: " + total);

        Log.d(TAG, "calculateAttendance: " + attendances);
        totalAttendance =  ((float)attended / (float)total) * 100;

        Log.d(TAG, "calculateAttendance: " + totalAttendance);
        attendanceTextView.setText("Attendance: " + Float.toString(totalAttendance) + "%");


    }



    private void setupFirebaseAuth(){

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();

                if(user != null){
                    Log.d(TAG, "onAuthStateChanged: Signed in");
                    getAttendance();

                }
                else{
                    Log.d(TAG, "onAuthStateChanged: Signed out");
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

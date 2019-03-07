package com.example.rohanspc.attendancemanagement.Home.SubActivities.AnalyticsActivityPackage.analyticsSubActivity;

import android.content.Entity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.rohanspc.attendancemanagement.Models.Attendance;
import com.example.rohanspc.attendancemanagement.Models.ClassroomUser;
import com.example.rohanspc.attendancemanagement.Models.Classrooms;
import com.example.rohanspc.attendancemanagement.Models.GraphData;
import com.example.rohanspc.attendancemanagement.R;
import com.example.rohanspc.attendancemanagement.RecyclerViewClassrooms;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class AnalyticsSubActivity extends AppCompatActivity {

    private static final String TAG = "AnalyticsSubActivity";
    
    
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mRef;
    private CollectionReference collectionReference,usersRef;

    private ArrayList<ClassroomUser> classroomUsers;
    private ArrayList<Attendance> mAttendance;
    private ArrayList<GraphData> graphData;
    private RecyclerViewClassrooms recyclerViewClassrooms;

    private ClassroomUser classroomUser;
    private ArrayList<String> userIDS;



    private Classrooms classroom;
    private int presentCount,absentCount;
    private String subject;

    private ArrayList<String> Dates;


    private ProgressBar progressBar;
    private LineChart lineChart;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_analytics);
        recyclerViewClassrooms = findViewById(R.id.recycler_view);
        presentCount = 0;
        absentCount = 0;

        progressBar = findViewById(R.id.progress_bar);
        lineChart = findViewById(R.id.lineChart);

        classroomUsers = new ArrayList<>();
        mAttendance = new ArrayList<>();
        userIDS = new ArrayList<>();
        Dates = new ArrayList<>();
        graphData = new ArrayList<>();




        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseFirestore.getInstance();
        collectionReference = mRef.collection(getString(R.string.DB_classrooms));


        Intent intent = getIntent();

        if(intent.hasExtra("classroom") && intent.hasExtra("subject")){
            classroom = (Classrooms) intent.getSerializableExtra("classroom");
            subject = intent.getStringExtra("subject");
        }

        Log.d(TAG, "onCreate: subject " + subject);

        setupFirebaseAuth();
        usersRef = collectionReference.document(classroom.getId()).collection(getString(R.string.DB_users));
    }


    private void setupFirebaseAuth(){

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();

                if(user != null){
                    Log.d(TAG, "onAuthStateChanged: Signed in");
                    getUsers();

                }
                else{
                    Log.d(TAG, "onAuthStateChanged: Signed out");
                }
            }
        };
    }

    private void getSingleAttendance(){
        usersRef.document(classroomUser.getUserID()).collection("attendance").document("subjects").collection(subject).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();

                    for(DocumentSnapshot documentSnapshot : documentSnapshots){
                        mAttendance.add(documentSnapshot.toObject(Attendance.class));
                    }
//                    Log.d(TAG, "onComplete: mAttendance" + mAttendance);
                    processDates();
                }
                else{

                }
            }
        });

    }

    private void setupRecyclerView(){
        RecyclerViewClassroomAdapter recyclerViewClassroomAdapter = new RecyclerViewClassroomAdapter(AnalyticsSubActivity.this,classroomUsers,subject,classroom);

        recyclerViewClassrooms.setAdapter(recyclerViewClassroomAdapter);
        recyclerViewClassrooms.setLayoutManager(new LinearLayoutManager(this));

    }

    private void processDates(){
        int x = 0;
        for(Attendance attendance : mAttendance){
            Dates.add(attendance.getDate());
        }

        for(String date : Dates){
            x += 15;
            GraphData graphData1 = new GraphData(date,0,x,0);

            graphData.add(graphData1);
        }
        getData();
    }

    private void getData(){

        mAttendance.clear();
        for(ClassroomUser classroomUser: classroomUsers){

                collectionReference.document(classroom.getId()).collection(getString(R.string.DB_users))
                        .document(classroomUser.getUserID()).collection("attendance")
                        .document("subjects").collection(subject).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();

                            for(DocumentSnapshot documentSnapshot : documentSnapshots){
                                mAttendance.add(documentSnapshot.toObject(Attendance.class));
                            }


                            processAttendance();


                        }
                        else{
                            Log.d(TAG, "onComplete: Data retrieval failed");
                        }
                    }
                });


        }




    }

    private void processAttendance(){


        for(Attendance attendance : mAttendance){

            if(attendance.isPresent()){
                for (GraphData g : graphData){
                    if(g.getDate().equals(attendance.getDate())){
                        int i = graphData.indexOf(g);
                        int count = g.getPresentCount();

                        count++;
                        graphData.get(i).setPresentCount(count);

                    }
                }
            }


        }
        setGraph();


    }



    private void getUsers(){
        classroomUsers.clear();
        mAttendance.clear();
        graphData.clear();
        userIDS.clear();
        Dates.clear();


        progressBar.setVisibility(View.VISIBLE);
        collectionReference.document(classroom.getId()).collection("users").orderBy("rollNo").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();

                    for(DocumentSnapshot documentSnapshot : documentSnapshots){
                        if(documentSnapshot.toObject(ClassroomUser.class).getAccountType().equals("student")) {
                            if(classroomUser == null) {
                                classroomUser = documentSnapshot.toObject(ClassroomUser.class);

                            }

                            classroomUsers.add(documentSnapshot.toObject(ClassroomUser.class));
                        }
                    }
                    setupRecyclerView();
                    getSingleAttendance();
                }
                else{
                    Log.d(TAG, "onComplete: Data retrieval failed");
                }
            }
        });
    }


    private void setGraph(){

        List<Entry> entry = new ArrayList<>();
        List<LegendEntry> entries = new ArrayList<>();
        final List<String> dates = new ArrayList<>();

        for(GraphData g : graphData){
            int y = g.getPresentCount();
            g.setY(y);
            entry.add(new Entry(g.getX(),g.getY()));

//            LegendEntry legendEntry = new LegendEntry();
//            legendEntry.formColor = R.color.colorPrimaryAnalytics;
//            legendEntry.label = g.getDate();

            Log.d(TAG, "setGraph: date added");
              dates.add(g.getDate());
            Log.d(TAG, "setGraph: date size" + dates.size());
//            entries.add(legendEntry);
        }



        final LineDataSet dataSet = new LineDataSet(entry,"");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dataSet.setColor(getColor(R.color.colorPrimaryAnalytics));
        }
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setFillColor(R.color.colorAccentAnalytics);

        dataSet.setDrawFilled(true);

        LineData lineData = new LineData(dataSet);
        Legend legend = lineChart.getLegend();


        lineChart.setData(lineData);
        lineChart.setGridBackgroundColor(R.color.colorWhite);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.setVisibleXRangeMaximum((graphData.size() * 5) - 5);
        lineChart.setVisibleXRangeMinimum(5);
        lineChart.animateX(1000);


        XAxis xAxis = lineChart.getXAxis();
        Log.d(TAG, "setGraph: dates size " + dates.size());

//        xAxis.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                return dates.get((int) value);
//            }
//        });

        lineChart.invalidate();

        progressBar.setVisibility(View.GONE);

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

package com.example.rohanspc.attendancemanagement.Home.SubActivities.AnalyticsActivityPackage.analyticsSubActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.example.rohanspc.attendancemanagement.Models.Classrooms;
import com.example.rohanspc.attendancemanagement.R;
import com.example.rohanspc.attendancemanagement.RecyclerViewClassrooms;

import java.util.ArrayList;

public class SubjectsActivity extends AppCompatActivity {

    private ArrayList<String> subjects;
    private Classrooms classroom;
    private RecyclerViewClassrooms recyclerViewClassrooms;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);

        Intent intent = getIntent();
        subjects = new ArrayList<>();
        recyclerViewClassrooms = findViewById(R.id.recycler_view);

        if(intent.hasExtra("classroom")){
            classroom = (Classrooms) intent.getSerializableExtra("classroom");
        }
        subjects = classroom.getSubjects();

        setupRecyclerView();

    }

    private void setupRecyclerView(){
        RecyclerViewSubjectsAdapter adapter = new RecyclerViewSubjectsAdapter(this,subjects,classroom);
        recyclerViewClassrooms.setAdapter(adapter);
        recyclerViewClassrooms.setLayoutManager(new LinearLayoutManager(this));



    }


}

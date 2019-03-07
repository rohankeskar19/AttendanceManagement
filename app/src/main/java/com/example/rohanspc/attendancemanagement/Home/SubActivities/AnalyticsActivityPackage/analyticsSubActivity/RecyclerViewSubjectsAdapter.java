package com.example.rohanspc.attendancemanagement.Home.SubActivities.AnalyticsActivityPackage.analyticsSubActivity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.rohanspc.attendancemanagement.Models.Classrooms;
import com.example.rohanspc.attendancemanagement.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class RecyclerViewSubjectsAdapter extends RecyclerView.Adapter<RecyclerViewSubjectsAdapter.ViewHolder>{



    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> subjects;
    private Context mContext;
    private Classrooms classroom;


    public RecyclerViewSubjectsAdapter(Context mContext, ArrayList<String> subjects,Classrooms classroom) {
        this.subjects = subjects;
        this.mContext = mContext;
        this.classroom = classroom;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");
        Log.d(TAG, "onBindViewHolder: " + subjects);

        holder.nameTextView.setText(subjects.get(position));

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + subjects.get(position));
                Intent intent = new Intent(mContext,AnalyticsSubActivity.class);
                intent.putExtra("subject",subjects.get(position));
                intent.putExtra("classroom",classroom);
                mContext.startActivity(intent);
            }
        });


    }



    @Override
    public int getItemCount() {
        return subjects.size();


    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nameTextView;
        RelativeLayout layout;


        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.userTextView);
            layout = itemView.findViewById(R.id.parent_text_layout);

        }



    }


}

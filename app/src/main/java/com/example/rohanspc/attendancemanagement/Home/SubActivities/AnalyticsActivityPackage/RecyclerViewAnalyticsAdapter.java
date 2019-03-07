package com.example.rohanspc.attendancemanagement.Home.SubActivities.AnalyticsActivityPackage;

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

import com.example.rohanspc.attendancemanagement.Home.SubActivities.AnalyticsActivityPackage.analyticsSubActivity.AnalyticsSubActivity;
import com.example.rohanspc.attendancemanagement.Home.SubActivities.AnalyticsActivityPackage.analyticsSubActivity.SubjectsActivity;
import com.example.rohanspc.attendancemanagement.Home.SubActivities.AttendanceActivityPackage.attendanceReciever.AttendanceRecieverActivity;
import com.example.rohanspc.attendancemanagement.Models.Classrooms;
import com.example.rohanspc.attendancemanagement.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class RecyclerViewAnalyticsAdapter extends RecyclerView.Adapter<RecyclerViewAnalyticsAdapter.ViewHolder>{



    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<Classrooms> mClassrooms;
    private Context mContext;

    public RecyclerViewAnalyticsAdapter(Context mContext, ArrayList<Classrooms> classrooms) {
        this.mClassrooms = classrooms;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        removeDuplicates();
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");
        Log.d(TAG, "onBindViewHolder: " + mClassrooms);

        holder.nameTextView.setText(mClassrooms.get(position).getName());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + mClassrooms.get(position));
                Intent intent  = new Intent(mContext,SubjectsActivity.class);
                intent.putExtra("classroom",mClassrooms.get(position));

                mContext.startActivity(intent);
            }
        });


    }

    private void removeDuplicates(){
        Set<Classrooms> classrooms = new HashSet<>();

        classrooms.addAll(mClassrooms);

        mClassrooms.clear();
        mClassrooms.addAll(classrooms);
    }

    @Override
    public int getItemCount() {
        return mClassrooms.size();


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

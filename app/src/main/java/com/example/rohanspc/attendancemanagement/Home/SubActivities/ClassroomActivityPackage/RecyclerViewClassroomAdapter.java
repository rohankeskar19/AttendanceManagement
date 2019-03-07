package com.example.rohanspc.attendancemanagement.Home.SubActivities.ClassroomActivityPackage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.rohanspc.attendancemanagement.Models.ClassroomUser;
import com.example.rohanspc.attendancemanagement.R;

import java.util.ArrayList;

public class RecyclerViewClassroomAdapter extends RecyclerView.Adapter<RecyclerViewClassroomAdapter.ViewHolder>{



    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<ClassroomUser> mClassroomUser;
    private Context mContext;

    public RecyclerViewClassroomAdapter(Context mContext, ArrayList<ClassroomUser> ClassroomUser) {
        this.mClassroomUser = ClassroomUser;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_classroom,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");

        holder.nameTextView.setText(mClassroomUser.get(position).getName());



    }

    @Override
    public int getItemCount() {
        return mClassroomUser.size();


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

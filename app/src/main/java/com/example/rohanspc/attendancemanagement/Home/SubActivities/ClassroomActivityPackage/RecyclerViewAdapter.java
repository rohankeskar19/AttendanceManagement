package com.example.rohanspc.attendancemanagement.Home.SubActivities.ClassroomActivityPackage;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.rohanspc.attendancemanagement.Models.Classrooms;
import com.example.rohanspc.attendancemanagement.Models.User;
import com.example.rohanspc.attendancemanagement.R;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{



    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<Classrooms> mClassrooms;
    private Context mContext;
    private User user;

    public RecyclerViewAdapter(Context mContext,ArrayList<Classrooms> mClassrooms,User user) {
        this.mClassrooms = mClassrooms;
        this.mContext = mContext;
        this.user = user;
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

        holder.nameTextView.setText(mClassrooms.get(position).getName());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + mClassrooms.get(position));

                Intent intent = new Intent(mContext,ClassroomDetailedActivity.class);
                intent.putExtra("classroom", (Serializable) mClassrooms.get(position));
                intent.putExtra("user",user);
                mContext.startActivity(intent);
            }
        });


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

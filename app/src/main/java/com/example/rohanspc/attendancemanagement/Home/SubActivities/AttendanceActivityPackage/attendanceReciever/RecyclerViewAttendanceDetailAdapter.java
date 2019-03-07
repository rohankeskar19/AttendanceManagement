package com.example.rohanspc.attendancemanagement.Home.SubActivities.AttendanceActivityPackage.attendanceReciever;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.rohanspc.attendancemanagement.Models.ClassroomUser;
import com.example.rohanspc.attendancemanagement.R;

import java.util.ArrayList;

public class RecyclerViewAttendanceDetailAdapter extends RecyclerView.Adapter<RecyclerViewAttendanceDetailAdapter.ViewHolder>{



    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<ClassroomUser> mUsers;
    private Context mContext;

    public RecyclerViewAttendanceDetailAdapter(Context mContext, ArrayList<ClassroomUser> mUsers) {
        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_attendance,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");

        holder.nameTextView.setText(mUsers.get(position).getName());


        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.checkBox.isChecked() && !mUsers.get(position).isPresent()){
                    mUsers.get(position).setPresent(true);
                }
                else{
                    mUsers.get(position).setPresent(false);
                }
            }
        });
        }




    @Override
    public int getItemCount() {
        return mUsers.size();


    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nameTextView;
        RelativeLayout layout;
        LinearLayout linearLayout;
        CheckBox checkBox;


        public ViewHolder(View itemView) {
            super(itemView);


            linearLayout = itemView.findViewById(R.id.checkboxLinearLayout);
            checkBox = itemView.findViewById(R.id.attendance_checkbox);
            nameTextView = itemView.findViewById(R.id.userTextView);
            layout = itemView.findViewById(R.id.parent_text_layout);

        }





    }




}

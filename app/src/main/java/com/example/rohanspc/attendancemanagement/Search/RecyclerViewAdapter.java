package com.example.rohanspc.attendancemanagement.Search;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rohanspc.attendancemanagement.Home.SubActivities.ClassroomActivityPackage.ClassroomDetailedActivity;
import com.example.rohanspc.attendancemanagement.Models.Classrooms;
import com.example.rohanspc.attendancemanagement.Models.Request;
import com.example.rohanspc.attendancemanagement.Models.User;
import com.example.rohanspc.attendancemanagement.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{


    FirebaseFirestore mRef;
    CollectionReference collectionReference;
    DocumentReference documentReference;
    private String requestID;

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<Classrooms> mClassrooms;
    private Context mContext;
    private User user;



    public RecyclerViewAdapter(Context mContext, ArrayList<Classrooms> mClassrooms,User user1) {
        this.mClassrooms = mClassrooms;
        this.mContext = mContext;
        mRef = FirebaseFirestore.getInstance();
        collectionReference = mRef.collection(mContext.getString(R.string.DB_requests));
        documentReference = collectionReference.document();
        user = new User();
        user = user1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_search,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");

        holder.ClassroomNameTextView.setText(mClassrooms.get(position).getName());

        ArrayList<String> classroomIDS = new ArrayList<>();



        boolean hasClassroom = false;

        final boolean hasClassroom1;
        if(user.getClassroomID() != null) {
            classroomIDS = user.getClassroomID();
        }
        Log.d(TAG, "onBindViewHolder: " + classroomIDS);
        for(String id : classroomIDS){
            if(id.equals(mClassrooms.get(position).getId())){
                holder.sendRequestImageView.setVisibility(View.GONE);
                hasClassroom = true;

            }
        }
        hasClassroom1 = hasClassroom;

        holder.ClassroomNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(hasClassroom1) {
                    Intent intent = new Intent(mContext, ClassroomDetailedActivity.class);
                    intent.putExtra("classroom", (Serializable) mClassrooms.get(position));
                    mContext.startActivity(intent);
                }
            }
        });

        holder.sendRequestImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            sendRequest(mClassrooms.get(position),holder);


            }
        });




    }

    @Override
    public int getItemCount() {
        return mClassrooms.size();


    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView ClassroomNameTextView;
        ImageView sendRequestImageView;
        RelativeLayout layout;


        public ViewHolder(View itemView) {
            super(itemView);

            sendRequestImageView = itemView.findViewById(R.id.send_request_image);
            layout = itemView.findViewById(R.id.parent_text_layout);
            ClassroomNameTextView = itemView.findViewById(R.id.classroomSearchTextView);

        }



    }

    private void sendRequest(Classrooms classrooms, final ViewHolder holder){
        requestID = documentReference.getId().substring(0,9);
        Request request = new Request(user.getUserID(),classrooms.getAuthor(),classrooms.getId(),mContext.getString(R.string.request_state_unaccepted),requestID,user.getName(),classrooms.getName());

        collectionReference.document(requestID).set(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(mContext,"Request sent",Toast.LENGTH_SHORT).show();
                    holder.sendRequestImageView.setVisibility(View.GONE);
                }
                else {
                    Toast.makeText(mContext,"Failed to send request",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}

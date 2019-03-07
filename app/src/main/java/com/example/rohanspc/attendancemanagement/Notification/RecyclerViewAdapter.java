package com.example.rohanspc.attendancemanagement.Notification;

import android.content.Context;
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

import com.example.rohanspc.attendancemanagement.Models.ClassroomUser;
import com.example.rohanspc.attendancemanagement.Models.Classrooms;
import com.example.rohanspc.attendancemanagement.Models.Request;
import com.example.rohanspc.attendancemanagement.Models.User;
import com.example.rohanspc.attendancemanagement.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{



    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<Request> mRequests;
    private Context mContext;

    private FirebaseFirestore mRef;
    private CollectionReference requestCollectionReferance,usersCollectionReference,classroomCollectionReference;




    public RecyclerViewAdapter(Context mContext, ArrayList<Request> mRequests) {
        this.mRequests = mRequests;
        this.mContext = mContext;

        mRef = FirebaseFirestore.getInstance();
        requestCollectionReferance = mRef.collection(mContext.getString(R.string.DB_requests));
        usersCollectionReference = mRef.collection(mContext.getString(R.string.DB_users));
        classroomCollectionReference = mRef.collection(mContext.getString(R.string.DB_classrooms));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_requests,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");

        holder.RequestNameTextView.setText(mRequests.get(position).getFromUserName());
        holder.ClassroomNameTextView.setText(mRequests.get(position).getClassroomName());

        holder.acceptRequestImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            acceptRequest(mRequests.get(position));

            }
        });


        holder.rejectRequestImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            rejectRequest(mRequests.get(position));


            }
        });

        Log.d(TAG, "onBindViewHolder: Size" + mRequests.size());
        Log.d(TAG, "onBindViewHolder: " + mRequests.toString());




    }

    @Override
    public int getItemCount() {
        return mRequests.size();


    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView RequestNameTextView,ClassroomNameTextView;

        ImageView acceptRequestImageView,rejectRequestImageView;
        RelativeLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            RequestNameTextView = itemView.findViewById(R.id.requestTextView);
            ClassroomNameTextView = itemView.findViewById(R.id.notification_classroom_name);

            acceptRequestImageView = itemView.findViewById(R.id.request_accept);
            rejectRequestImageView = itemView.findViewById(R.id.request_reject);
            layout = itemView.findViewById(R.id.parent_text_layout_notification);


        }

    }

    private void acceptRequest(final Request request){
        getUserData(request);

        deleteDocument(request);
    }


    private void rejectRequest(Request request){

        deleteDocument(request);
    }

    private void deleteDocument(Request request){
        Log.d(TAG, "deleteDocument: " + request.getRequestID());
        requestCollectionReferance.document(request.getRequestID()).delete();
        mRequests.remove(request);
        notifyDataSetChanged();



    }

    private void getUserData(final Request request){
        usersCollectionReference.document(request.getFromUserID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = new User();
                    user = task.getResult().toObject(User.class);
                    Log.d(TAG, "onComplete: " + user.toString());

                    setObject(user, request);

                }
                else {
                    Log.d(TAG, "onComplete: " + task.getException());
                }
            }
        });
    }



    private void setObject(User user,Request request){
        ArrayList<String> classroomID = new ArrayList<>();
        try{
            if (user.getClassroomID() != null) {
                classroomID = user.getClassroomID();
            }
        }catch (NullPointerException e){
            Log.d(TAG, "onComplete: " + e.getMessage());
        }
        classroomID.add(request.getClassroomID());
        user.setClassroomID(classroomID);
        usersCollectionReference.document(request.getFromUserID()).set(user);
        ClassroomUser classroomUser = new ClassroomUser(user.getName(),user.getUserID(),user.getEmail(),user.getAccountType(),0,false);
        classroomCollectionReference.document(request.getClassroomID()).collection(mContext.getString(R.string.DB_users)).document(request.getFromUserID()).set(classroomUser);

    }
}

package com.example.rohanspc.attendancemanagement.Home;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.rohanspc.attendancemanagement.Home.SubActivities.AnalyticsActivityPackage.AnalyticsActivity;
import com.example.rohanspc.attendancemanagement.Home.SubActivities.AttendanceActivityPackage.AttendanceActivity;
import com.example.rohanspc.attendancemanagement.Home.SubActivities.ClassroomActivityPackage.ClassroomActivity;
import com.example.rohanspc.attendancemanagement.Home.SubActivities.NotificationSubActivityPackage.NotificationSubActivity;
import com.example.rohanspc.attendancemanagement.Login.LoginActivity;
import com.example.rohanspc.attendancemanagement.Models.Attendance;
import com.example.rohanspc.attendancemanagement.Models.User;
import com.example.rohanspc.attendancemanagement.R;
import com.example.rohanspc.attendancemanagement.Utilities.bottomNavigationViewHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int ACTIVITY_NUMBER = 0;
    private User user_info;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user1;
    private FirebaseFirestore mRef;
    private CollectionReference collectionReference;

    private TextView mClassroomDesc,mAnalyticsDesc,mNotificationDesc,mAttendanceDesc,mNotificationDescStudent;
    private CardView attendanceCardView,notificationsCardView,notificationsCardViewStudent;

    private ProgressBar mProgressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting");
        mClassroomDesc = findViewById(R.id.classroom_description);
        mAnalyticsDesc = findViewById(R.id.analytics_description);
        mNotificationDesc = findViewById(R.id.notification_description);
        mAttendanceDesc = findViewById(R.id.attendance_description);
        mProgressBar = findViewById(R.id.progress_bar);
        mNotificationDescStudent = findViewById(R.id.notification_description_student);

        mAuth = FirebaseAuth.getInstance();
        mProgressBar.setVisibility(View.VISIBLE);

        mRef = FirebaseFirestore.getInstance();
        collectionReference = mRef.collection("users");

        attendanceCardView = findViewById(R.id.attendance);
        notificationsCardView = findViewById(R.id.notifications);
        notificationsCardViewStudent = findViewById(R.id.notificationsStudent);

        setupFirebaseAuth();
        setupBottomNavigationView();



    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


        if(intent.getFlags() == (Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION)){
            overridePendingTransition(0,0);
        }
    }

    /*
    * Bottom Navigation view setup
    * */

    private void setupBottomNavigationView(){

        Log.d(TAG, "setupBottomNavigationView: setting up bottom nav view");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom_nav_bar);

        bottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        bottomNavigationViewHelper.enableNavigation(MainActivity.this,bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);

    }


/*
* ----------------------------------------------Firebase------------------------------------------------
*
* */




   private void setupFirebaseAuth(){
       Log.d(TAG, "setupFirebaseAuth: Starting");
       mAuth = FirebaseAuth.getInstance();

       mAuthListener = new FirebaseAuth.AuthStateListener() {
           @Override
           public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
               FirebaseUser user = firebaseAuth.getCurrentUser();

//               checkUser(user);
               if(user != null){
                   Log.d(TAG, "onAuthStateChanged: Signed_in " + user.getUid());
                    user1 = user;

                   try {
                       Log.d(TAG, "getting user data" );
                       getData(user1);


                   }
                   catch (NullPointerException e){
                       Log.d(TAG, "onStart: Null pointer exception");
                   }

               }
               else{
                   Log.d(TAG, "onAuthStateChanged: signed_out");

                   Intent intent  = new Intent(MainActivity.this, LoginActivity.class);
                   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                   startActivity(intent);
               }
           }
       };

   }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Starting");
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Starting");
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }

    private void getData(FirebaseUser user){

        DocumentReference documentReference = collectionReference.document(user.getUid());

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        user_info = documentSnapshot.toObject(User.class);
                        Log.d(TAG, "onComplete: User: get Data" + user_info.toString());
                        setUserData(user_info);
                        }
                    else {
                        Log.d(TAG, "onComplete: No such document");
                    }

                }
                else {
                    Log.d(TAG, "onComplete: Get request failed");
                }

                
            }
        });
        }


       private void setUserData(User user){

           Log.d(TAG, "setUserData: starting");
           if(user.getAccountType().equals("student")){
               mClassroomDesc.setText(getString(R.string.classroom_desc_student));
               mAnalyticsDesc.setText(getString(R.string.analytics_desc_student));
               mNotificationDescStudent.setText(getString(R.string.notification_desc_student));
               mAttendanceDesc.setText(getString(R.string.attendance_desc_student));
               attendanceCardView.setVisibility(View.INVISIBLE);
               notificationsCardView.setVisibility(View.INVISIBLE);
                notificationsCardViewStudent.setVisibility(View.VISIBLE);

           }
           else if(user.getAccountType().equals("teacher")){
               mClassroomDesc.setText(getString(R.string.classroom_desc_teacher));
               mAnalyticsDesc.setText(getString(R.string.analytics_desc_teacher));
               mNotificationDesc.setText(getString(R.string.notification_desc_teacher));
               mAttendanceDesc.setText(getString(R.string.attendance_desc_teacher));
           }

            mProgressBar.setVisibility(View.GONE);

       }



       public void startSubActivityOnClick(View v){

            switch(v.getId()){
                case R.id.classroom:
                        Intent intent1 = new Intent(MainActivity.this, ClassroomActivity.class);
                        intent1.putExtra("user_info",user_info);
                        startActivity(intent1);
                        break;
                case R.id.analytics:
                        Intent intent2 = new Intent(MainActivity.this, AnalyticsActivity.class);
                        intent2.putExtra("user_info",user_info);
                        startActivity(intent2);
                    break;
                case R.id.notifications:
                        Intent intent3 = new Intent(MainActivity.this, NotificationSubActivity.class);
                        intent3.putStringArrayListExtra("classrooms",user_info.getClassroomID());
                        startActivity(intent3);

                    break;
                case R.id.attendance:
                        Intent intent4 = new Intent(MainActivity.this, AttendanceActivity.class);

                        intent4.putStringArrayListExtra("classroomID",user_info.getClassroomID());
                        startActivity(intent4);

                    break;
                case R.id.notificationsStudent:
                    Intent intent5 = new Intent(MainActivity.this, NotificationSubActivity.class);
                    intent5.putStringArrayListExtra("classrooms",user_info.getClassroomID());
                    startActivity(intent5);
                    break;


            }


       }
    /*----------------------------------------------------------------------------------------------------*/
}

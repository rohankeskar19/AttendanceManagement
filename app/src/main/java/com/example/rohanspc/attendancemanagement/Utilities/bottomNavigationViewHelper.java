package com.example.rohanspc.attendancemanagement.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.example.rohanspc.attendancemanagement.Account.AccountActivity;
import com.example.rohanspc.attendancemanagement.Home.MainActivity;
import com.example.rohanspc.attendancemanagement.Notification.NotificationActivity;
import com.example.rohanspc.attendancemanagement.R;
import com.example.rohanspc.attendancemanagement.Search.SearchActivity;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class bottomNavigationViewHelper {
    private static final String TAG = "bottomNavigationViewHel";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG, "setupBottomNavigationView: Starting");
        
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }


    public static void enableNavigation(final Context context, BottomNavigationViewEx viewEx){
        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.ic_home:

                        Intent intent1 = new Intent(context, MainActivity.class);

                        intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent1);


                        break;

                    case R.id.ic_search:

                        Intent intent2 = new Intent(context, SearchActivity.class);

                        intent2.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent2);

                        break;

                    case R.id.ic_notifications:

                        Intent intent3 = new Intent(context, NotificationActivity.class);

                        intent3.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent3);

                        break;

                    case R.id.ic_account:

                        Intent intent4 = new Intent(context, AccountActivity.class);

                        intent4.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intent4.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent4);

                        break;

                }


                return false;
            }
        });
    }
}

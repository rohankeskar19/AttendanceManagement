package com.example.rohanspc.attendancemanagement.Home.SubActivities.NotificationSubActivityPackage;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.rohanspc.attendancemanagement.Models.Notification;
import com.example.rohanspc.attendancemanagement.R;

import java.util.ArrayList;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private ArrayList<Notification> mNotifications;

    public ExpandableListAdapter(Context mContext, ArrayList<Notification> mNotifications) {
        this.mContext = mContext;
        this.mNotifications = mNotifications;
    }

    @Override
    public int getGroupCount() {
        return mNotifications.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return mNotifications.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return mNotifications.get(i).getDescription();
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
         Notification notification = (Notification) getGroup(i);

         String headerTitle = notification.getTitle();
         String date = notification.getDate();
         String hour = Integer.toString(notification.getHour());
         String min = Integer.toString(notification.getMiinute());

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.list_group,null);
        }

        TextView header = view.findViewById(R.id.listHeader);
        TextView dateTextView = view.findViewById(R.id.dateTextView);
        TextView timeTextView = view.findViewById(R.id.timeTextView);

        String time = hour + " : " + min;


        header.setText(headerTitle);

        dateTextView.setText(date);
        timeTextView.setText(time);

        return  view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        String childText = (String) getChild(i,i1);

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.dropdown_list_item,null);
        }
        TextView child = view.findViewById(R.id.descriptionTextView);

        child.setText(childText);

        return view;

    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}

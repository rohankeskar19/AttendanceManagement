package com.example.rohanspc.attendancemanagement;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RecyclerViewClassrooms extends RecyclerView {
    private static final String TAG = "RecyclerViewClassrooms";
    private List<View> viewsToHide = Collections.emptyList();
    private List<View> viewsToShow = Collections.emptyList();
    
    public RecyclerViewClassrooms(Context context) {
        super(context);


    }

    public RecyclerViewClassrooms(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewClassrooms(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

        if(adapter != null){
            adapter.registerAdapterDataObserver(dataObserver);
            toggleViews();
        }
        dataObserver.onChanged();
    }

    public void showIfEmpty(View... views){
        viewsToShow = Arrays.asList(views);
    }

    public void hideIfNotEmpty(View... views){
        viewsToHide = Arrays.asList(views);
    }

    public void toggleViews(){
        Log.d(TAG, "toggleViews: starting");
        if(!viewsToHide.isEmpty() && !viewsToShow.isEmpty()) {
            if (getAdapter().getItemCount() == 0) {
                Log.d(TAG, "toggleViews: Recycler view invisible text view visible");
                for (View v : viewsToShow) {
                    v.setVisibility(VISIBLE);
                }
                setVisibility(View.GONE);
            } else {
                Log.d(TAG, "toggleViews: Recycler view visible text view invisible");
                for (View v : viewsToShow) {
                    v.setVisibility(View.GONE);
                }
                setVisibility(VISIBLE);
            }
        }
    }

    RecyclerView.AdapterDataObserver dataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            Log.d(TAG, "onChanged: Starting");
            toggleViews();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {

            Log.d(TAG, "onItemRangeChanged: starting");
            toggleViews();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            Log.d(TAG, "onItemRangeChanged: Starting");
            toggleViews();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {

            Log.d(TAG, "onItemRangeInserted: Starting");
            toggleViews();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {

            Log.d(TAG, "onItemRangeRemoved: Starting");
            toggleViews();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            Log.d(TAG, "onItemRangeMoved: Starting");
            toggleViews();
        }
    };
}

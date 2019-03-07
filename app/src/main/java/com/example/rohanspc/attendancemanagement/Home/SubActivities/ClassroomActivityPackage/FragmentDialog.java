package com.example.rohanspc.attendancemanagement.Home.SubActivities.ClassroomActivityPackage;






import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;



import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;

import com.example.rohanspc.attendancemanagement.R;

public class FragmentDialog extends DialogFragment {
    private static final String TAG = "FragmentDialog";
    private EditText nameEditText;
    private Button mbtnSubmit,mbtnCancel;

    public interface OnInputListener{
        void sendInput(String input);
    }
    public OnInputListener mOnInputSelected;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starting dialog");

        View view = inflater.inflate(R.layout.layout_input_dialog_classroom,container,false);

        nameEditText = view.findViewById(R.id.edit_classroom_name);
        mbtnSubmit = view.findViewById(R.id.btn_ok);
        mbtnCancel = view.findViewById(R.id.btn_cancel);
        init();

        return view;
    }

    private void init(){
        Log.d(TAG, "init: starting");
        mbtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Adding");

                String input = nameEditText.getText().toString();
//
               if(!input.equals("")){
                   mOnInputSelected.sendInput(input);
               }

                getDialog().dismiss();
            }
        });

        mbtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Closing");
                getDialog().dismiss();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mOnInputSelected = (OnInputListener) getActivity();
        }
        catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException" + e.toString() );
        }
    }
}

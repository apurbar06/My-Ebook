package com.example.myebook.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.myebook.R;

public class ReadableSubjectListAdapter extends BaseAdapter {

    private final Activity mContext;
    private static String[] mSubjects;
    private boolean mForDelete;
    public boolean[] mCheckBoxState;

    public ReadableSubjectListAdapter(Activity context, String[] subjects, boolean forDelete) {
        super();

        mContext = context;
        mSubjects = subjects;
        mForDelete = forDelete;
        mCheckBoxState = new boolean[subjects.length];
    }

    public static String getItemAtPosition(int position) {
        return mSubjects[position];
    }


    @Override
    public int getCount() {
        return mSubjects.length;
    }

    @Override
    public  String getItem(int position) {
        return mSubjects[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // inflate the layout for each item of mGridView
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.readable_subject, parent, false);

        // get the references
        TextView titleText = (TextView) convertView.findViewById(R.id.subject_text);
        final CheckBox checkBox = convertView.findViewById(R.id.subject_check_box);


        // if for delete add external listener in list view and check box
        if (mForDelete) {
            // view listener
            View.OnClickListener viewListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean checked = checkBox.isChecked();
                    if (checked) {
                        // if checkbox was previously checked make mCheckBoxState false and uncheck the checkbox
                        checkBox.setChecked(false);
                        mCheckBoxState[position]=false;
                    } else {
                        // if checkbox was previously unchecked make mCheckBoxState true and check the checkbox
                        checkBox.setChecked(true);
                        mCheckBoxState[position]=true;
                    }
                }
            };
            // checkbox listener
            View.OnClickListener checkBoxListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean checked = checkBox.isChecked();
                    if (checked) {
                        // if checkbox is checked make mCheckBoxState true
                        mCheckBoxState[position]=true;
                    } else {
                        // if checkbox is unchecked make mCheckBoxState false
                        mCheckBoxState[position]=false;
                    }
                }
            };
            //  if it is for delete show checkbox
            checkBox.setVisibility(View.VISIBLE);
            // attach the listener
            convertView.setOnClickListener(viewListener);
            // attach the listener
            checkBox.setOnClickListener(checkBoxListener);
        } else {
            //  if it is not for delete hide checkbox
            checkBox.setVisibility(View.GONE);
        }

        // Set the title
        titleText.setText(mSubjects[position]);


        return convertView;
    }
}

package com.example.myebook.handler;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myebook.R;

public class ReadableSubjectListAdapter extends BaseAdapter {

    private final Activity mContext;
    private final String[] mSubjects;

    public ReadableSubjectListAdapter(Activity context, String[] subjects) {
        super();

        mContext = context;
        mSubjects = subjects;
    }


    @Override
    public int getCount() {
        return mSubjects.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // inflate the layout for each item of mGridView
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.readable_subject, parent, false);

        // get the reference of textView
        TextView titleText = (TextView) convertView.findViewById(R.id.grid_subject_text);
        // Set the title and button action
        titleText.setText(mSubjects[position]);


        return convertView;
    }
}

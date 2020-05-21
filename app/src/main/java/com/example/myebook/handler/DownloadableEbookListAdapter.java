package com.example.myebook.handler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.ListAdapter;

import com.example.myebook.R;


public class DownloadableEbookListAdapter extends BaseAdapter{

    private static final String TAG = "DownloadableEbookListAdapter";
    private final Activity context;
    private final String[] title;

    public DownloadableEbookListAdapter(Activity context, String[] title) {

        super();

        this.context = context;
        this.title = title;
    }

    @Override
    public int getCount() {
        // return the number of records
        return title.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    // getView method is called for each item of ListView
    public View getView(int position, View view, ViewGroup parent) {

        // inflate the layout for each item of listView
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.downloadable_ebook, parent, false);


        // get the reference of textView and button
        TextView titleText = (TextView) view.findViewById(R.id.downloadable_title);
        ImageButton downloadBTN = (ImageButton) view.findViewById(R.id.downloadable_btn);

        // Set the title and button action
        titleText.setText(title[position]);
        // titleText.setText(title.get(position));
        downloadBTN.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button is clicked");
            }
        });




        return view;

    };
}

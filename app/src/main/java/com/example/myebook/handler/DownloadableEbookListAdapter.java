package com.example.myebook.handler;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.myebook.R;


public class DownloadableEbookListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] title;

    public DownloadableEbookListAdapter(Activity context, String[] title) {

        super(context, R.layout.downloadable_ebook, title);

        this.context = context;
        this.title = title;
    }

    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.downloadable_ebook, null,true);


        TextView titleText = (TextView) rowView.findViewById(R.id.downloadable_title);


        titleText.setText(title[position]);


        return rowView;

    };
}

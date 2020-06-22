package com.example.myebook.handler;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ReadableEbookListAdapter extends BaseAdapter {


    private final Activity mContext;
    private final String mSubject;
    private final String[] mEbooks;

    public ReadableEbookListAdapter(Activity context, String subject, String[] ebooks) {
        super();

        mContext = context;
        mSubject = subject;
        mEbooks = ebooks;
    }

    @Override
    public int getCount() {
        return mEbooks.length;
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
        TextView dummyTextView = new TextView(mContext);
        dummyTextView.setText(String.valueOf(position));
        return dummyTextView;
    }
}

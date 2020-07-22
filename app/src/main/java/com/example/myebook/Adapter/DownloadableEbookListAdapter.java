package com.example.myebook.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myebook.Handler.Downloader;
import com.example.myebook.R;


public class DownloadableEbookListAdapter extends BaseAdapter {

    private static final String TAG = "DownloadableEbookListAdapter";
    private final Activity mContext;
    private final String mSubject;
    private final String[] mTitle;
    private final String[] mURL;
    public Downloader d;
    private AlphaAnimation buttonClicked = new AlphaAnimation(0.2f, 1.0f);

    public DownloadableEbookListAdapter(Activity context, String subject, String[] title, String[] url) {

        super();

        mContext = context;
        mSubject = subject;
        mTitle = title;
        mURL = url;
    }

    @Override
    public int getCount() {
        // return the number of records
        return mTitle.length;
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
    public View getView(final int position, View view, ViewGroup parent) {

        // inflate the layout for each item of listView
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.downloadable_ebook, parent, false);


        // get the reference of textView and button
        TextView titleText = (TextView) view.findViewById(R.id.downloadable_title);
        ImageButton downloadBTN = (ImageButton) view.findViewById(R.id.downloadable_btn);

        // Set the title and button action
        titleText.setText(mTitle[position]);
        downloadBTN.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button is clicked");

                //to make effect when button is clicked
                buttonClicked.setDuration(500);
                buttonClicked.setStartOffset(100);
                buttonClicked.setFillAfter(true);
                v.startAnimation(buttonClicked);

                if(checkInternetConnection()) {
                    //download pdf using new thread
                    d = new Downloader(mContext);
                    d.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mURL[position], mSubject, mTitle[position]);
                } else {
                    Toast.makeText(mContext, "Check internet connectivity", Toast.LENGTH_LONG).show();
                }

            }
        });

        return view;

    }

    boolean cancel(){
        return d.cancel(true);
    }



    private boolean checkInternetConnection() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connectivityManager
                =(ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check for network connections
        if (    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }else {
            return false;
        }
    }



}

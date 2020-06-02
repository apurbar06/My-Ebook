package com.example.myebook.handler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myebook.R;


public class DownloadableEbookListAdapter extends BaseAdapter{

    private static final String TAG = "DownloadableEbookListAdapter";
    private final Activity mContext;
    private final String[] mTitle;
    private final String[] mURL;

    public DownloadableEbookListAdapter(Activity context, String[] title, String[] url) {

        super();

        this.mContext = context;
        this.mTitle = title;
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
        // titleText.setText(mTitle.get(position));
        downloadBTN.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button is clicked");



//                DownloadManager downloadmanager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
//                Uri uri = Uri.parse(mURL[position]);
//
//                DownloadManager.Request request = new DownloadManager.Request(uri);
//                request.setTitle("My pdf");
//                request.setDescription("Downloading");
//                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                request.setVisibleInDownloadsUi(false);
//                request.setDestinationUri(Uri.parse("file://" + "Ebook" + "/mypdf.pdf"));
//
//                downloadmanager.enqueue(request);
//                new DownloadEbookFromURL().doInBackground("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf");

                new Downloader().execute("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf", "test.pdf");

            }
        });




        return view;

    };
}

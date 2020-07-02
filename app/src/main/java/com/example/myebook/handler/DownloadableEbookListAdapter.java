package com.example.myebook.handler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myebook.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;


public class DownloadableEbookListAdapter extends BaseAdapter{

    private static final String TAG = "DownloadableEbookListAdapter";
    private final Activity mContext;
    private final String mSubject;
    private final String[] mTitle;
    private final String[] mURL;


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

                //download pdf using new thread
                new Downloader().executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR, "https://www.nhc.noaa.gov/tafb_latest/USA_latest.pdf", mSubject, mTitle[position]+".pdf" );



            }
        });


        return view;

    };


    /**
     * This will execute after every click on download button
     */
    private class Downloader extends AsyncTask<String, Integer, Void> {

        private static final int  MEGABYTE = 1024 * 1024;
        NotificationManagerCompat notificationManager;
        NotificationCompat.Builder builder;
        private static final String CHANNEL_ID = "id";
        int notificationId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);



        protected void onPreExecute(){
            super.onPreExecute();
            notificationManager = NotificationManagerCompat.from(mContext);
            builder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
            builder.setContentTitle("My Ebook")
                    .setContentText("Download in progress")
                    .setSmallIcon(R.drawable.ic_e)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_LOW);

            // Issue the initial notification with zero progress
            int PROGRESS_MAX = 100;
            int PROGRESS_CURRENT = 0;
            builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
            notificationManager.notify(notificationId, builder.build());

        }


        @Override
        protected Void doInBackground(String... strings) {

            String fileUrl = strings[0];   // the url for download the pdf
            String fileFolder = strings[1];  //subject name
            String fileName = strings[2];  // pdf file name
            builder.setContentTitle(fileName);
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "My Ebook/" + fileFolder);
            if(!folder.exists()){
                folder.mkdirs();
            }

            File pdfFile = new File(folder, fileName);
            try{
                pdfFile.createNewFile();
            }catch (IOException e){
//            e.printStackTrace();
            }

            downloadFile(fileUrl, pdfFile);
            return null;
        }


        public void downloadFile(String fileUrl, File directory){

            try {

                URL url = new URL(fileUrl);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                //urlConnection.setRequestMethod("GET");
                //urlConnection.setDoOutput(true);
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(directory);
                int totalSize = urlConnection.getContentLength();

                byte[] buffer = new byte[MEGABYTE];
                int bufferLength = 0;
                long total = 0;
                while((bufferLength = inputStream.read(buffer))>0 ){
                    total += bufferLength;
                    // publishing the progress
                    publishProgress((int)((total*100)/totalSize));
                    // writing data to output file
                    fileOutputStream.write(buffer, 0, bufferLength);
                }
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        protected void onProgressUpdate(Integer... progress) {

            builder.setProgress(100, progress[0], false);
            // Displays the progress bar on notification
            notificationManager.notify(notificationId, builder.build());
        }


        protected void onPostExecute(Void result){
            builder.setContentText("Download complete")
                   .setOngoing(false);
            // Removes the progress bar
            builder.setProgress(0,0,false);
            notificationManager.notify(notificationId, builder.build());
        }

    }





}

package com.example.myebook.Handler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myebook.BroadcastReceiver.NotificationActionReceiver;
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

public class Downloader extends AsyncTask<String, Integer, Void> {

    private Activity mContext;
    private String mGraduationLevel;
    private String mCourse;
    private String mSemester;
    private SharedPreferences mSharedPreferences;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder builder;
    private static final String CHANNEL_ID = "id";
    private int notificationId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
    private static final int  MEGABYTE = 1024 * 1024;


    public Downloader(Activity context) {
        mContext = context;
    }


//    Intent intentPause = new Intent(mContext, NotificationActionReceiver.class).setAction("PAUSE");
////        intentConfirm.setAction("CONFIRM");
////        intentConfirm.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//
//
//    Intent intentCancel = new Intent(mContext, NotificationActionReceiver.class).setAction("CANCEL");
////        intentCancel.setAction("CANCEL");
////        intentCancel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//
//    //This Intent will be called when Pause button from notification will be
//    //clicked by user.
//    PendingIntent pendingIntentPause = PendingIntent.getBroadcast(mContext, 12345, intentPause, PendingIntent.FLAG_CANCEL_CURRENT);
//
//    //This Intent will be called when Cancel button from notification will be
//    //clicked by user.
//    PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(mContext, 12346, intentCancel, PendingIntent.FLAG_CANCEL_CURRENT);
//
//    NotificationCompat.Action actionPause = new NotificationCompat.Action.Builder(R.drawable.ic_book_icon, "Pause", pendingIntentPause).build();
//    NotificationCompat.Action act  ionCancel = new NotificationCompat.Action.Builder(R.drawable.ic_download_icon, "Cancel", pendingIntentCancel).build();



    protected void onPreExecute(){
        super.onPreExecute();
        notificationManager = NotificationManagerCompat.from(mContext);
        builder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
        builder.setContentTitle("My Ebook")
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.ic_e)
                .setOngoing(true);
//                .addAction(actionPause)
//                .addAction(actionCancel);
//                    .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Issue the initial notification with zero progress
        int PROGRESS_MAX = 100;
        int PROGRESS_CURRENT = 0;
        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
        notificationManager.notify(notificationId, builder.build());

    }


    @Override
    protected Void doInBackground(String... strings) {

        mSharedPreferences = mContext.getSharedPreferences("myEbook", Context.MODE_PRIVATE);
        mGraduationLevel = mSharedPreferences.getString("GraduationLevel", null);
        mCourse = mSharedPreferences.getString("Course", null);
        mSemester = mSharedPreferences.getString("Semester", null);

        String fileUrl = strings[0];   // the url for download the pdf
        String fileFolder = mGraduationLevel +"/"+ mCourse +"/"+ mSemester +"/"+ strings[1];  //GraduationLevel -> Course -> Semester -> subject name
        String fileName = strings[2];  // pdf file name
        builder.setContentTitle(fileName);
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDirectory, "MyEbook/" + fileFolder);
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


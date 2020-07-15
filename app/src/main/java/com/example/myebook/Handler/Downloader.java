package com.example.myebook.Handler;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;

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

public class Downloader extends AsyncTask<String, Integer, Void> {

    private static final String TAG = "Downloader";
    private Activity mContext;
    private String mGraduationLevel;
    private String mCourse;
    private String mSemester;
    private String fileName;
    private String mFileLocation;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private static final String CHANNEL_ID = "id";
    private int notificationId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
    private static final int  MEGABYTE = 1024 * 1024;


    public Downloader(Activity context) {
        mContext = context;
    }



    protected void onPreExecute(){
//        Intent intentPause = new Intent(mContext, NotificationActionReceiver.class).setAction("PAUSE");
//        Intent intentCancel = new Intent(mContext, NotificationActionReceiver.class).setAction("CANCEL");
//
//        PendingIntent pendingIntentPause = PendingIntent.getBroadcast(mContext, 12345, intentPause, PendingIntent.FLAG_CANCEL_CURRENT);
//        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(mContext, 12346, intentCancel, PendingIntent.FLAG_CANCEL_CURRENT);
//
//        NotificationCompat.Action actionPause = new NotificationCompat.Action.Builder(R.drawable.ic_book_icon, "Pause", pendingIntentPause).build();
//        NotificationCompat.Action actionCancel = new NotificationCompat.Action.Builder(R.drawable.ic_download_icon, "Cancel", pendingIntentCancel).build();



//        super.onPreExecute();
        mBuilder = new NotificationCompat.Builder(mContext.getApplicationContext(), CHANNEL_ID);
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder.setContentTitle("My Ebook")
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.ic_e)
                .setOngoing(true);
//                .addAction(actionPause)
//                .addAction(actionCancel);
//                    .setPriority(NotificationCompat.PRIORITY_HIGH);


        // Issue the initial notification with zero progress
        int PROGRESS_MAX = 100;
        int PROGRESS_CURRENT = 0;
        mBuilder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);


        // Android 8 introduced a new requirement of setting the channelId property by
        // using a NotificationChannel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = CHANNEL_ID;
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "My Ebook channel",
                    NotificationManager.IMPORTANCE_LOW);

            //Configure the notification channel, NO SOUND
            channel.setDescription("no sound");
            channel.setSound(null,null); // ignore sound
            channel.enableVibration(false);

            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }


        mNotificationManager.notify(notificationId, mBuilder.build());

    }


    @Override
    protected Void doInBackground(String... strings) {

        mSharedPreferences = mContext.getSharedPreferences("myEbook", Context.MODE_PRIVATE);
        mGraduationLevel = mSharedPreferences.getString("GraduationLevel", null);
        mCourse = mSharedPreferences.getString("Course", null);
        mSemester = mSharedPreferences.getString("Semester", null);

        String fileUrl = strings[0];   // the url for download the pdf
        String fileFolder = mGraduationLevel +"/"+ mCourse +"/"+ mSemester +"/"+ strings[1];  //GraduationLevel -> Course -> Semester -> subject name
        fileName = strings[2];  // pdf file name

        mBuilder.setContentTitle(fileName);
        mFileLocation = fileFolder +"/"+ fileName;

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

        mBuilder.setProgress(100, progress[0], false);
        // Displays the progress bar on notification
        mNotificationManager.notify(notificationId, mBuilder.build());
    }


    protected void onPostExecute(Void result){

        /*
        * First cancelling the Notification Then Showing Again
        * */
        mNotificationManager.cancel(notificationId);

        mBuilder = new NotificationCompat.Builder(mContext.getApplicationContext(), CHANNEL_ID);
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder.setContentTitle(fileName)
                .setContentText("Download complete")
                .setSmallIcon(R.drawable.ic_e)
                .setOngoing(false);

        // Android 8 introduced a new requirement of setting the channelId property by
        // using a NotificationChannel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = CHANNEL_ID;
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "My Ebook channel",
                    NotificationManager.IMPORTANCE_LOW);

//            //Configure the notification channel, NO SOUND
//            channel.setDescription("no sound");
//            channel.setSound(null,null); // ignore sound
//            channel.enableVibration(false);

            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(notificationId, mBuilder.build());


        /*
        * keeping track that the download task if the file is completed
        * */
        mEditor = mSharedPreferences.edit();
        mEditor.putString(mFileLocation, "DownloadCompleted"); // Storing string
        mEditor.apply(); // apply changes
    }


}


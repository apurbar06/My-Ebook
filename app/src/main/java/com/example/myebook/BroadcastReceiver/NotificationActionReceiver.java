package com.example.myebook.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.myebook.Adapter.DownloadableEbookListAdapter;

/**
 * Currently there is no use of this
 */
public class NotificationActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.getAction().equalsIgnoreCase("PAUSE")) {

            Toast.makeText(context, "Booking your ride", Toast.LENGTH_SHORT).show();


        } else if (intent.getAction().equalsIgnoreCase("CANCEL")) {

//            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.cancel(11111);

//            DownloadableEbookListAdapter.Downloader lmib = new DownloadableEbookListAdapter.Downloader();
//            if(lmib.getStatus() == AsyncTask.Status.RUNNING){
//                lmib.cancel(true);
//                Toast.makeText(context, "Booking ", Toast.LENGTH_SHORT).show();
//            }

        }
    }
}

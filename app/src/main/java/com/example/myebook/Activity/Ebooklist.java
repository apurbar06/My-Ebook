package com.example.myebook.Activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.myebook.Adapter.ReadableEbookListAdapter;
import com.example.myebook.Handler.Downloader;
import com.example.myebook.Handler.RequestAppPermission;
import com.example.myebook.R;

import java.io.File;

public class Ebooklist extends AppCompatActivity {
    private static final String TAG = "Ebooklist";

    int APP_PERMISSION_REQUEST_CODE = 123;
    ReadableEbookListAdapter mEbookListAdapter;
    private ListView mEbookListView;
    private LinearLayout mEmptyMessage;
    private String mGraduationLevel;
    private String mCourse;
    private String mSemester;
    private String mClickedSubject;
    private String[] mEbooks;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private boolean readyForDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebook_list);

        //requesting permission from user to read and write external storage for API 23 or higher
        RequestAppPermission request = new RequestAppPermission();
        request.readWrite(Ebooklist.this, APP_PERMISSION_REQUEST_CODE);

        //enable the home button
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mClickedSubject = intent.getStringExtra("clickedSubject");

        mEbookListView = (ListView) findViewById(R.id.listViewEbook);
        mEmptyMessage = (LinearLayout) findViewById(R.id.emptyMessageEbook);
        mSharedPreferences = this.getSharedPreferences("myEbook", Context.MODE_PRIVATE);
        mGraduationLevel = mSharedPreferences.getString("GraduationLevel", null);
        mCourse = mSharedPreferences.getString("Course", null);
        mSemester = mSharedPreferences.getString("Semester", null);

        loadEbookIntoListView();
    }


    private void loadEbookIntoListView() {

        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDirectory, "MyEbook/" + mGraduationLevel + "/" + mCourse + "/" + mSemester + "/" + mClickedSubject);
        mEbooks = folder.list();//getting the list of files in selectedSubject in string array

        if(mEbooks == null) {
            mEbooks = new String[]{""};
        }

        //showing empty message while ebooks array is empty
        if (mEbooks.length == 0) {
            mEmptyMessage.setVisibility(View.VISIBLE);
            mEbookListView.setVisibility(View.GONE);
        } else {
            mEbookListView.setVisibility(View.VISIBLE);
            mEmptyMessage.setVisibility(View.GONE);
        }

        mEbookListAdapter = new ReadableEbookListAdapter(Ebooklist.this, mEbooks, readyForDelete);
        mEbookListView.setAdapter(mEbookListAdapter);

        //onClickListener in ebook listView
        mEbookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "onItemClick: item " + ReadableEbookListAdapter.getItemAtPosition(position));

                //extracting the selected ebook
                String clickedEbook = (String) ReadableEbookListAdapter.getItemAtPosition(position);

                String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                File file = new File(extStorageDirectory, "MyEbook/" + mGraduationLevel + "/" + mCourse + "/" + mSemester + "/" + mClickedSubject + "/" + clickedEbook);
                String fileLocation = mGraduationLevel + "/" + mCourse + "/" + mSemester + "/" + mClickedSubject + "/" + clickedEbook;
                Log.d(TAG, "onItemClick: " + file);


//                Uri uri = Uri.fromFile(file);
                // If your targetSdkVersion >= 24, then we have to use FileProvider class to give access to
                // the particular file or folder to make them accessible for other apps. We create our own
                // class inheriting FileProvider in order to make sure our FileProvider doesn't conflict with
                // FileProviders declared in imported dependencies
                Uri uri = FileProvider.getUriForFile(
                        Ebooklist.this,
                        getApplicationContext()
                                .getPackageName() + ".provider", file);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                // Checking what kind of file user is trying to open, by comparing the file with extensions.
                // When the if condition is matched, plugin sets the correct intent (mime) type,
                // so Android knew what application to use to open the file
                if (file.toString().contains(".doc") || file.toString().contains(".docx")) {
                    // Word document
                    intent.setDataAndType(uri, "application/msword");
                } else if (file.toString().contains(".pdf")) {
                    // PDF file
                    intent.setDataAndType(uri, "application/pdf");
                } else if (file.toString().contains(".ppt") || file.toString().contains(".pptx")) {
                    // Powerpoint file
                    intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                } else if (file.toString().contains(".xls") || file.toString().contains(".xlsx")) {
                    // Excel file
                    intent.setDataAndType(uri, "application/vnd.ms-excel");
                } else if (file.toString().contains(".zip") || file.toString().contains(".rar")) {
                    // WAV audio file
                    intent.setDataAndType(uri, "application/x-wav");
                } else if (file.toString().contains(".rtf")) {
                    // RTF file
                    intent.setDataAndType(uri, "application/rtf");
                } else if (file.toString().contains(".wav") || file.toString().contains(".mp3")) {
                    // WAV audio file
                    intent.setDataAndType(uri, "audio/x-wav");
                } else if (file.toString().contains(".gif")) {
                    // GIF file
                    intent.setDataAndType(uri, "image/gif");
                } else if (file.toString().contains(".jpg") || file.toString().contains(".jpeg") || file.toString().contains(".png")) {
                    // JPG file
                    intent.setDataAndType(uri, "image/jpeg");
                } else if (file.toString().contains(".txt")) {
                    // Text file
                    intent.setDataAndType(uri, "text/plain");
                } else if (file.toString().contains(".3gp") || file.toString().contains(".mpg") || file.toString().contains(".mpeg") || file.toString().contains(".mpe") || file.toString().contains(".mp4") || file.toString().contains(".avi")) {
                    // Video files
                    intent.setDataAndType(uri, "video/*");
                } else {
                    //additionally use else clause below, to manage other unknown extensions
                    //in this case, Android will show all applications installed on the device
                    //so you user choose which application to use
                    intent.setDataAndType(uri, "*/*");
                }
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    //This if part will execute when the download will be completed
                    if(mSharedPreferences.getString(fileLocation, "PreviouslyDownloaded").equals("DownloadCompleted") || mSharedPreferences.getString(fileLocation, "PreviouslyDownloaded").equals("PreviouslyDownloaded")) {

                        startActivity(intent);
                    } else {
                        Toast.makeText(Ebooklist.this, "Download is not completed", Toast.LENGTH_LONG).show();
                    }
                } catch (ActivityNotFoundException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Log.d(TAG, "run: Application to open this file is not found");
                            Toast.makeText(Ebooklist.this, "Application to open this file is not found", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception ex) {
                    Log.d(TAG, "onItemClick: ex");
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        // adding menu
        inflater.inflate(R.menu.menu_mark, menu);
        // adding the delete menu by default
        inflater.inflate(R.menu.menu_delete, menu);

        return true;
    }


    /**
     * this method is called when the user click the three dot icon
     *
     * @param menu menu
     * @return boolean
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem delete = menu.findItem(R.id.delete);
        MenuItem mark = menu.findItem(R.id.mark);
        if (mEbooks.length == 0) {
            delete.setVisible(false);
            mark.setVisible(false);
        } else {
            if (readyForDelete) {
                mark.setTitle("Undo");
                delete.setVisible(true);
            } else {
                mark.setTitle("Mark");
                delete.setVisible(false);
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.mark:
                // reverse the current status of readyForDelete
                readyForDelete = !readyForDelete;
                loadEbookIntoListView();
                return true;
            case R.id.delete:
                deleteCheckedEbooks();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void deleteCheckedEbooks() {

        for (int i = 0; i < mEbookListAdapter.mCheckBoxState.length; i++) {
            if (mEbookListAdapter.mCheckBoxState[i] == true) {
                Log.d(TAG, "deleteCheckedItems: " + ReadableEbookListAdapter.getItemAtPosition(i));
                String ebookToDelete = ReadableEbookListAdapter.getItemAtPosition(i);
                File file = new File(Environment.getExternalStorageDirectory() + "/MyEbook/" + mGraduationLevel + "/" + mCourse + "/" + mSemester + "/" + mClickedSubject + "/" + ebookToDelete);

                String fileLocation =  mGraduationLevel + "/" + mCourse + "/" + mSemester + "/" + mClickedSubject + "/" + ebookToDelete;
                mSharedPreferences.edit().remove(fileLocation).apply();


                file.delete();
            }
        }
        readyForDelete = false;
        loadEbookIntoListView();

    }
}

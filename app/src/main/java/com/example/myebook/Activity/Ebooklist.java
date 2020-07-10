package com.example.myebook.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
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

import com.example.myebook.R;
import com.example.myebook.Adapter.ReadableEbookListAdapter;

import java.io.File;

public class Ebooklist extends AppCompatActivity {
    private static final String TAG = "Ebooklist";

    private ListView mEbookListView;
    private LinearLayout mEmptyMessage;
    private String mGraduationLevel;
    private String mCourse;
    private String mSemester;
    private  String mClickedSubject;
    private String[] mEbooks;
    ReadableEbookListAdapter mEbookListAdapter;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private boolean readyForDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebook_list);
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
        File folder = new File(extStorageDirectory, "MyEbook/"+ mGraduationLevel +"/"+ mCourse +"/"+ mSemester +"/" + mClickedSubject);
        mEbooks = folder.list();//getting the list of files in selectedSubject in string array


        //showing empty message while ebooks array is empty
        assert mEbooks != null;
        if(mEbooks.length == 0) {
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
                File file = new File(extStorageDirectory, "MyEbook/"+ mGraduationLevel +"/"+ mCourse +"/"+ mSemester +"/" + mClickedSubject + "/" + clickedEbook);
                Log.d(TAG, "onItemClick: " + file);


                Uri uri = Uri.fromFile(file);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                // Checking what kind of file user is trying to open, by comparing the file with extensions.
                // When the if condition is matched, plugin sets the correct intent (mime) type,
                // so Android knew what application to use to open the file
                if (file.toString().contains(".doc") || file.toString().contains(".docx")) {
                    // Word document
                    intent.setDataAndType(uri, "application/msword");
                } else if(file.toString().contains(".pdf")) {
                    // PDF file
                    intent.setDataAndType(uri, "application/pdf");
                } else if(file.toString().contains(".ppt") || file.toString().contains(".pptx")) {
                    // Powerpoint file
                    intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                } else if(file.toString().contains(".xls") || file.toString().contains(".xlsx")) {
                    // Excel file
                    intent.setDataAndType(uri, "application/vnd.ms-excel");
                } else if(file.toString().contains(".zip") || file.toString().contains(".rar")) {
                    // WAV audio file
                    intent.setDataAndType(uri, "application/x-wav");
                } else if(file.toString().contains(".rtf")) {
                    // RTF file
                    intent.setDataAndType(uri, "application/rtf");
                } else if(file.toString().contains(".wav") || file.toString().contains(".mp3")) {
                    // WAV audio file
                    intent.setDataAndType(uri, "audio/x-wav");
                } else if(file.toString().contains(".gif")) {
                    // GIF file
                    intent.setDataAndType(uri, "image/gif");
                } else if(file.toString().contains(".jpg") || file.toString().contains(".jpeg") || file.toString().contains(".png")) {
                    // JPG file
                    intent.setDataAndType(uri, "image/jpeg");
                } else if(file.toString().contains(".txt")) {
                    // Text file
                    intent.setDataAndType(uri, "text/plain");
                } else if(file.toString().contains(".3gp") || file.toString().contains(".mpg") || file.toString().contains(".mpeg") || file.toString().contains(".mpe") || file.toString().contains(".mp4") || file.toString().contains(".avi")) {
                    // Video files
                    intent.setDataAndType(uri, "video/*");
                } else {
                    //additionally use else clause below, to manage other unknown extensions
                    //in this case, Android will show all applications installed on the device
                    //so you user choose which application to use
                    intent.setDataAndType(uri, "*/*");
                }

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    runOnUiThread(new Runnable(){
                        public void run() {
                            Toast.makeText(Ebooklist.this, "Application to open this file is not found", Toast.LENGTH_LONG).show();
                        }
                    });
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
     * @param menu menu
     * @return boolean
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem delete = menu.findItem(R.id.delete);
        MenuItem mark = menu.findItem(R.id.mark);
        if(mEbooks.length == 0) {
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

        for(int i = 0; i< mEbookListAdapter.mCheckBoxState.length ; i++) {
            if (mEbookListAdapter.mCheckBoxState[i] == true) {
                Log.d(TAG, "deleteCheckedItems: " + ReadableEbookListAdapter.getItemAtPosition(i));
                String ebookToDelete = ReadableEbookListAdapter.getItemAtPosition(i);
                File file = new File(Environment.getExternalStorageDirectory() + "/MyEbook/"+ mGraduationLevel +"/"+ mCourse +"/"+ mSemester +"/"+ mClickedSubject +"/"+ ebookToDelete);

//                //to delete a particular diretory first files in that directory should be deleted
//                if (dir.isDirectory()) {
//                    String[] children = dir.list();
//                    for (int j = 0; j < children.length; j++)
//                    {
//                        new File(dir, children[j]).delete();
//                    }
//                }
                file.delete();
            }
        }
        readyForDelete = false;
        loadEbookIntoListView();

    }
}

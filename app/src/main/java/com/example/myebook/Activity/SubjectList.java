package com.example.myebook.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.myebook.BuildConfig;
import com.example.myebook.Handler.RequestAppPermission;
import com.example.myebook.R;
import com.example.myebook.Adapter.ReadableSubjectListAdapter;

import java.io.File;

public class SubjectList extends AppCompatActivity {
    private static final String TAG = "SubjectList";

    int APP_PERMISSION_REQUEST_CODE = 123;
    private ListView mSubjectListView;
    private LinearLayout mEmptyMessage;
    private String mGraduationLevel;
    private String mCourse;
    private String mSemester;
    String[] mSubjects;
    private  String mClickedSubject;
    private ReadableSubjectListAdapter mSubjectListAdapter;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private boolean readyForDelete = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

        //requesting permission from user to read and write external storage for API 23 or higher
        RequestAppPermission request = new RequestAppPermission();
        request.readWrite(SubjectList.this, APP_PERMISSION_REQUEST_CODE);


        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDirectory, "MyEbook");
        if(!folder.exists()){
            folder.mkdirs();
        }

        mSharedPreferences = this.getSharedPreferences("myEbook", Context.MODE_PRIVATE);
        mGraduationLevel = mSharedPreferences.getString("GraduationLevel", null);
        mCourse = mSharedPreferences.getString("Course", null);
        mSemester = mSharedPreferences.getString("Semester", null);

        loadSubjectIntoListView();

    }


    private void loadSubjectIntoListView() {

        mSubjectListView = (ListView) findViewById(R.id.listViewSubject);
        mEmptyMessage = (LinearLayout) findViewById(R.id.emptyMessageSubject);

        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDirectory, "MyEbook/"+ mGraduationLevel +"/"+ mCourse +"/"+ mSemester +"/");
        if(!folder.exists()){
            folder.mkdirs();
        }
        //getting the list of files in folder in string array
        mSubjects = folder.list();

        if(mSubjects == null) {
            mSubjects = new String[]{""};
        }

        //showing empty message while folders array is empty
        if(mSubjects.length == 0) {
            mEmptyMessage.setVisibility(View.VISIBLE);
            mSubjectListView.setVisibility(View.GONE);
        } else {
            mSubjectListView.setVisibility(View.VISIBLE);
            mEmptyMessage.setVisibility(View.GONE);
        }


        //the adapter to load data into list
        mSubjectListAdapter = new ReadableSubjectListAdapter(SubjectList.this, mSubjects, readyForDelete);
        //attaching adapter to mGridView
        mSubjectListView.setAdapter(mSubjectListAdapter);


        //onClickListener in subject mListView
        mSubjectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "onItemClick: item is " + ReadableSubjectListAdapter.getItemAtPosition(position));


                //extracting the selected subject
                mClickedSubject = (String) ReadableSubjectListAdapter.getItemAtPosition(position);

                Intent intent = new Intent(SubjectList.this, Ebooklist.class);
                intent.putExtra("clickedSubject", mClickedSubject);
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        // adding menu
        inflater.inflate(R.menu.menu_contribute, menu);
        inflater.inflate(R.menu.menu_download, menu);
        inflater.inflate(R.menu.menu_mark, menu);
        // adding the delete menu by default
        inflater.inflate(R.menu.menu_delete, menu);
        inflater.inflate(R.menu.menu_setup, menu);
        inflater.inflate(R.menu.menu_about, menu);

        return true;
    }


    /**
     * this method is called when the user click the three dot icon
     * @param menu menu
     * @return boolean
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem contribute = menu.findItem(R.id.contribute);
        MenuItem download = menu.findItem(R.id.download);
        MenuItem delete = menu.findItem(R.id.delete);
        MenuItem mark = menu.findItem(R.id.mark);
        MenuItem setup = menu.findItem(R.id.setup);
        MenuItem about = menu.findItem(R.id.about);
        if(mSubjects.length == 0) {
            mark.setVisible(false);
            delete.setVisible(false);
            contribute.setVisible(true);
            download.setVisible(true);
            setup.setVisible(true);
            about.setVisible(true);
        } else {
            if (readyForDelete) {
                mark.setTitle("Undo");
                delete.setVisible(true);
                contribute.setVisible(false);
                download.setVisible(false);
                setup.setVisible(false);
                about.setVisible(false);
            } else {
                mark.setTitle("Mark");
                delete.setVisible(false);
                contribute.setVisible(true);
                download.setVisible(true);
                setup.setVisible(true);
                about.setVisible(true);
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.contribute:
                Intent uploadIntent = new Intent(SubjectList.this, Contribute.class);
                startActivity(uploadIntent);
                this.finish();
                return true;
            case R.id.download:
                Intent downloadIntent = new Intent(SubjectList.this, DownloadEbook.class);
                startActivity(downloadIntent);
                this.finish();
                return true;
            case R.id.about:
                Intent aboutIntent = new Intent(SubjectList.this, About.class);
                startActivity(aboutIntent);
                this.finish();
                return true;
            case R.id.mark:
                // reverse the current status of readyForDelete
                readyForDelete = !readyForDelete;
                loadSubjectIntoListView();
                return true;
            case R.id.delete:
                deleteCheckedSubjects();
                return true;
            case R.id.setup:
                mSharedPreferences = this.getSharedPreferences("myEbook", Context.MODE_PRIVATE);
                mEditor = mSharedPreferences.edit();
                mEditor.putString("makeSetup", "Yes"); // Storing string
                mEditor.apply(); // apply changes
                Intent setupIntent = new Intent(SubjectList.this, Setup.class);
                startActivity(setupIntent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void onClickShare(View view) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My Ebook");
            String shareMessage= "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "Now the link is not available. It will be available soon." + BuildConfig.APPLICATION_ID +"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Choose one"));
        } catch(Exception e) {
            //e.toString();
        }
    }



    private void deleteCheckedSubjects() {

        for(int i = 0; i< mSubjectListAdapter.mCheckBoxState.length ; i++) {
            if (mSubjectListAdapter.mCheckBoxState[i] == true) {
                Log.d(TAG, "deleteCheckedItems: " + ReadableSubjectListAdapter.getItemAtPosition(i));
                String subjectToDelete = ReadableSubjectListAdapter.getItemAtPosition(i);
                File dir = new File(Environment.getExternalStorageDirectory() + "/MyEbook/"+ mGraduationLevel +"/"+ mCourse +"/"+ mSemester +"/"+ subjectToDelete);

                //to delete a particular diretory first files in that directory should be deleted
                if (dir.isDirectory()) {
                    String[] children = dir.list();
                    for (int j = 0; j < children.length; j++)
                    {
                        new File(dir, children[j]).delete();
                    }
                }
                dir.delete();
                Log.d(TAG, "deleteCheckedItems: " + ReadableSubjectListAdapter.getItemAtPosition(i));
            }
        }
        readyForDelete = false;
        loadSubjectIntoListView();

    }


}

package com.example.myebook;

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
import android.widget.ListView;
import android.widget.Toast;

import com.example.myebook.handler.ReadableEbookListAdapter;
import com.example.myebook.handler.ReadableSubjectListAdapter;

import java.io.File;

public class Ebooklist extends AppCompatActivity {
    private static final String TAG = "Ebooklist";

    private String mGraduationLevel;
    private String mCourse;
    private String mSemester;
    private  String mClickedSubject;
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

        mSharedPreferences = this.getSharedPreferences("myEbook", Context.MODE_PRIVATE);
        mGraduationLevel = mSharedPreferences.getString("GraduationLevel", null);
        mCourse = mSharedPreferences.getString("Course", null);
        mSemester = mSharedPreferences.getString("Semester", null);

        loadEbookIntoListView();
    }


    private void loadEbookIntoListView() {

        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDirectory, "My Ebook/"+ mGraduationLevel +"/"+ mCourse +"/"+ mSemester +"/" + mClickedSubject);
        String[] ebooks = folder.list();//getting the list of files in selectedSubject in string array


        ListView listView = (ListView) findViewById(R.id.listViewEbook);
        ReadableEbookListAdapter adapter = new ReadableEbookListAdapter(Ebooklist.this, ebooks);
        listView.setAdapter(adapter);

        //onClickListener in ebook listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "onItemClick: item " + ReadableEbookListAdapter.getItemAtPosition(position));

                //extracting the selected ebook
                String clickedEbook = (String) ReadableEbookListAdapter.getItemAtPosition(position);

                String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                File file = new File(extStorageDirectory, "My Ebook/"+ mGraduationLevel +"/"+ mCourse +"/"+ mSemester +"/" + mClickedSubject + "/" + clickedEbook);
                Log.d(TAG, "onItemClick: " + file);

                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(Uri.fromFile(file), "application/pdf");
                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                Intent intent = Intent.createChooser(target, "Open File");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    runOnUiThread(new Runnable(){
                        public void run() {
                            Toast.makeText(Ebooklist.this, "Please install a pdf reader", Toast.LENGTH_LONG).show();
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
        inflater.inflate(R.menu.menu_contribute, menu);
        inflater.inflate(R.menu.menu_download, menu);
        inflater.inflate(R.menu.menu_mark, menu);
        // adding the delete menu by default
        inflater.inflate(R.menu.menu_delete, menu);
        inflater.inflate(R.menu.menu_setup, menu);

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
        if (readyForDelete) {
            mark.setTitle("Undo");
            delete.setVisible(true);
            contribute.setVisible(false);
            download.setVisible(false);
            setup.setVisible(false);
        } else {
            mark.setTitle("Mark");
            delete.setVisible(false);
            contribute.setVisible(true);
            download.setVisible(true);
            setup.setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.contribute:
                Intent intent = new Intent(Ebooklist.this, Contribute.class);
                startActivity(intent);
                return true;
            case R.id.download:
                Intent intent1 = new Intent(Ebooklist.this, DownloadEbook.class);
                startActivity(intent1);
                return true;
            case R.id.mark:
                // reverse the current status of readyForDelete
                readyForDelete = !readyForDelete;
                loadEbookIntoListView();
                return true;
            case R.id.delete:
                deleteCheckedEbooks();
                return true;
            case R.id.setup:
                mSharedPreferences = this.getSharedPreferences("myEbook", Context.MODE_PRIVATE);
                mEditor = mSharedPreferences.edit();
                mEditor.putString("makeSetup", "Yes"); // Storing string
                mEditor.apply(); // apply changes
                Intent intent2 = new Intent(Ebooklist.this, MainActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void deleteCheckedEbooks() {

//        for(int i = 0; i< mSubjectListAdapter.mCheckBoxState.length ; i++) {
//            if (mSubjectListAdapter.mCheckBoxState[i] == true) {
//                Log.d(TAG, "deleteCheckedItems: " + ReadableSubjectListAdapter.getItemAtPosition(i));
//                String subjectToDelete = ReadableSubjectListAdapter.getItemAtPosition(i);
//                File dir = new File(Environment.getExternalStorageDirectory() + "/My Ebook/"+ mGraduationLevel +"/"+ mCourse +"/"+ mSemester +"/"+ subjectToDelete);
//
//                //to delete a particular diretory first files in that directory should be deleted
//                if (dir.isDirectory()) {
//                    String[] children = dir.list();
//                    for (int j = 0; j < children.length; j++)
//                    {
//                        new File(dir, children[j]).delete();
//                    }
//                }
//                dir.delete();
//                Log.d(TAG, "deleteCheckedItems: " + ReadableSubjectListAdapter.getItemAtPosition(i));
//            }
//        }
        readyForDelete = false;
        loadEbookIntoListView();

    }
}

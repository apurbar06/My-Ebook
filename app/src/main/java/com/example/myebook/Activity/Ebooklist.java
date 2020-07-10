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
import android.widget.ListView;
import android.widget.Toast;

import com.example.myebook.R;
import com.example.myebook.Adapter.ReadableEbookListAdapter;

import java.io.File;

public class Ebooklist extends AppCompatActivity {
    private static final String TAG = "Ebooklist";

    private String mGraduationLevel;
    private String mCourse;
    private String mSemester;
    private  String mClickedSubject;
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
        mEbookListAdapter = new ReadableEbookListAdapter(Ebooklist.this, ebooks, readyForDelete);
        listView.setAdapter(mEbookListAdapter);

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

                //extracting the extension like pdf/ppt/pptx
                String last3 = ((clickedEbook == null) || (clickedEbook.length() < 3)) ? clickedEbook : clickedEbook.substring(clickedEbook.length() - 3);
                String last4 = ((clickedEbook == null) || (clickedEbook.length() < 4)) ? clickedEbook: clickedEbook.substring(clickedEbook.length() - 4);


                Intent target = new Intent(Intent.ACTION_VIEW);
                //set type pdf/ppt if the file is an pdf/ppt file
                if(last3.equals("pdf")) {
                    target.setDataAndType(Uri.fromFile(file), "application/pdf");
                } else if(last3.equals("ppt") || last4.equals("pptx")) {
                    target.setDataAndType(Uri.fromFile(file), "application/vnd.ms-powerpoint");
                }
                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                Intent intent = Intent.createChooser(target, "Open File");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    runOnUiThread(new Runnable(){
                        public void run() {
                            Toast.makeText(Ebooklist.this, "Reader not found", Toast.LENGTH_LONG).show();
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
        if (readyForDelete) {
            mark.setTitle("Undo");
            delete.setVisible(true);
        } else {
            mark.setTitle("Mark");
            delete.setVisible(false);
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
                File file = new File(Environment.getExternalStorageDirectory() + "/My Ebook/"+ mGraduationLevel +"/"+ mCourse +"/"+ mSemester +"/"+ mClickedSubject +"/"+ ebookToDelete);

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

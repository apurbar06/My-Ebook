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

public class ReadEbook extends AppCompatActivity {
    private static final String TAG = "ReadEbook";

    ListView mListView;
    private String mGraduationLevel;
    private String mCourse;
    private String mSemester;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private boolean readyForDelete = false;
    ReadableSubjectListAdapter mSubjectListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_ebook);

        loadIntoListView();


    }

    private void loadIntoListView() {

        //enable the home button but keep it invisible when a subject item is clicked
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);

        mSharedPreferences = this.getSharedPreferences("myEbook", Context.MODE_PRIVATE);
        mGraduationLevel = mSharedPreferences.getString("GraduationLevel", null);
        mCourse = mSharedPreferences.getString("Course", null);
        mSemester = mSharedPreferences.getString("Semester", null);
        mListView = (ListView) findViewById(R.id.listViewR);

        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDirectory, "My Ebook/"+ mGraduationLevel +"/"+ mCourse +"/"+ mSemester +"/");
        if(!folder.exists()){
            folder.mkdirs();
        }
        //getting the list of files in folder in string array
        String[] folders = folder.list();
//        Log.d(TAG, "loadIntoListView: " + folders);

        //the adapter to load data into list
        mSubjectListAdapter = new ReadableSubjectListAdapter(ReadEbook.this, folders, readyForDelete);
        //attaching adapter to mGridView
        mListView.setAdapter(mSubjectListAdapter);


        //onClickListener in subject mListView
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //make visible the home button
                actionBar.setDisplayHomeAsUpEnabled(true);

                Log.d(TAG, "onItemClick: item is " + ReadableSubjectListAdapter.getItemAtPosition(position));


                //extracting the selected subject
                final String selectedSubject = (String) ReadableSubjectListAdapter.getItemAtPosition(position);

                String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                File folder = new File(extStorageDirectory, "My Ebook/"+ mGraduationLevel +"/"+ mCourse +"/"+ mSemester +"/" + selectedSubject);
                String[] ebooks = folder.list();//getting the list of files in selectedSubject in string array


                ListView listView = (ListView) findViewById(R.id.listViewR);
                ReadableEbookListAdapter adapter = new ReadableEbookListAdapter(ReadEbook.this, ebooks);
                listView.setAdapter(adapter);

                //onClickListener in ebook listView
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Log.d(TAG, "onItemClick: item " + ReadableEbookListAdapter.getItemAtPosition(position));

                        //extracting the selected ebook
                        String selectedEbook = (String) ReadableEbookListAdapter.getItemAtPosition(position);

                        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                        File file = new File(extStorageDirectory, "My Ebook/"+ mGraduationLevel +"/"+ mCourse +"/"+ mSemester +"/" + selectedSubject + "/" + selectedEbook);
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
                                    Toast.makeText(ReadEbook.this, "Please install a pdf reader", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
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
                loadIntoListView();
                return true;
            case R.id.contribute:
                Intent intent = new Intent(ReadEbook.this, Contribute.class);
                startActivity(intent);
                return true;
            case R.id.download:
                Intent intent1 = new Intent(ReadEbook.this, DownloadEbook.class);
                startActivity(intent1);
                this.finish();
                return true;
            case R.id.mark:
                // reverse the current status of readyForDelete
                readyForDelete = !readyForDelete;
                loadIntoListView();
                return true;
            case R.id.delete:
                deleteCheckedItems();
                return true;
            case R.id.setup:
                mSharedPreferences = this.getSharedPreferences("myEbook", Context.MODE_PRIVATE);
                mEditor = mSharedPreferences.edit();
                mEditor.putString("makeSetup", "Yes"); // Storing string
                mEditor.apply(); // apply changes
                Intent intent2 = new Intent(ReadEbook.this, MainActivity.class);
                startActivity(intent2);
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
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Choose one"));
        } catch(Exception e) {
            //e.toString();
        }
    }

    private void deleteCheckedItems() {

        for(int i = 0; i< mSubjectListAdapter.mCheckBoxState.length ; i++) {
            if (mSubjectListAdapter.mCheckBoxState[i] == true) {
                Log.d(TAG, "deleteCheckedItems: " + ReadableSubjectListAdapter.getItemAtPosition(i));
                String subjectToDelete = ReadableSubjectListAdapter.getItemAtPosition(i);
                File dir = new File(Environment.getExternalStorageDirectory() + "/My Ebook/"+ mGraduationLevel +"/"+ mCourse +"/"+ mSemester +"/"+ subjectToDelete);
//                if (dir.isDirectory()) {
//                    String[] children = dir.list();
//                    for (int j = 0; j < children.length; j++)
//                    {
//                        new File(dir, children[j]).delete();
//                    }
//                }
                dir.delete();
            }
        }
        readyForDelete = false;
        loadIntoListView();

    }
}

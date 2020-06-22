package com.example.myebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import com.example.myebook.handler.ReadableEbookListAdapter;
import com.example.myebook.handler.ReadableSubjectListAdapter;

import org.json.JSONException;

import java.io.File;

public class ReadEbook extends AppCompatActivity {
    private static final String TAG = "ReadEbook";

    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_ebook);

        loadIntoListView();


    }

    private void loadIntoListView () {

        //enable the home button but keep it invisible when a subject item is clicked
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);

        mListView = (ListView) findViewById(R.id.listViewR);
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDirectory, "MyEbook/");
        String[] folders = folder.list();//getting the list of files in My Ebook in string array

        //the adapter to load data into list
        ReadableSubjectListAdapter adapter = new ReadableSubjectListAdapter(ReadEbook.this, folders);
        //attaching adapter to mGridView
        mListView.setAdapter(adapter);


        //onClickListener in subject mListView
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //make visible the home button
                actionBar.setDisplayHomeAsUpEnabled(true);

                Log.d(TAG, "onItemClick: item is " + ReadableSubjectListAdapter.getItemAtPosition(position));


                //extracting the selected subject
                String selectedSubject = (String) ReadableSubjectListAdapter.getItemAtPosition(position);

                String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                File folder = new File(extStorageDirectory, "MyEbook/" + selectedSubject);
                String[] ebooks = folder.list();//getting the list of files in My Ebook/selectedSubject in string array



                ListView listView = (ListView) findViewById(R.id.listViewR);
                ReadableEbookListAdapter adapter = new ReadableEbookListAdapter(ReadEbook.this, ebooks);
                listView.setAdapter(adapter);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                loadIntoListView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

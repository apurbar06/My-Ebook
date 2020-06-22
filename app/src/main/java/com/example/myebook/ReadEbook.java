package com.example.myebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
                final String selectedSubject = (String) ReadableSubjectListAdapter.getItemAtPosition(position);

                String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                File folder = new File(extStorageDirectory, "MyEbook/" + selectedSubject);
                String[] ebooks = folder.list();//getting the list of files in My Ebook/selectedSubject in string array



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
                        File file = new File(extStorageDirectory, "MyEbook/" + selectedSubject + "/" + selectedEbook);
                        Log.d(TAG, "onItemClick: " + file);

                        Intent target = new Intent(Intent.ACTION_VIEW);
                        target.setDataAndType(Uri.fromFile(file),"application/pdf");
                        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        Intent intent = Intent.createChooser(target, "Open File");
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(ReadEbook.this, "Please install a pdf reader", Toast.LENGTH_LONG).show();
                        }
                    }
                });
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

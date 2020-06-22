package com.example.myebook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

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

        mListView = (ListView) findViewById(R.id.listViewR);
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDirectory, "MyEbook/");
        String[] folders = folder.list();//getting the list of files in My Ebook in string array

        //the adapter to load data into list
        ReadableSubjectListAdapter adapter = new ReadableSubjectListAdapter(ReadEbook.this, folders);
        //attaching adapter to mGridView
        mListView.setAdapter(adapter);


//        //onClickListener in subject mListView
//        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.d(TAG, "onItemClick: item is " + lview.getItemAtPosition(position));
//
//                //extracting the selected subject
//                String selectedSubject = (String) lview.getItemAtPosition(position);
//
//                String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
//                File folder = new File(extStorageDirectory, "MyEbook/" + selectedSubject);
//                String[] ebooks = folder.list();//getting the list of files in My Ebook/selectedSubject in string array
//
//
//
//                GridView gridView = (GridView)findViewById(R.id.gridview);
//                ReadableEbookListAdapter adapter = new ReadableEbookListAdapter(ReadEbook.this, selectedSubject, ebooks);
//                gridView.setAdapter(adapter);
//            }
//        });


    }
}

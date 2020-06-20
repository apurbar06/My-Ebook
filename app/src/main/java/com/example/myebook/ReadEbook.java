package com.example.myebook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;

public class ReadEbook extends AppCompatActivity {

    ListView lview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_ebook);

        lview = (ListView) findViewById(R.id.listViewR);
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDirectory, "MyEbook/");
        String[] values = folder.list();//getting the list of files in My Ebook in string array

        //the array adapter to load data into list
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);

        //attaching adapter to lview
        lview.setAdapter(arrayAdapter);




    }
}

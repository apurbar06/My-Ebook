package com.example.myebook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Spinner mSpinnerGroup;
    private Spinner mSpinnerSemester;
    private int mSpinnerGroupPos;
    private static final String[] mGroupArray = {"CED", "MPD", "ESD", "CSE", "MSM"};
    private static final String[] mSemesterArrayDual = {"First", "Second", "Third", "Forth", "Fifth"};
    private static final String[] mSemesterArraySingle = {"First", "Second", "Third", "Forth"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSpinnerGroup = (Spinner)findViewById(R.id.spinner_group);


        ArrayAdapter<String>adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, mGroupArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerGroup.setAdapter(adapter);
        mSpinnerGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mSpinnerGroupPos = position;
                Log.d(TAG, "onItemSelected: position " + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



}

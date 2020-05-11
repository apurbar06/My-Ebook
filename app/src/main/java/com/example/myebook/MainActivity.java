package com.example.myebook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "MainActivity";
    private Spinner mSpinnerGroup;
    private Spinner mSpinnerSemester;
    private static final String[] mGroupArray = {"CED", "MPD", "ESD", "CSE", "MSM"};
    private static final String[] mSemesterArrayDual = {"First", "Second", "Third", "Forth", "Fifth"};
    private static final String[] mSemesterArraySingle = {"First", "Second", "Third", "Forth", "Fifth"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

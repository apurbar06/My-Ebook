package com.example.myebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Spinner mSpinnerGraduationLevel;
    private Spinner mSpinnerCourse;
    private Spinner mSpinnerSemester;
    private int mSpinnerGraduationLevelPos;
    private int mSpinnerCoursePos;
    private String mGraduationLevel;
    private String mCourse;
    private String mSemester;
    private static final String[] mGraduationLevelArray = {"UG", "DD", "PG"};
    private static final String[] mUgCourseArray = {"COE", "EDM", "MDM", "MSM"};
    private static final String[] mDdCourseArray = {"CED", "EVD", "ESD", "MPD", "MFD"};
    private static final String[] mPgCourseArray = {"CES", "EDS", "MDS", "SMT"};
    private static final String[] mUgSemesterArray = {"First", "Second"};
    private static final String[] mDdSemesterArray = {"First", "Second"};
    private static final String[] mPgSemesterArray = {"First", "Second"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSpinnerGraduationLevel = (Spinner)findViewById(R.id.spinner_graduation_level);
        mSpinnerCourse = (Spinner)findViewById(R.id.spinner_course);
        mSpinnerSemester = (Spinner)findViewById(R.id.spinner_semester);


        ArrayAdapter<String>adapterGL = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, mGraduationLevelArray);
        adapterGL.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerGraduationLevel.setAdapter(adapterGL);
        mSpinnerGraduationLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mSpinnerGraduationLevelPos = position;
                mGraduationLevel = mSpinnerGraduationLevel.getSelectedItem().toString();

                if (mSpinnerGraduationLevelPos == 0) {

                    ArrayAdapter<String>adapter1 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, mUgCourseArray);
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerCourse.setAdapter(adapter1);

                } else if (mSpinnerGraduationLevelPos == 1) {

                    ArrayAdapter<String>adapter2 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, mDdCourseArray);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerCourse.setAdapter(adapter2);

                } else {

                    ArrayAdapter<String>adapter3 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, mPgCourseArray);
                    adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerCourse.setAdapter(adapter3);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        mSpinnerCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mSpinnerCoursePos = position;
                mCourse = mSpinnerCourse.getSelectedItem().toString();


                if (mSpinnerGraduationLevelPos == 0){

                    ArrayAdapter<String>adapter1 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, mUgSemesterArray);
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerSemester.setAdapter(adapter1);

                } else if (mSpinnerGraduationLevelPos ==1) {

                    ArrayAdapter<String>adapter2 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, mDdSemesterArray);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerSemester.setAdapter(adapter2);

                } else {

                    ArrayAdapter<String>adapter3 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, mPgSemesterArray);
                    adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerSemester.setAdapter(adapter3);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSpinnerSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mSemester = mSpinnerSemester.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void setGlCourseSemester(View view) {

        Log.d(TAG, "onItemSelected: " + mGraduationLevel + mCourse + mSemester);
        Intent intent = new Intent(MainActivity.this, DownloadEbook.class);
        startActivity(intent);
    }
}

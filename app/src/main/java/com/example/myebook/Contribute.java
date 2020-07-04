package com.example.myebook;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

public class Contribute extends AppCompatActivity {

    private static final String TAG = "Contribute";
    private Spinner mSpinnerGraduationLevel;
    private Spinner mSpinnerCourse;
    private Spinner mSpinnerSemester;
    private int mSpinnerGraduationLevelPos;
    private int mSpinnerCoursePos;
    private String mGraduationLevel;
    private String mCourse;
    private String mSemester;
    private String mFileName;
    private EditText mEditText;
    private TextView mTvAttachment;
    private static final String[] mGraduationLevelArray = {"UG", "DD", "PG"};
    private static final String[] mUgCourseArray = {"COE", "EDM", "MDM", "MSM"};
    private static final String[] mDdCourseArray = {"CED", "EVD", "ESD", "MPD", "MFD"};
    private static final String[] mPgCourseArray = {"CES", "EDS", "MDS", "SMT"};
    private static final String[] mUgSemesterArray = {"First", "Second"};
    private static final String[] mDdSemesterArray = {"First", "Second"};
    private static final String[] mPgSemesterArray = {"First", "Second"};
    private Uri URI = null;
    private static final int PICK_FROM_STORAGE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mSpinnerGraduationLevel = (Spinner)findViewById(R.id.cnb_spinner_graduation_level);
        mSpinnerCourse = (Spinner)findViewById(R.id.cnb_spinner_course);
        mSpinnerSemester = (Spinner)findViewById(R.id.cnb_spinner_semester);
        mEditText = (EditText) findViewById(R.id.cnb_file_name);
        mTvAttachment = (TextView) findViewById(R.id.tv_attachment);



        /**
         * This part will deal with the spinners and extract data from spinners
         */
        ArrayAdapter<String> adapterGL = new ArrayAdapter<String>(Contribute.this, android.R.layout.simple_spinner_item, mGraduationLevelArray);
        adapterGL.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerGraduationLevel.setAdapter(adapterGL);
        mSpinnerGraduationLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mSpinnerGraduationLevelPos = position;
                mGraduationLevel = mSpinnerGraduationLevel.getSelectedItem().toString();

                if (mSpinnerGraduationLevelPos == 0) {

                    ArrayAdapter<String>adapter1 = new ArrayAdapter<String>(Contribute.this, android.R.layout.simple_spinner_item, mUgCourseArray);
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerCourse.setAdapter(adapter1);

                } else if (mSpinnerGraduationLevelPos == 1) {

                    ArrayAdapter<String>adapter2 = new ArrayAdapter<String>(Contribute.this, android.R.layout.simple_spinner_item, mDdCourseArray);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerCourse.setAdapter(adapter2);

                } else {

                    ArrayAdapter<String>adapter3 = new ArrayAdapter<String>(Contribute.this, android.R.layout.simple_spinner_item, mPgCourseArray);
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

                    ArrayAdapter<String>adapter1 = new ArrayAdapter<String>(Contribute.this, android.R.layout.simple_spinner_item, mUgSemesterArray);
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerSemester.setAdapter(adapter1);

                } else if (mSpinnerGraduationLevelPos ==1) {

                    ArrayAdapter<String>adapter2 = new ArrayAdapter<String>(Contribute.this, android.R.layout.simple_spinner_item, mDdSemesterArray);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerSemester.setAdapter(adapter2);

                } else {

                    ArrayAdapter<String>adapter3 = new ArrayAdapter<String>(Contribute.this, android.R.layout.simple_spinner_item, mPgSemesterArray);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_attach, menu);
        inflater.inflate(R.menu.menu_send, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.send:
                sendEmail();
                return true;
            case R.id.attach:
                openFolder();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_STORAGE && resultCode == RESULT_OK) {
            URI = data.getData();
            mTvAttachment.setText(URI.getLastPathSegment());
//            tvAttachment.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This will execute when the send button is clicked
     */
    public void sendEmail() {
        Log.d(TAG, "sendEmail: " + mGraduationLevel + mCourse + mSemester + mEditText.getText().toString());

        mFileName = mEditText.getText().toString();

        try {
            String message = "Graduation Level : "+ mGraduationLevel +"\nCourse : "+ mCourse +"\nSemester : "+ mSemester +"\nFile Name : "+ mFileName;
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"apurbar06@gmail.com"});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Contribution to My Ebook.");
            if (URI != null) {
                emailIntent.putExtra(Intent.EXTRA_STREAM, URI);
            }
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
            this.startActivity(Intent.createChooser(emailIntent, "Please use institute mail id"));
        } catch (Throwable t) {
            runOnUiThread(new Runnable(){
                public void run() {
                    Toast.makeText(Contribute.this, "Please install Gmail", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * This will execute when the attach button is clicked
     */
    public void openFolder() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("return-data", true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_STORAGE);
    }
}

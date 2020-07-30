package com.example.myebook.Activity;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
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

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myebook.R;

import java.io.File;
import java.util.List;

public class Upload extends AppCompatActivity {

    private static final String TAG = "Upload";
    private Spinner mSpinnerGraduationLevel;
    private Spinner mSpinnerCourse;
    private Spinner mSpinnerSemester;
    private int mSpinnerGraduationLevelPos;
    private int mSpinnerCoursePos;
    private String mGraduationLevel;
    private String mCourse;
    private String mSemester;
    private String mComment;
    private EditText mEditText;
    private TextView mTvAttachment;
    private Uri URI = null;
    private static final int PICK_FROM_STORAGE = 101;
    private static final String[] mGraduationLevelArray = {"Undergraduate", "Dual Degree", "Postgraduate (M.Tech)"};
    private static final String[] mUgCourseArray = {"COE", "EDM", "MDM", "MSM"};
    private static final String[] mDdCourseArray = {"CED", "EVD", "ESD", "MPD", "MFD"};
    private static final String[] mPgCourseArray = {"CDS", "EDS", "MDS", "SMT"};
    private static final String[] mUgSemesterArray = {"First", "Second", "Third", "Fourth", "Fifth", "Sixth", "Seventh", "Eighth"};
    private static final String[] mDdSemesterArray = {"First", "Second", "Third", "Fourth", "Fifth", "Sixth", "Seventh", "Eighth", "Ninth", "Tenth"};
    private static final String[] mPgSemesterArray = {"First", "Second", "Third", "Fourth"};


    // GetContent creates an ActivityResultLauncher<String> to allow you to pass
    // in the mime type you'd like to allow the user to select
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    // Handle the returned Uri
                    URI = uri;
                    mTvAttachment.setText(URI.getLastPathSegment());

                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mSpinnerGraduationLevel = (Spinner)findViewById(R.id.cnb_spinner_graduation_level);
        mSpinnerCourse = (Spinner)findViewById(R.id.cnb_spinner_course);
        mSpinnerSemester = (Spinner)findViewById(R.id.cnb_spinner_semester);
        mEditText = (EditText) findViewById(R.id.cnb_file_name);
        mTvAttachment = findViewById(R.id.tv_attachment);



        /**
         * This part will deal with the spinners and extract data from spinners
         */
        ArrayAdapter<String> adapterGL = new ArrayAdapter<String>(Upload.this, android.R.layout.simple_spinner_item, mGraduationLevelArray);
        adapterGL.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerGraduationLevel.setAdapter(adapterGL);
        mSpinnerGraduationLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mSpinnerGraduationLevelPos = position;
                mGraduationLevel = mSpinnerGraduationLevel.getSelectedItem().toString();

                if (mSpinnerGraduationLevelPos == 0) {

                    ArrayAdapter<String>adapter1 = new ArrayAdapter<String>(Upload.this, android.R.layout.simple_spinner_item, mUgCourseArray);
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerCourse.setAdapter(adapter1);

                } else if (mSpinnerGraduationLevelPos == 1) {

                    ArrayAdapter<String>adapter2 = new ArrayAdapter<String>(Upload.this, android.R.layout.simple_spinner_item, mDdCourseArray);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerCourse.setAdapter(adapter2);

                } else {

                    ArrayAdapter<String>adapter3 = new ArrayAdapter<String>(Upload.this, android.R.layout.simple_spinner_item, mPgCourseArray);
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

                    ArrayAdapter<String>adapter1 = new ArrayAdapter<String>(Upload.this, android.R.layout.simple_spinner_item, mUgSemesterArray);
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerSemester.setAdapter(adapter1);

                } else if (mSpinnerGraduationLevelPos ==1) {

                    ArrayAdapter<String>adapter2 = new ArrayAdapter<String>(Upload.this, android.R.layout.simple_spinner_item, mDdSemesterArray);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerSemester.setAdapter(adapter2);

                } else {

                    ArrayAdapter<String>adapter3 = new ArrayAdapter<String>(Upload.this, android.R.layout.simple_spinner_item, mPgSemesterArray);
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
                Intent intent = new Intent(Upload.this, SubjectList.class);
                startActivity(intent);
                this.finish();
                return true;
            case R.id.send:
                sendEmail();
                return true;
            case R.id.attach:
                mGetContent.launch("*/*");
//                openFolder();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(Upload.this, SubjectList.class);
        startActivity(intent);
        this.finish();
    }




    /**
     * This will execute when the send button is clicked
     */
    public void sendEmail() {
        Log.d(TAG, "sendEmail: " + mGraduationLevel + mCourse + mSemester + mEditText.getText().toString());

        mComment = mEditText.getText().toString();

        try {
            String message = "Graduation Level : "+ mGraduationLevel +"\nCourse : "+ mCourse +"\nSemester : "+ mSemester +"\nComment : "+ mComment;
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/html");
            emailIntent.setPackage("com.google.android.gm");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"apurbar011@gmail.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contribution to My Ebook.");
            if (URI != null) {
                emailIntent.putExtra(Intent.EXTRA_STREAM, URI);
            }
            emailIntent.putExtra(Intent.EXTRA_TEXT, message);
            this.startActivity(Intent.createChooser(emailIntent, "Send Email"));
        } catch (Throwable t) {
            runOnUiThread(new Runnable(){
                public void run() {
                    Toast.makeText(Upload.this, "Gmail not found", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

//    /**
//     * This will execute when the attach button is clicked
//     */
//    public void openFolder() {
//        Intent intent = new Intent();
//        intent.setType("*/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.putExtra("return-data", true);
//        startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_STORAGE);
//    }
//
//
//    /**
//     * This will execute when startActivityForResult() in openFolder is called
//     */
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_FROM_STORAGE && resultCode == RESULT_OK) {
//            URI = data.getData();
//            mTvAttachment.setText(URI.getLastPathSegment());
////            tvAttachment.setVisibility(View.VISIBLE);
//        }
//    }

}

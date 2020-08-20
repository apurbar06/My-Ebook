package com.example.myebook.Activity;

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
    private String mComment;
    private EditText mEditText;
    private TextView mTvAttachment;
    private Uri URI = null;
    private static final int PICK_FROM_STORAGE = 101;
    private static final String[] mGraduationLevelArray = {"Undergraduate", "Dual Degree", "Postgraduate (M.Tech)", "GATE Corner"};
    private static final String[] mUgCourseArray = {"COE", "EDM", "MDM", "MSM"};
    private static final String[] mDdCourseArray = {"CED", "EVD", "ESD", "MPD", "MFD"};
    private static final String[] mPgCourseArray = {"CDS", "EDS", "MDS", "SMT"};
    private static final String[] mGateCourseArray = {"Computer Science", "Electronics", "Mechanical"};
    private static final String[] mUgSemesterArray = {"First", "Second", "Third", "Fourth", "Fifth", "Sixth", "Seventh", "Eighth"};
    private static final String[] mDdSemesterArray = {"First", "Second", "Third", "Fourth", "Fifth", "Sixth", "Seventh", "Eighth", "Ninth", "Tenth"};
    private static final String[] mPgSemesterArray = {"First", "Second", "Third", "Fourth"};
    private static final String[] mGateSemesterArray = {"NA"};


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
        setContentView(R.layout.activity_contribute);
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
        ArrayAdapter<String>adapterGL = new ArrayAdapter<String>(Contribute.this, android.R.layout.simple_spinner_item, mGraduationLevelArray);
        adapterGL.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerGraduationLevel.setAdapter(adapterGL);
        mSpinnerGraduationLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mSpinnerGraduationLevelPos = position;
                mGraduationLevel = mSpinnerGraduationLevel.getSelectedItem().toString();

                //checking what is selected as graduation level and setting the course spinner accordingly

                if (mSpinnerGraduationLevelPos == 0) {

                    ArrayAdapter<String>adapterUgCourse = new ArrayAdapter<String>(Contribute.this, android.R.layout.simple_spinner_item, mUgCourseArray);
                    adapterUgCourse.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerCourse.setAdapter(adapterUgCourse);

                } else if (mSpinnerGraduationLevelPos == 1) {

                    ArrayAdapter<String>adapterDdCourse = new ArrayAdapter<String>(Contribute.this, android.R.layout.simple_spinner_item, mDdCourseArray);
                    adapterDdCourse.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerCourse.setAdapter(adapterDdCourse);

                } else if (mSpinnerGraduationLevelPos == 2){

                    ArrayAdapter<String>adapterPgCourse = new ArrayAdapter<String>(Contribute.this, android.R.layout.simple_spinner_item, mPgCourseArray);
                    adapterPgCourse.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerCourse.setAdapter(adapterPgCourse);

                } else {

                    ArrayAdapter<String>adapterGateCourse = new ArrayAdapter<String>(Contribute.this, android.R.layout.simple_spinner_item, mGateCourseArray);
                    adapterGateCourse.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerCourse.setAdapter(adapterGateCourse);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //checking what is selected as graduation level and setting the semester spinner accordingly

        mSpinnerCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mSpinnerCoursePos = position;
                mCourse = mSpinnerCourse.getSelectedItem().toString();

                //checking what is selected as graduation level and setting the semester spinner accordingly

                if (mSpinnerGraduationLevelPos == 0){

                    ArrayAdapter<String>adapterUgSemester = new ArrayAdapter<String>(Contribute.this, android.R.layout.simple_spinner_item, mUgSemesterArray);
                    adapterUgSemester.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerSemester.setAdapter(adapterUgSemester);

                } else if (mSpinnerGraduationLevelPos ==1) {

                    ArrayAdapter<String>adapterDdSemester = new ArrayAdapter<String>(Contribute.this, android.R.layout.simple_spinner_item, mDdSemesterArray);
                    adapterDdSemester.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerSemester.setAdapter(adapterDdSemester);

                } else if (mSpinnerGraduationLevelPos ==2) {

                    ArrayAdapter<String>adapterPgSemester = new ArrayAdapter<String>(Contribute.this, android.R.layout.simple_spinner_item, mPgSemesterArray);
                    adapterPgSemester.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerSemester.setAdapter(adapterPgSemester);
                } else {

                    ArrayAdapter<String>adapterGateSemester = new ArrayAdapter<String>(Contribute.this, android.R.layout.simple_spinner_item, mGateSemesterArray);
                    adapterGateSemester.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerSemester.setAdapter(adapterGateSemester);
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
                Log.d(TAG, "onItemSelected: "+mGraduationLevel+mCourse+mSemester);

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
                Intent intent = new Intent(Contribute.this, SubjectList.class);
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
        Intent intent = new Intent(Contribute.this, SubjectList.class);
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
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"myebook711@gmail.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contribution to My Ebook.");
            if (URI != null) {
                emailIntent.putExtra(Intent.EXTRA_STREAM, URI);
            }
            emailIntent.putExtra(Intent.EXTRA_TEXT, message);
            this.startActivity(Intent.createChooser(emailIntent, "Send Email"));
        } catch (Throwable t) {
            runOnUiThread(new Runnable(){
                public void run() {
                    Toast.makeText(Contribute.this, "Gmail not found", Toast.LENGTH_LONG).show();
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

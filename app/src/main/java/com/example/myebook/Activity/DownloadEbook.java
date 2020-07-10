package com.example.myebook.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myebook.R;
import com.example.myebook.Adapter.DownloadableEbookListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DownloadEbook extends AppCompatActivity {

    private static final String TAG = "DownloadEbook";
    ListView mListView;
    LinearLayout mLinLaHeaderProgress;
    String mJsonString;
    private String mGraduationLevel;
    private String mCourse;
    private String mSemester;
    private SharedPreferences mSharedPreferences;
    private boolean PointerIsAtFinishingStage = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Map<String, Object> mSemesterMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_ebook);
        mListView = (ListView) findViewById(R.id.listView);
        mLinLaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mSharedPreferences = this.getSharedPreferences("myEbook", Context.MODE_PRIVATE);
        mGraduationLevel = mSharedPreferences.getString("GraduationLevel", null);
        mCourse = mSharedPreferences.getString("Course", null);
        mSemester = mSharedPreferences.getString("Semester", null);
        Log.d(TAG, "onCreate : "  + mGraduationLevel + mCourse + mSemester);

//        getJSON("http://192.168.43.32/My%20Ebook%20Android%20app/getdata.php");
//        getJSON("https://sheetsu.com/apis/v1.0su/12e1cfc87bb8");



        // SHOW THE SPINNER WHILE LOADING FEEDS
        mLinLaHeaderProgress.setVisibility(View.VISIBLE);
        // HIDE THE LISTVIEW WHILE LOADING FEEDS
        mListView.setVisibility(View.GONE);
        db.collection(mGraduationLevel).document(mCourse).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        try {
                            mSemesterMap = document.getData();
                            Log.d(TAG, "onComplete: " + mSemesterMap);
                            loadIntoListView();
                        } catch (Exception e) {
                            // HIDE THE SPINNER
                            mLinLaHeaderProgress.setVisibility(View.GONE);
                            // SHOW THE LISTVIEW
                            mListView.setVisibility(View.VISIBLE);
                            runOnUiThread(new Runnable(){
                                public void run() {
                                    Toast.makeText(DownloadEbook.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                    } else {
                        // HIDE THE SPINNER
                        mLinLaHeaderProgress.setVisibility(View.GONE);
                        // SHOW THE LISTVIEW
                        mListView.setVisibility(View.VISIBLE);
                        runOnUiThread(new Runnable(){
                            public void run() {
                                Toast.makeText(DownloadEbook.this, "Currently unavailable", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    // HIDE THE SPINNER
                    mLinLaHeaderProgress.setVisibility(View.GONE);
                    // SHOW THE LISTVIEW
                    mListView.setVisibility(View.VISIBLE);
                    runOnUiThread(new Runnable(){
                        public void run() {
                            Toast.makeText(DownloadEbook.this, "Check internet connectivity", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(PointerIsAtFinishingStage) {
                        PointerIsAtFinishingStage = false;
                        loadIntoListView();
                } else {
                    Intent intent = new Intent(DownloadEbook.this, SubjectList.class);
                    startActivity(intent);
                    this.finish();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(PointerIsAtFinishingStage) {
            PointerIsAtFinishingStage = false;
            loadIntoListView();
        } else {
            Intent intent = new Intent(DownloadEbook.this, SubjectList.class);
            startActivity(intent);
            this.finish();
        }
    }



    private void loadIntoListView() {

        List<String> subjectList = new ArrayList<String>();

        //getting subjects
        for (Map.Entry<String, Object> semester : mSemesterMap.entrySet()) {
            if (semester.getKey().equals(mSemester)) {
                Map<String, Object> subjectMap = (Map<String, Object>) semester.getValue();
                for (Map.Entry<String, Object> dataEntry : subjectMap.entrySet()) {
                    subjectList.add(dataEntry.getKey());
                }
            }
        }


        String[] subjects = subjectList.toArray(new String[subjectList.size()]);

        // HIDE THE SPINNER AFTER LOADING FEEDS
        mLinLaHeaderProgress.setVisibility(View.GONE);
        // SHOW THE LISTVIEW AFTER LOADING FEEDS
        mListView.setVisibility(View.VISIBLE);

        //the array adapter to load data into list
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, subjects);

        //attaching adapter to listview
        mListView.setAdapter(arrayAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: item is " + mListView.getItemAtPosition(position));

                //extracting the selected subject
                String selectedSubject = (String) mListView.getItemAtPosition(position);

                List<String> eBooksList = new ArrayList<String>();
                List<String> eBooksURLList = new ArrayList<String>();

                PointerIsAtFinishingStage = true;

                //getting ebooks and URLs
                for (Map.Entry<String, Object> semester : mSemesterMap.entrySet()) {
                    if (semester.getKey().equals(mSemester)) {
                        Map<String, Object> subjectMap = (Map<String, Object>) semester.getValue();
                        for (Map.Entry<String, Object> subject : subjectMap.entrySet()) {
                            if (subject.getKey().equals(selectedSubject)) {
                                Map<String, Object> eBookMap = (Map<String, Object>) subject.getValue();
                                for (Map.Entry<String, Object> dataEntry : eBookMap.entrySet()) {
                                    eBooksList.add(dataEntry.getKey());
                                    eBooksURLList.add(dataEntry.getValue().toString());
                                }
                            }
                        }
                    }
                }


                String[] eBooks = eBooksList.toArray(new String[eBooksList.size()]);
                String[] eBooksURL = eBooksURLList.toArray(new String[eBooksURLList.size()]);


                //set the list view form where one can download ebook
                DownloadableEbookListAdapter adapter = new DownloadableEbookListAdapter(DownloadEbook.this, selectedSubject, eBooks, eBooksURL);
                mListView.setAdapter(adapter);

            }
        });

    }


}

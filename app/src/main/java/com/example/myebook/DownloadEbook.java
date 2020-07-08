package com.example.myebook;

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

import com.example.myebook.handler.DownloadableEbookListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;

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

                        mSemesterMap = document.getData();
                        loadIntoListView();

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

        String[] subjects = new String[mSemesterMap.size()];

        //getting subjects
        for (Map.Entry<String, Object> semester : mSemesterMap.entrySet()) {
            if (semester.getKey().equals(mSemester)) {
                Map<String, Object> subjectMap = (Map<String, Object>) semester.getValue();
                int j = 0;
                for (Map.Entry<String, Object> subject : subjectMap.entrySet()) {
                    subjects[j] = subject.getKey();
                    Log.d(TAG, "loadIntoListView: " + subjects[j]);
                    j++;
                }
            }
        }

        //removing all null values from subjects
        List<String> list = new ArrayList<String>();
        for(String s : subjects) {
            if(s != null && s.length() > 0) {
                list.add(s);
            }
        }
        subjects = list.toArray(new String[list.size()]);

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

                String[] eBooks = new String[mSemesterMap.size()];
                String[] eBooksURL = new String[mSemesterMap.size()];

                PointerIsAtFinishingStage = true;

                //getting ebooks and URLs
                for (Map.Entry<String, Object> semester : mSemesterMap.entrySet()) {
                    if (semester.getKey().equals(mSemester)) {
                        Map<String, Object> subjectMap = (Map<String, Object>) semester.getValue();
                        for (Map.Entry<String, Object> subject : subjectMap.entrySet()) {
                            if (subject.getKey().equals(selectedSubject)) {
                                Map<String, Object> eBookMap = (Map<String, Object>) subject.getValue();
                                int i = 0;
                                for (Map.Entry<String, Object> dataEntry : eBookMap.entrySet()) {
                                    eBooks[i] = dataEntry.getKey();
                                    eBooksURL[i] = dataEntry.getValue().toString();
                                    Log.d(TAG, "onItemClick: ebook : " + eBooks[i]+ "  url : " + eBooksURL[i]);
                                    i++;
                                }
                            }
                        }
                    }
                }

                //removing all null values from eBooks
                List<String> list = new ArrayList<String>();
                for(String s : eBooks) {
                    if(s != null && s.length() > 0) {
                        list.add(s);
                    }
                }
                eBooks = list.toArray(new String[list.size()]);

                //removing all null values from eBooksURL
                List<String> list1 = new ArrayList<String>();
                for(String s : eBooksURL) {
                    if(s != null && s.length() > 0) {
                        list1.add(s);
                    }
                }
                eBooksURL = list1.toArray(new String[list1.size()]);


                //set the list view form where one can download ebook
                DownloadableEbookListAdapter adapter = new DownloadableEbookListAdapter(DownloadEbook.this, selectedSubject, eBooks, eBooksURL);
                mListView.setAdapter(adapter);

            }
        });

    }


}

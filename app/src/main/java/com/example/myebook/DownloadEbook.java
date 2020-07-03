package com.example.myebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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
        getJSON("https://sheetsu.com/apis/v1.0su/12e1cfc87bb8");

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(PointerIsAtFinishingStage) {
                    try {
                        PointerIsAtFinishingStage = false;
                        loadIntoListView(mJsonString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Intent intent = new Intent(DownloadEbook.this, ReadEbook.class);
                    startActivity(intent);
                    this.finish();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //this method is actually fetching the json string
    private void getJSON(final String urlWebService) {
        /*
         * As fetching the json string is a network operation
         * And we cannot perform a network operation in main thread
         * so we need an AsyncTask
         * The constrains defined here are
         * Void -> We are not passing anything
         * Void -> Nothing at progress update as well
         * String -> After completion it should return a string and it will be the json string
         * */
        class GetJSON extends AsyncTask<Void, Void, String> {

            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some time
            @Override
            protected void onPreExecute() {
                // SHOW THE SPINNER WHILE LOADING FEEDS
                mLinLaHeaderProgress.setVisibility(View.VISIBLE);
                // HIDE THE LISTVIEW WHILE LOADING FEEDS
                mListView.setVisibility(View.GONE);
                super.onPreExecute();
            }

            //this method will be called after execution
            @Override
            protected void onPostExecute(String s) {
                // HIDE THE SPINNER AFTER LOADING FEEDS
                mLinLaHeaderProgress.setVisibility(View.GONE);
                // SHOW THE LISTVIEW AFTER LOADING FEEDS
                mListView.setVisibility(View.VISIBLE);
                super.onPostExecute(s);

                try {
                    mJsonString = s;
                    loadIntoListView(mJsonString);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {

                try {
                    //creating a URL
                    URL url = new URL(urlWebService);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json;

                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {

                        //appending it to string builder
                        sb.append(json + "\n");
                    }

                    //finally returning the read string
                    return sb.toString().trim();
                } catch (Exception e) {
                    runOnUiThread(new Runnable(){
                        public void run() {
                            Toast.makeText(DownloadEbook.this, "Please make sure that you have a proper internet connection", Toast.LENGTH_LONG).show();
                        }
                    });
                    return "";
                }
            }
        }

        //creating asynctask object and executing it
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    private void loadIntoListView(String json) throws JSONException {
        //creating a json array from the json string
        final JSONArray jsonArray = new JSONArray(json);

        //creating a string array
        String[] GraduationLevel = new String[jsonArray.length()];
        String[] Course = new String[jsonArray.length()];
        String[] Semester = new String[jsonArray.length()];
        String[] subjects = new String[jsonArray.length()];

        //looping through all the elements in json array
        for (int i = 0; i < jsonArray.length(); i++) {

            //getting json object from the json array
            JSONObject obj = jsonArray.getJSONObject(i);

            //getting the values from the json object and putting it inside string array
            GraduationLevel[i] = obj.getString("graduation_level");
            Course[i] =obj.getString("department");
            Semester[i] = obj.getString("semester");

            //taking those subjects whose GraduationLevel, Course and Semester are mach with users choice
            if(GraduationLevel[i].equals(mGraduationLevel) && Course[i].equals(mCourse) && Semester[i].equals(mSemester)) {
                subjects[i] = obj.getString("subject");
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

        // getting unique subjects
        final String[] uniqueSubjects = new HashSet<String>(Arrays.asList(subjects)).toArray(new String[0]);

        //the array adapter to load data into list
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, uniqueSubjects);

        //attaching adapter to listview
        mListView.setAdapter(arrayAdapter);

        //onClickListener in subject listView
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: item is " + mListView.getItemAtPosition(position));

                //extracting the selected subject
                String selectedSubject = (String) mListView.getItemAtPosition(position);

                String[] GraduationLevel = new String[jsonArray.length()];
                String[] Course = new String[jsonArray.length()];
                String[] Semester = new String[jsonArray.length()];
                String[] subjects = new String[jsonArray.length()];
                String[] eBooks = new String[jsonArray.length()];
                String[] eBooksURL = new String[jsonArray.length()];

                PointerIsAtFinishingStage = true;

                //looping through all the elements in json array
                for (int i = 0; i < jsonArray.length(); i++) {

                    //getting json object from the json array
                    JSONObject obj = null;
                    try {
                        obj = jsonArray.getJSONObject(i);

                        //getting the values from the json object and putting it inside string array
                        GraduationLevel[i] = obj.getString("graduation_level");
                        Course[i] =obj.getString("department");
                        Semester[i] = obj.getString("semester");
                        subjects[i] = obj.getString("subject");

                        //taking those eBooks whose GraduationLevel, Course, Semester and Subject are mach with users choice
                        if (GraduationLevel[i].equals(mGraduationLevel) && Course[i].equals(mCourse) && Semester[i].equals(mSemester) && subjects[i].equals(selectedSubject)) {
                            eBooks[i] = obj.getString("eBook_name");
                            eBooksURL[i] = obj.getString("eBook_URL");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
                List<String> List = new ArrayList<String>();
                for(String s : eBooksURL) {
                    if(s != null && s.length() > 0) {
                        List.add(s);
                    }
                }
                eBooksURL = List.toArray(new String[List.size()]);


                //set the list view form where one can download ebook
                DownloadableEbookListAdapter adapter = new DownloadableEbookListAdapter(DownloadEbook.this, selectedSubject, eBooks, eBooksURL);
                mListView.setAdapter(adapter);
            }
        });
    }

}

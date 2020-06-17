package com.example.myebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    ListView listView;
    String mJsonString;
    private String mGraduationLevel;
    private String mCourse;
    private String mSemester;
    private boolean DownloadableEbookListPointer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_ebook);
        listView = (ListView) findViewById(R.id.listView);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mGraduationLevel = intent.getExtras().getString("GraduationLevel");
        mCourse = intent.getExtras().getString("Course");
        mSemester = intent.getExtras().getString("Semester");
        Log.d(TAG, "onCreate : "  + mGraduationLevel + mCourse + mSemester);

        getJSON("http://192.168.43.32/My%20Ebook%20Android%20app/getdata.php");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(DownloadableEbookListPointer) {
                    try {
                        DownloadableEbookListPointer = false;
                        loadIntoListView(mJsonString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
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
                super.onPreExecute();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String s) {
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
                    return null;
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
        listView.setAdapter(arrayAdapter);

        //onClickListener in subject listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: item is " + listView.getItemAtPosition(position));

                //extracting the selected subject
                String selectedSubject = (String) listView.getItemAtPosition(position);

                String[] GraduationLevel = new String[jsonArray.length()];
                String[] Course = new String[jsonArray.length()];
                String[] Semester = new String[jsonArray.length()];
                String[] subjects = new String[jsonArray.length()];
                String[] eBooks = new String[jsonArray.length()];
                String[] eBooksURL = new String[jsonArray.length()];

                DownloadableEbookListPointer = true;

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
                DownloadableEbookListAdapter adapter=new DownloadableEbookListAdapter(DownloadEbook.this, selectedSubject, eBooks, eBooksURL);
                listView.setAdapter(adapter);
            }
        });
    }

}

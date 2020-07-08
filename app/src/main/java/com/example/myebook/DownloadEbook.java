package com.example.myebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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


        db.collection("CED")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "onComplete: " + document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }



}

package com.example.taskmaster.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taskmaster.R;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = SettingActivity.class.getSimpleName();
    public static final String USERNAME = "username";
    private EditText mUsername;
    private Button mSavaBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mUsername = findViewById(R.id.edit_user_name);
        mSavaBtn = findViewById(R.id.btn_save);
        mSavaBtn.setOnClickListener(view -> {
            Log.i(TAG , "Submit button clicked");
            saveUserName();
        });
    }


    public void saveUserName(){

        String userName =  mUsername.getText().toString();
        Toast.makeText(this, "The username is : " + userName, Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor preferanceEditor = sharedPreferences.edit();
        preferanceEditor.putString(USERNAME, userName);
        preferanceEditor.apply();

    }
}
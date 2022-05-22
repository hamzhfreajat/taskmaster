package com.example.taskmaster.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.MainActivity;
import com.example.taskmaster.R;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = SettingActivity.class.getSimpleName();
    public static final String USERNAME = "username";
    public static final String TEAMNAME = "teamName";
    private EditText mUsername;
    private Button mSavaBtn;
    private Spinner spinner;
    private List<String> teamList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mUsername = findViewById(R.id.edit_user_name);
        mSavaBtn = findViewById(R.id.btn_save);
        spinner = findViewById(R.id.setting_team_spinner);



        Handler handler = new Handler(Looper.getMainLooper() , msg -> {

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, teamList);
            adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

            spinner.setAdapter(adapter);



            mSavaBtn.setOnClickListener(view -> {
                Log.i(TAG , "Submit button clicked");
                saveUserName();
            });
            return true ;
        });




        Amplify.API.query(
                ModelQuery.list(Team.class),
                response -> {

                    for (Team team : response.getData()) {
                        teamList.add(team.getName());
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("data" , "Done");

                    Message message = new Message();
                    message.setData(bundle);
                    handler.sendMessage(message);

                },
                error -> Log.e("MyAmplifyApp", "Query failure", error)
        );


    }


    public void saveUserName(){

        String userName =  mUsername.getText().toString();
        Toast.makeText(this, "The username is : " + userName, Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor preferanceEditor = sharedPreferences.edit();
        preferanceEditor.putString(USERNAME, userName);
        preferanceEditor.putString(TEAMNAME, spinner.getSelectedItem().toString());
        preferanceEditor.apply();
        startActivity(new Intent(getApplicationContext() , MainActivity.class));
    }
}
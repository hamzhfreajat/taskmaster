package com.example.taskmaster;

import androidx.annotation.NonNull;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.analytics.AnalyticsEvent;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.data.AppDatabase;
import com.example.taskmaster.data.TaskData;
import com.example.taskmaster.ui.CustomRecyclerView;
import com.example.taskmaster.ui.LoginActivity;
import com.example.taskmaster.ui.SettingActivity;
import com.example.taskmaster.ui.TaskDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView mUserTitle;
    List<TaskData> taskData = new ArrayList<>();
    private Handler handler;
    private List<Task> mytasks;
    private String userId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        AnalyticsEvent event = AnalyticsEvent.builder()
                .name("openMyApp")
                .addProperty("Successful", true)
                .build();

        Amplify.Analytics.recordEvent(event);

        Button addTaskBtn = findViewById(R.id.btn_add_task);
        addTaskBtn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext() , AddTaskActivity.class));
        });


        mUserTitle = findViewById(R.id.text_user);



        authSession("onCreate");

    }

    @Override
    protected void onResume() {
        setUserName();
        mytasks = new ArrayList<>();
        Handler handler = new Handler(Looper.getMainLooper() , msg -> {
            RecyclerView recyclerView = findViewById(R.id.recycler_view);

            CustomRecyclerView customRecyclerView = new CustomRecyclerView(mytasks, new CustomRecyclerView.CustomClickListener() {
                @Override
                public void onTaskItemClicked(int position) {
                    Intent taskDetailActivity = new Intent(getApplicationContext() , TaskDetailActivity.class);
                    taskDetailActivity.putExtra("id" ,  mytasks.get(position).getId().toString());
                    startActivity(taskDetailActivity);
                }
            });

            recyclerView.setAdapter(customRecyclerView);



            recyclerView.setHasFixedSize(true);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            return true ;
        });
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String teamName = sharedPreferences.getString("teamName" , "") ;
        Log.i("teamName" , teamName) ;

        Amplify.API.query(
                ModelQuery.list(Team.class , Team.NAME.contains(teamName)),
                response -> {

                    for (Team team : response.getData()) {
                        mytasks = team.getTasks();
                    }
                    Log.i("tasks" , mytasks.toString()) ;
                    Bundle bundle = new Bundle();
                    bundle.putString("data" , "Done");

                    Message message = new Message();
                    message.setData(bundle);
                    handler.sendMessage(message);

                },
                error -> Log.e("MyAmplifyApp", "Query failure", error)
        );





        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                navigateToSettings();
                return true;

            case R.id.logout:
                logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
//
    public void navigateToSettings(){
        Intent settingIntent = new Intent(this , SettingActivity.class);
        startActivity(settingIntent);
    }

    private void authSession(String method) {




        Amplify.Auth.fetchAuthSession(
                result -> {
                    Log.i(TAG, "Auth Session => " + method + result.toString()) ;

                    AWSCognitoAuthSession cognitoAuthSession = (AWSCognitoAuthSession) result;

                    switch(cognitoAuthSession.getIdentityId().getType()) {
                        case SUCCESS:
                        {
                            Log.i("AuthQuickStart", "IdentityId: " + cognitoAuthSession.getIdentityId().getValue());
                            userId = cognitoAuthSession.getIdentityId().getValue();
                            break;
                        }

                        case FAILURE:
                            Log.i("AuthQuickStart", "IdentityId not present because: " + cognitoAuthSession.getIdentityId().getError().toString());
                    }


                },
                error -> Log.e(TAG, error.toString())
        );
    }


    public void logout(){
        Amplify.Auth.signOut(
                () -> {
                    Log.i(TAG, "Signed out successfully");
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    authSession("logout");
                    finish();
                },
                error -> Log.e(TAG, error.toString())
        );
    }


    public void setUserName(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserTitle.setText( sharedPreferences.getString("teamName" , "team1") + " : " +sharedPreferences.getString("username" , "My") +" Tasks");
    }


}
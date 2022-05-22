package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.data.AppDatabase;
import com.example.taskmaster.data.TaskData;
import com.example.taskmaster.ui.CustomRecyclerView;
import com.example.taskmaster.ui.TaskDetailActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class AddTaskActivity extends AppCompatActivity {

    private static final String TAG = AddTaskActivity.class.getName();
    private EditText taskTitle;
    private EditText taskDesc;
    private Spinner spinner;
    private List<Team> teamList = new ArrayList<>();
    private Team team ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


//        try {
//            Amplify.addPlugin(new AWSDataStorePlugin());
//            Amplify.addPlugin(new AWSApiPlugin());
//            Amplify.configure(getApplicationContext());
//
//            Log.i("Tutorial", "Initialized Amplify");
//        } catch (AmplifyException e) {
//            Log.e("Tutorial", "Could not initialize Amplify", e);
//        }

        super.onCreate(savedInstanceState);

//        Handler handler2 = new Handler(Looper.getMainLooper() , msg -> {
//
//
//            return true ;
//        });

        setContentView(R.layout.activity_add_task);
        Button addTask = findViewById(R.id.btn_submit_task);
        spinner = findViewById(R.id.team_spinner);


        Handler handler = new Handler(Looper.getMainLooper() , msg -> {
            Log.i("adapter" , teamList.toString()) ;

            List<String> spinnerList = teamList.stream().map(index -> index.getName()).collect(Collectors.toList());
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, spinnerList);
            adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

            spinner.setAdapter(adapter);


            return true ;
        });


        Amplify.API.query(
                ModelQuery.list(Team.class),
                response -> {

                    Log.i("MyAmplifyApp", "Query succ");
                    for (Team team : response.getData()) {
                        teamList.add(team);
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("data" , "Done");

                    Message message = new Message();
                    message.setData(bundle);
                    handler.sendMessage(message);

                },
                error -> Log.e("MyAmplifyApp", "Query failure", error)
        );


        addTask.setOnClickListener(v -> {


            taskTitle = findViewById(R.id.edit_task_title);
            taskDesc = findViewById(R.id.edit_task_desc);

            String spinnerSelected = spinner.getSelectedItem().toString();
            Team selectedTeam = teamList.get(0);

            for (Team team :
                    teamList) {
                if (team.getName() == spinnerSelected) {
                    selectedTeam = team ;
                }
            }





            Task item = Task.builder()
                    .title(taskTitle.getText().toString())
                    .body(taskDesc.getText().toString())
                    .status(spinner.getSelectedItem().toString())
                    .teamTasksId(selectedTeam.getId())
                    .build();



//
            Amplify.DataStore.save(item,
                    success -> Log.i(TAG, "Saved item "),
                    error -> Log.e(TAG, "Could not save item to DataStore", error)
            );

//            Amplify.API.mutate(ModelMutation.create(item) ,
//                    success -> Log.i(TAG, "Saved item API") ,
//                    error -> Log.e(TAG, "Could not save item to DataStore", error)
//            );





//            TaskData taskData = new TaskData(taskTitle.getText().toString() , taskDesc.getText().toString() ,spinner.getSelectedItem().toString());
//            Long newTask = AppDatabase.getInstance(getApplicationContext()).taskDao().insertTask(taskData);
            startActivity(new Intent(getApplicationContext() , MainActivity.class));
        });
    }
}
package com.example.taskmaster;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
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

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class AddTaskActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 123;
    private static final String TAG = AddTaskActivity.class.getName();

    private EditText taskTitle;
    private EditText taskDesc;

    private Spinner spinner;

    private List<Team> teamList = new ArrayList<>();
    private String imageKey = "" ;

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

        // LAB 37
        Button upload = findViewById(R.id.btn_image_upload);

        upload.setOnClickListener(view -> uploadImage());


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
                    .image(imageKey)
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


    public void uploadImage(){

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            // Handle error
            Log.e(TAG, "onActivityResult: Error getting image from device");
            return;
        }

        switch(requestCode) {
            case REQUEST_CODE:
                // Get photo picker response for single select.
                Uri currentUri = data.getData();

                // Do stuff with the photo/video URI.
                Log.i(TAG, "onActivityResult: the uri is => " + currentUri);

                try {
                    Bitmap bitmap = getBitmapFromUri(currentUri);

                    File file = new File(getApplicationContext().getFilesDir(), "image.jpg");
                    OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.close();

                    // upload to s3
                    // uploads the file
                    Amplify.Storage.uploadFile(
                            "image.jpg",
                            file,
                            result -> {
                                Log.i(TAG, "Successfully uploaded: " + result.getKey()) ;
                                imageKey = result.getKey();
                            },
                            storageFailure -> Log.e(TAG, "Upload failed", storageFailure)
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {

        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();

        return image;
    }

    private void pictureDownload(){
        Amplify.Storage.downloadFile(
                "ExampleKey",
                new File(getApplicationContext().getFilesDir() + "/download.txt"),
                result -> Log.i("MyAmplifyApp", "Successfully downloaded: " + result.getFile().getName()),
                error -> Log.e("MyAmplifyApp",  "Download Failure", error)
        );
    }
}
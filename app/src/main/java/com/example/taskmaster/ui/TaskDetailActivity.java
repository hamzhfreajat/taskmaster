package com.example.taskmaster.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;
import com.bumptech.glide.Glide;
import com.example.taskmaster.R;
import com.example.taskmaster.data.AppDatabase;
import com.example.taskmaster.data.TaskData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TaskDetailActivity extends AppCompatActivity {

    private static final String TAG = TaskDetailActivity.class.getSimpleName();
    private String title , desc , status;
    private ImageView imageView;
    private String imageKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_task_detail);

        TextView textTitle = findViewById(R.id.text_title);
        TextView textDesc = findViewById(R.id.text_description);
        TextView textStatus = findViewById(R.id.text_status);

        imageView = findViewById(R.id.img_task);

        Intent intent = getIntent();


        Handler handler = new Handler(Looper.getMainLooper() , msg -> {

            textTitle.setText(title);
            textDesc.setText(desc);
            textStatus.setText(status.toString());
            if ( imageKey != null){
                setImage(imageKey);
            }

            return true ;
        });

        Amplify.API.query(
                    ModelQuery.get(Task.class, intent.getStringExtra("id")),
                    response -> {

                        Log.i("MyAmplifyApp", (response.getData()).getTitle()) ;

                        title = response.getData().getTitle();
                        desc = response.getData().getBody();
                        status = response.getData().getStatus();
                        imageKey = response.getData().getImage();



                        Bundle bundle = new Bundle();


                        Message message = new Message();
                        message.setData(bundle);
                        handler.sendMessage(message);


                    },
                    error -> Log.e("MyAmplifyApp", error.toString(), error)
            );


//        TaskData taskData = AppDatabase.getInstance(this).taskDao().getTaskByID(Long.valueOf(intent.getStringExtra("id")));

    }

    private void setImage(String image) {
        if(image != null) {
            Amplify.Storage.downloadFile(
                    image,
                    new File(getApplicationContext().getFilesDir() + "/" + image + "download.jpg"),
                    result -> {
                        Log.i(TAG, "The root path is: " + getApplicationContext().getFilesDir());
                        Log.i(TAG, "Successfully downloaded: " + result.getFile().getName());
                        runOnUiThread(() -> {

                            Glide.with(this).load(result.getFile().getPath()).into(imageView);

                        });
                    },
                    error -> Log.e(TAG, "Download Failure", error)
            );
        }
    }
}
package com.example.taskmaster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
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
import com.example.taskmaster.ui.MapsActivity;
import com.example.taskmaster.ui.TaskDetailActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.vmadalin.easypermissions.EasyPermissions;

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

public class AddTaskActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    public static final int REQUEST_CODE = 123;
    private static final String TAG = AddTaskActivity.class.getName();
    private double latitude;
    private double longitude;
    private FusedLocationProviderClient mFusedLocationClient;


    private EditText taskTitle;
    private EditText taskDesc;

    private Button addLocation;

    private Spinner spinner;

    private List<Team> teamList = new ArrayList<>();
    private String imageKey = "" ;

    private Team team ;
    private Team selectedTeam;


    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude = mLastLocation.getLatitude() ;
            longitude =  mLastLocation.getLongitude();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        // Lab 39
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

//        if (type.startsWith("image/")) {
//            handleSendImage(intent); // Handle single image being sent
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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


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

        // Submit button listener
        addTask.setOnClickListener(v -> {

            taskTitle = findViewById(R.id.edit_task_title);
            taskDesc = findViewById(R.id.edit_task_desc);

            String spinnerSelected = spinner.getSelectedItem().toString();
            selectedTeam = teamList.get(0);

            for (Team team :
                    teamList) {
                if (team.getName() == spinnerSelected) {
                    selectedTeam = team ;
                }
            }



            getLastLocation();


// Map Lab




        });




//        addLocation = findViewById(R.id.fab_log_weather);
//
//        addLocation.setOnClickListener(view -> {
//            Intent navigateToMaps = new Intent(getApplicationContext(),
//                    MapsActivity.class);
//            startActivity(navigateToMaps);
//        });
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

                // Upload image to S3
                imageS3upload(currentUri);

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


    private void handleSendImage(Intent intent){
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            imageS3upload(imageUri);
        }
    }


    private void imageS3upload(Uri currentUri){
        Bitmap bitmap = null;
        String currentUriStr = String.valueOf(currentUri.getLastPathSegment())  + ".jpg";
        Log.i("CurrentURI" , currentUriStr);
        try {
            bitmap = getBitmapFromUri(currentUri);
            File file = new File(getApplicationContext().getFilesDir(), currentUriStr );
            OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.close();

            // upload to s3
            // uploads the file
            Amplify.Storage.uploadFile(
                    currentUriStr,
                    file,
                    result -> {
                        Log.i(TAG, "Successfully uploaded: " + result.getKey()) ;
                        imageKey = result.getKey();
                        Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                    },
                    storageFailure -> Log.e(TAG, "Upload failed", storageFailure)
            );

        } catch (IOException e) {
            e.printStackTrace();
        }

    }





    @Override
    public void onPermissionsGranted(int i, @NonNull List<String> list) {
        Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (hasLocationPermission()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    Log.i("location1" , location.toString());
                    if (location == null) {
                        Log.i("location2" , location.toString());
                        requestNewLocationData();
                    } else {
                        Log.i("location3" , String.valueOf(location.getLatitude()));
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        runOnUiThread(() -> {
                            Task item = Task.builder()
                                    .title(taskTitle.getText().toString())
                                    .body(taskDesc.getText().toString())
                                    .status(spinner.getSelectedItem().toString())
                                    .latitude(latitude)
                                    .longitude(longitude)
                                    .teamTasksId(selectedTeam.getId())
                                    .image(imageKey)
                                    .build();

                            Amplify.API.mutate(ModelMutation.create(item),
                                    successSaveToAPI -> Log.i(TAG, "Saved item: " + successSaveToAPI.getData().getTitle()),
                                    error -> Log.e(TAG, "Could not save to API" + error)
                            );

                            startActivity(new Intent(getApplicationContext() , MainActivity.class));
                        });

                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();

            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestLocationPermission();
            mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                Log.i(TAG, "" + location.getLongitude());
                Toast.makeText(getApplicationContext(), "NO LONGITUDE & LATITUDE =>" + location.getLongitude(), Toast.LENGTH_SHORT).show();
            });
        }

    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private boolean hasLocationPermission() {
        return EasyPermissions.hasPermissions(
                getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        );

    }

    private void requestLocationPermission() {
        EasyPermissions.requestPermissions(
                this,
                "this Application cannot work without Location Permission",
                REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION
        );
    }

    @Override
    public void onPermissionsDenied(int i, @NonNull List<String> list) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            Toast.makeText(this, "the application have not permission", Toast.LENGTH_SHORT).show();

        } else {
            requestLocationPermission();
        }
    }
}

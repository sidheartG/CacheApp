package com.example.cacheapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    // Define an enum to represent upload status
    enum UploadStatus {
        NOT_UPLOADED,
        UPLOADING,
        UPLOADED
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a string to store some sample data
        String data = "This is a java program to save cache in SharedPreferences";

        // Get an instance of the SharedPreferences object
        Context context = getApplicationContext();
        SharedPreferences preferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);

        // Get an editor object to modify the preferences
        SharedPreferences.Editor editor = preferences.edit();

        // Check network connectivity
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // Save data to local cache if network is not available
        if (!isConnected) {
            editor.putString("my_data", data);
            editor.apply();
        }

        // Upload saved data when the network is available
        if (isConnected) {
            // Get saved data from Shared Preferences
            String get_data = preferences.getString("my_data", "");
            if (!get_data.isEmpty()) {
                // Set the upload status to UPLOADING
                editor.putInt("upload_status", UploadStatus.UPLOADING.ordinal());
                editor.apply();

                // Upload data to the database
                boolean isUploaded = uploadDataToDatabase(get_data);
                // Use retry mechanism if upload fails
                int retryCount = 0;
                int maxRetryCount = 3;
                while (!isUploaded && retryCount < maxRetryCount) {
                    // Wait for 5 seconds before retrying
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Retry upload
                    isUploaded = uploadDataToDatabase(get_data);
                    retryCount++;
                }
                // Check if the data is UPLOADED or not
                if (isUploaded) {
                    // Set the upload status to UPLOADED and remove saved data
                    editor.putInt("upload_status", UploadStatus.UPLOADED.ordinal());
                    editor.remove("my_data");
                    editor.apply();
                }

            } else {
                // Set the upload status to NOT_UPLOADED
                editor.putInt("upload_status", UploadStatus.NOT_UPLOADED.ordinal());
                editor.apply();
            }
        }
    }

    // Method to upload data to the database
    private boolean uploadDataToDatabase(String data) {
        // implement logic to upload data to database
        return true;
    }
}

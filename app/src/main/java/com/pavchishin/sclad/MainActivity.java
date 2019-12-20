package com.pavchishin.sclad;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    static String PLACE_FOLDER = "Places";
    static String ONEDRIVE_FOLDER = "OneDrive";
    static final String TAG = "S C L A D - >>>";

    Button placeCalculate, partsCalculae, inventCalculate, exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNoActionBar(this);

        exit = findViewById(R.id.btn_exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });

        placeCalculate = findViewById(R.id.btn_place);
        placeCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFolder();
                Intent intent = new Intent(MainActivity.this, ManagerActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkFolder() {
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + PLACE_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
            Log.d(TAG, "Folder " + PLACE_FOLDER + " created!");
        } else {
            Log.d(TAG, "Folder " + PLACE_FOLDER + " exist!");
        }
    }
    public void setNoActionBar(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();

        if (view == null) {
            view = new View(activity);
        }
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}

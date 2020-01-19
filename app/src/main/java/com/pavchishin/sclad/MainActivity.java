package com.pavchishin.sclad;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import static com.pavchishin.sclad.DBHelper.DATABASE_PARTS;

public class MainActivity extends AppCompatActivity {

    static String PLACE_FOLDER = "Places";
    static String PARTS_FOLDER = "Parts";
    static String ONEDRIVE_FOLDER = "OneDrive";
    public static final String TAG = "S C L A D - >>>";
    public static final String OUTPUT_FOLDER = "Output";

    public Context context = this;
    String fileName;

    Button placeCalculate, partsCalculate, inventCalculate, exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNoActionBar();

        partsCalculate = findViewById(R.id.btn_parts);
        partsCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFile(PARTS_FOLDER))
                    new CalculateTask(context, fileName).execute();
            }
        });

        placeCalculate = findViewById(R.id.btn_place);
        placeCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUnCompleteScanning();
            }
        });
        exit = findViewById(R.id.btn_exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });
    }

    private void checkUnCompleteScanning() {
        if (new DBHelper(context).doesTableExist(context, DBHelper.TABLE_PLACES)){
            Log.d(TAG, "Table exist");
            String message = "Продовжити сканування";
            confirmDialog(message);
        } else {
            checkFolder();
            Intent intent = new Intent(MainActivity.this, ManagerActivity.class);
            startActivity(intent);
        }
    }

    public boolean checkFile(String folderName){
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String[] fileInFolder = folder.list();
        assert fileInFolder != null;
        if (fileInFolder.length > 0){
            fileName = fileInFolder[0];
            return true;
        } else {
            Toast.makeText(context, "Добавте файл в папку " + folderName, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void confirmDialog(String message) {
        final Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setCancelable(false);
        dialog.setTitle(message);
        dialog.setContentView(R.layout.round_corner);

        TextView title = dialog.findViewById(R.id.text_dialog);
        title.setText(message);

        ImageButton dialogOk = dialog.findViewById(R.id.btn_ok);
        dialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DisplayActivity.class));
                dialog.dismiss();
            }
        });

        ImageButton dialogCancel = dialog.findViewById(R.id.btn_cancel);
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDatabase(DATABASE_PARTS);
                startActivity(new Intent(MainActivity.this, ManagerActivity.class));
                dialog.dismiss();
            }
        });
        dialog.show();
        setNoActionBar();

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
    public void setNoActionBar() {
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

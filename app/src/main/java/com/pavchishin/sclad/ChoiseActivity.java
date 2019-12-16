package com.pavchishin.sclad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import static com.pavchishin.sclad.MainActivity.ONEDRIVE_FOLDER;
import static com.pavchishin.sclad.MainActivity.PLACE_FOLDER;
import static com.pavchishin.sclad.MainActivity.TAG;
import static com.pavchishin.sclad.ManagerActivity.ITEMS;

public class ChoiseActivity extends AppCompatActivity {

    private TextView loadTitle, titleNumberFiles;
    private ImageButton ok;
    private ImageButton back;
    private ListView mainList;
    List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choise);
        setNoActionBar(this);

        loadTitle = findViewById(R.id.txt_title_load);
        loadTitle.setVisibility(View.INVISIBLE);
        titleNumberFiles = findViewById(R.id.txt_number_files);

        mainList = findViewById(R.id.display_to_dock);
        back = findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoiseActivity.this, ManagerActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ok = findViewById(R.id.btn_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFileIsExist();
                try {
                    copySelectedFiles(itemList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new LoadTask(ChoiseActivity.this).execute();
            }
        });

        Intent intent = getIntent();
        itemList = (ArrayList<Item>) intent.getSerializableExtra(ITEMS);
        assert itemList != null;
        for (Item itm : itemList) {
            Log.d(TAG, itm.getName());
        }
        fillDisplay(itemList);
    }

    private void removeFileIsExist() {
        File destination = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + PLACE_FOLDER);
        Log.d(TAG, destination.listFiles().length + " Length");
        if (destination.listFiles().length > 0){
            for (File file : destination.listFiles()) {
                file.delete();
            }
        }
    }

    private void copySelectedFiles(List<Item> itemList) throws IOException {
        for (Item item : itemList) {
            File source = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + ONEDRIVE_FOLDER + File.separator + item.getName());
            File destination = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + PLACE_FOLDER + File.separator + item.getName());
            FileChannel sourceChanel = null;
            FileChannel destinationChanel = null;

            try {
                sourceChanel = new FileInputStream(source).getChannel();
                destinationChanel = new FileOutputStream(destination).getChannel();
                destinationChanel.transferFrom(sourceChanel, 0, sourceChanel.size());
            } finally {
                if (sourceChanel != null) {
                    sourceChanel.close();
                }
                if (destinationChanel != null) {
                    destinationChanel.close();
                }
            }
        }
    }

    private void fillDisplay(List<Item> itemList) {
        ItemAdapter adapter = new ItemAdapter(this, R.layout.item_layout, itemList);
        mainList.setAdapter(adapter);
        setTitle();
    }

    @SuppressLint("DefaultLocale")
    public void setTitle(){
        loadTitle.setVisibility(View.VISIBLE);
        int dockCount = itemList.size();
        if (dockCount == 1){
            titleNumberFiles.setText(String.format("%d   файл", dockCount));
        } else if (dockCount == 2 || dockCount == 3 || dockCount == 4){
            titleNumberFiles.setText(String.format("%d   файли", dockCount));
        } else
            titleNumberFiles.setText(String.format("%d   файлiв", dockCount));
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

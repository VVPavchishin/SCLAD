package com.pavchishin.sclad;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.pavchishin.sclad.MainActivity.ONEDRIVE_FOLDER;

public class ManagerActivity extends AppCompatActivity {

    public static final String ITEMS = "items";
    File destFile = new File(Environment.getExternalStorageDirectory()
            + File.separator + ONEDRIVE_FOLDER);
    ListView fileList;
    List<Item> selectedItems;
    ItemAdapter adapter;
    List<Item> itemList;
    TextView select;
    ImageButton next;
    ImageButton backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        setNoActionBar(this);

        fileList = findViewById(R.id.list_files);
        select = findViewById(R.id.txt_select);
        next = findViewById(R.id.okNext);

        backButton = findViewById(R.id.bk_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManagerActivity.this, MainActivity.class));
                finish();
            }
        });

        itemList = new ArrayList<>();
        selectedItems = new ArrayList<>();
        adapter = new ItemAdapter(this, R.layout.item_layout, itemList);
        fileList.setAdapter(adapter);

        File[] files = destFile.listFiles();
        assert files != null;
        if (files.length == 0){
            Toast.makeText(this, "Folder empty!", Toast.LENGTH_LONG).show();
        } else {
            for (File file : files) {
                if (file.getName().endsWith(".xlsx") && file.getName().length() == 33){
                    String dt = getDateFromName(file.getName());
                    itemList.add(new Item(R.drawable.file_icon, file.getName(), dt));
                }
            }
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerActivity.this, ChoiseActivity.class);
                intent.putExtra(ITEMS, (Serializable) selectedItems);
                startActivity(intent);
                finish();
            }
        });

        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item selected = (Item)parent.getItemAtPosition(position);

                if (selectedItems.contains(selected)){
                    selected.setFolderRecourse(R.drawable.file_icon);
                    adapter.notifyDataSetChanged();
                    selectedItems.remove(selected);
                } else {
                    selectedItems.add(selected);
                    selected.setFolderRecourse(R.drawable.ok_icon);
                    adapter.notifyDataSetChanged();
                }
                select.setText(setSelectedText(selectedItems.size()));
            }
        });

    }
    private String setSelectedText(int size) {
        String name = "";
        if (size > 0){
            name = "ВИБРАНО (" + size + ")";
        }
        return name;
    }

    private String getDateFromName(String name) {
        String nameDate;
            String year = name.substring(14, 18);
            String month = name.substring(18, 20);
            String day = name.substring(20, 22);
            nameDate = day + "." + month + "." + year;
        return nameDate;
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

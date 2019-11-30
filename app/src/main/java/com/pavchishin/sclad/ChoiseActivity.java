package com.pavchishin.sclad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static com.pavchishin.sclad.MainActivity.TAG;
import static com.pavchishin.sclad.MainActivity.PLACE_FOLDER;

public class ChoiseActivity extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 5;
    private TextView loadTitle, titleNumberFiles;
    private Button load;
    private ImageButton ok;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choise);
        setNoActionBar(this);

        loadTitle = findViewById(R.id.txt_title_load);
        loadTitle.setVisibility(View.INVISIBLE);
        titleNumberFiles = findViewById(R.id.txt_number_files);

        mainLayout = findViewById(R.id.display_to_dock);

        load = findViewById(R.id.btn_load);
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                }
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
                try {
                    startActivityForResult(intent, FILE_SELECT_CODE);
                } catch (ActivityNotFoundException e){}
            }
        });
    }
    @SuppressLint("DefaultLocale")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setNoActionBar(this);
        if (resultCode == RESULT_OK && requestCode == FILE_SELECT_CODE) {
            if (data.getData() != null) {
                String fileEName = getRealFilePath(data.getData());
                showOnDisplay(fileEName);
                copyFileFromUri(this, data.getData());
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    ClipData clipData = data.getClipData();
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        String fileMName = getRealFilePath(clipData.getItemAt(i).getUri());
                        showOnDisplay(fileMName);
                    }
                }
            }

        }
        int dockCount = mainLayout.getChildCount();
        if (dockCount == 1){
            titleNumberFiles.setText(String.format("%d   файл", dockCount));
        } else if (dockCount == 2 || dockCount == 3 || dockCount == 4){
            titleNumberFiles.setText(String.format("%d   файли", dockCount));
        } else
            titleNumberFiles.setText(String.format("%d   файлiв", dockCount));
    }

    private void showOnDisplay(String name) {
        loadTitle.setVisibility(View.VISIBLE);
        TextView view = new TextView(this);
        view.setText(name);
        view.setTextColor(Color.WHITE);
        mainLayout.addView(view);
    }

    public String getRealFilePath(Uri uri){
        String path = uri.getPath();
        String[] pathArray = path.split(":");
        String fileName = pathArray[pathArray.length - 1];
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName;
        Log.d(TAG, filePath);
        return fileName;
    }

    public boolean copyFileFromUri(Context context, Uri fileUri)
    {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try
        {
            ContentResolver content = context.getContentResolver();
            inputStream = content.openInputStream(fileUri);

            File root = Environment.getExternalStorageDirectory();
            if(root == null){
                Log.d(TAG, "Failed to get root");
            }

            // create a directory
            File saveDirectory = new File(Environment.getExternalStorageDirectory()+ File.separator + PLACE_FOLDER);

            outputStream = new FileOutputStream( saveDirectory + File.separator + "filename.xlsx"); // filename.png, .mp3, .mp4 ...
            if(outputStream != null){
                Log.d( TAG, "Output Stream Opened successfully");
            }

            byte[] buffer = new byte[1000];
            int bytesRead = 0;
            while ( ( bytesRead = inputStream.read( buffer, 0, buffer.length ) ) >= 0 ){
                outputStream.write( buffer, 0, buffer.length );
            }
            Log.d( TAG, "File copy successfully");
        } catch ( Exception e ){
            Log.e( TAG, "Exception occurred " + e.getMessage());
        } finally{

        }
        return true;
    }

    public void setNoActionBar(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();

        if (view == null) {
            view = new View(activity);
        }
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

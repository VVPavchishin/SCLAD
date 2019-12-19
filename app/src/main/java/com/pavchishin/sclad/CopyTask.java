package com.pavchishin.sclad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

import static com.pavchishin.sclad.MainActivity.ONEDRIVE_FOLDER;
import static com.pavchishin.sclad.MainActivity.PLACE_FOLDER;
import static com.pavchishin.sclad.MainActivity.TAG;

public class CopyTask extends AsyncTask<String, Void, Void> {
    private List<Item> itemList;
    @SuppressLint("StaticFieldLeak")
    private ProgressBar progressBar;
    @SuppressLint("StaticFieldLeak")
    private Activity activity;

    public CopyTask(List<Item> itemList, ProgressBar progressBar, ChoiseActivity choiseActivity) {
        this.itemList = itemList;
        this.activity = choiseActivity;
        this.progressBar = progressBar;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        File workDirPath = new File(Environment.getExternalStorageDirectory()
                + File.separator + PLACE_FOLDER);
        String[] inputFiles = workDirPath.list();
        assert inputFiles != null;
        new LoadTask((ChoiseActivity) activity, progressBar).execute(inputFiles);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setMax(itemList.size());
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            copySelectedFiles(itemList);
            Log.d(TAG, " Files copy success!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
}

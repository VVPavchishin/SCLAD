package com.pavchishin.sclad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


import static com.pavchishin.sclad.DBHelper.TABLE_PLACES;
import static com.pavchishin.sclad.MainActivity.PLACE_FOLDER;

public class LoadTask extends AsyncTask<Void, Void, Void> {

    private static String EMPTY = "";

    @SuppressLint("StaticFieldLeak")
    private Activity activity;
    private DBHelper helper;
    private SQLiteDatabase database;

    public LoadTask(ChoiseActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Intent intent = new Intent(activity, DisplayActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        helper = new DBHelper(activity);
        database = new DBHelper(activity).getWritableDatabase();
        helper.onCreatePlaceDB(database);

        File workDirPath = new File(Environment.getExternalStorageDirectory()
                + File.separator + PLACE_FOLDER);
        if (workDirPath.exists()) {
            String[] inputFiles = workDirPath.list();

            for (String fileName : inputFiles){
                    if (!fileName.endsWith(".xslx")){
                        try {
                            fillDataBase(workDirPath, fileName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        }
        return null;
    }
    private void fillDataBase(File workDirPath, String fileName) throws Exception {
        ContentValues cv = new ContentValues();
        InputStream stream = new FileInputStream(workDirPath.toString()
                + File.separator + fileName);
        XSSFWorkbook workbook = new XSSFWorkbook(stream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow row;

        for (int rowIndex = 19; rowIndex < sheet.getLastRowNum(); rowIndex++) {
            row = sheet.getRow(rowIndex);
            if (row != null) {

                Cell cellArt = row.getCell(1);
                String cellArtikul = cellArt.getStringCellValue();
                if (cellArtikul.equals(EMPTY) || cellArtikul.length() == 0){
                    break;
                }
                cv.put(DBHelper.PLACE_ARTIKUL_PART, cellArtikul);

                String numberDocument = String.valueOf(workbook.
                        getSheetAt(0).getRow(15).getCell(4));
                cv.put(DBHelper.PLACE_DOCNAME, numberDocument);

                Cell cellNam = row.getCell(2);
                String cellName = cellNam.getStringCellValue();
                cv.put(DBHelper.PLACE_NAME_PART, cellName);

                Cell cellQan = row.getCell(4);
                double cellQuantity = cellQan.getNumericCellValue();
                cv.put(DBHelper.PLACE_QUANTITY_PART, cellQuantity);

                Cell cellPrc = row.getCell(5);
                double cellPrice = cellPrc.getNumericCellValue();
                cv.put(DBHelper.PLACE_PRICE_PART, cellPrice);

                Cell cellPls = row.getCell(7);
                String cellPlace = cellPls.getStringCellValue();
                cv.put(DBHelper.PLACE_PLACE_NUMBER, cellPlace);

                cv.put(DBHelper.PLACE_STATUS, "UNSCANN");

                database.insert(TABLE_PLACES, null, cv);
            }

        }
    }
}

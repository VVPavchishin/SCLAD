package com.pavchishin.sclad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static com.pavchishin.sclad.MainActivity.TAG;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_PARTS = "spare_parts";

    static final String TABLE_PARTS = "parts";

    private static final String PART_ID = "_id";
    static final String PART_BARCODE = "barcode";
    static final String PART_ARTIKUL = "artikul";
    static final String PART_NAME = "name";
    static final String PART_PLACE = "place";
    static final String PART_QUANTITY_DOC = "quantity_doc";
    static final String PART_QUANTITY_REAL = "quantity_real";

    static final String TABLE_PLACES = "places";

    static final String PLACE_DOCNAME = "name_dok";
    static final String PLACE_ARTIKUL_PART = "artikul_part";
    static final String PLACE_NAME_PART = "name_part";
    static final String PLACE_QUANTITY_PART = "quantity_part";
    static final String PLACE_PRICE_PART = "price_part";
    static final String PLACE_PLACE_NUMBER = "place_number";
    static final String PLACE_STATUS = "status";

    static final String PLACE_STATUS_UNSCAN = "UNSCANN";
    static final String PLACE_STATUS_SCAN = "SCANN";

    public DBHelper(Context context) {
        super(context, DATABASE_PARTS, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "<--- Create Parts database --->");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTS);
        db.execSQL("create table " + TABLE_PARTS + " ("
                + PART_ID + " INTEGER PRIMARY KEY,"
                + PART_BARCODE + " TEXT,"
                + PART_ARTIKUL + " TEXT,"
                + PART_NAME + " TEXT,"
                + PART_PLACE + " TEXT,"
                + PART_QUANTITY_DOC + " INTEGER,"
                + PART_QUANTITY_REAL + " INTEGER" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public void onCreatePlaceDB(SQLiteDatabase db) {
        Log.d(TAG, "<--- Create Place database --->");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES);
        db.execSQL("create TABLE " + TABLE_PLACES + " ("
                + PLACE_DOCNAME + " TEXT, "
                + PLACE_ARTIKUL_PART + " TEXT, "
                + PLACE_NAME_PART + " TEXT, "
                + PLACE_QUANTITY_PART + " INTEGER, "
                + PLACE_PRICE_PART + " REAL, "
                + PLACE_PLACE_NUMBER + " TEXT, "
                + PLACE_STATUS + " TEXT" + ");");
    }

    public void onDelete(Context context, String tableName){
        Log.d(TAG, "<--- Delete database --->");
        new DBHelper(context).getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + tableName);
    }

    public ArrayList showData(Context context, String table){
        Log.d(TAG, "<--- Show database --->");
        ArrayList list = new ArrayList();
        Cursor c = new DBHelper(context).getWritableDatabase()
                .rawQuery("SELECT DISTINCT " + DBHelper.PLACE_PLACE_NUMBER
                + " FROM " + table + " WHERE " + DBHelper.PLACE_STATUS + " =?", new String[]{PLACE_STATUS_UNSCAN});
        c.moveToFirst();
        do {
            String number = "";
            try {
                number = c.getString(c.getColumnIndex(DBHelper.PLACE_PLACE_NUMBER));
            } catch (CursorIndexOutOfBoundsException e){}
            if (number.length() != 0 || !number.equals(""))
                list.add(number);
        } while(c.moveToNext());
        c.close();

        return list;
    }

    public void setScannedDB(Context context, String placeNumber){
        Log.d(TAG, "<--- Set place scanned " + placeNumber + " --->");
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.PLACE_STATUS, PLACE_STATUS_SCAN);
        new DBHelper(context).getWritableDatabase().
                update(DBHelper.TABLE_PLACES, cv, DBHelper.PLACE_PLACE_NUMBER +" =?", new String[]{placeNumber});
    }

    public boolean doesTableExist(Context context, String tableName) {
        Log.d(TAG, "<<<--- Check " + tableName + " existing.... --->>>");
        SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master " +
                "WHERE tbl_name = '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                Log.d(TAG, "<<<--- Database " + tableName + " exist --->>>");
                return true;
            }
            cursor.close();
        }
        Log.d(TAG, "<<<--- Database " + tableName + " no exist --->>>");
        return false;
    }

    public int setDockNumbers(Context context, String table){
        Log.d(TAG, "<--- Get number document --->");
        Cursor c = new DBHelper(context).getWritableDatabase()
                .rawQuery("SELECT DISTINCT " + PLACE_DOCNAME
                        + " FROM " + table, null);
        int numD = c.getCount();
        Log.d(TAG, "Число накладных " + numD);
        c.close();
        return numD;
    }

    public int setPlaceNumbers (Context context, String table){
        Log.d(TAG, "<--- Get place number --->");
        int numP = 0;
        Cursor c = new DBHelper(context).getWritableDatabase()
                .rawQuery("SELECT DISTINCT " + PLACE_PLACE_NUMBER
                        + " FROM " + table, null);
        c.moveToFirst();
        do {
            String number = "";
            try {
                number = c.getString(c.getColumnIndex(DBHelper.PLACE_PLACE_NUMBER));
            }  catch (CursorIndexOutOfBoundsException e){}

            if (!number.equals(""))
                numP++;
        } while(c.moveToNext());
        c.close();

        return numP;
    }
    public int setOnScanNumbers (Context context, String table){
        Log.d(TAG, "<--- Get scanned number --->");
        int count;
        Cursor c = new DBHelper(context).getWritableDatabase()
                .rawQuery("SELECT DISTINCT " + DBHelper.PLACE_PLACE_NUMBER
                        + " FROM " + table + " WHERE " + DBHelper.PLACE_STATUS + "=?", new String[]{PLACE_STATUS_SCAN});
        count = c.getCount();
        c.close();

        return count;
    }
    public int setUnScanNumbers (Context context, String table){
        Log.d(TAG, "<--- Get Unscanned number --->");
        int count = 0;
        Cursor c = new DBHelper(context).getWritableDatabase()
                .rawQuery("SELECT DISTINCT " + DBHelper.PLACE_PLACE_NUMBER
                        + " FROM " + table + " WHERE " + DBHelper.PLACE_STATUS + "=?", new String[]{PLACE_STATUS_UNSCAN});
        if (c.moveToFirst()) {
            do {
                String number = c.getString(c.getColumnIndex(DBHelper.PLACE_PLACE_NUMBER));
                    if (!number.equals("")) {
                        count++;
                    }
            } while (c.moveToNext());
        }
        c.close();
        return count;
    }
    public ArrayList<Part> setBoxParts(Context context, String boxNumber){
        Log.d(TAG, "<--- Get parts in box  --->");
        ArrayList<Part> partList = new ArrayList<>();

        Cursor c = new DBHelper(context).getWritableDatabase()
                .rawQuery("SELECT DISTINCT " + DBHelper.PLACE_ARTIKUL_PART + ","
                + DBHelper.PLACE_NAME_PART + "," + DBHelper.PLACE_QUANTITY_PART + ","
                + DBHelper.PLACE_PRICE_PART + " FROM " +  DBHelper.TABLE_PLACES
                + " WHERE " + DBHelper.PLACE_PLACE_NUMBER + " = ?", new String[]{boxNumber});

        c.moveToFirst();
        do {
            String art = c.getString(c.getColumnIndex(DBHelper.PLACE_ARTIKUL_PART));
            String name = c.getString(c.getColumnIndex(DBHelper.PLACE_NAME_PART));
            int quantity = c.getInt(c.getColumnIndex(DBHelper.PLACE_QUANTITY_PART));
            float price = c.getFloat(c.getColumnIndex(DBHelper.PLACE_PRICE_PART));

            partList.add(new Part(art, name, quantity, price));
            Log.d(TAG, art + " >>> " + name + " >>> " + quantity + " >>> " + price);
        } while (c.moveToNext());
        c.close();

        return partList;
    }

    public PartItem getPartValues(Context context, String name){
        PartItem part = null;
        Cursor cursor = new DBHelper(context).getWritableDatabase().
                rawQuery("SELECT * FROM " + DBHelper.TABLE_PARTS
                        + " WHERE " + DBHelper.PART_BARCODE + " LIKE '" + name + "'", null);
        if (cursor.moveToFirst()){
            String artikulValue = cursor.getString(cursor.getColumnIndex(DBHelper.PART_ARTIKUL));
            String nameValue = cursor.getString(cursor.getColumnIndex(DBHelper.PART_NAME));
            String locationValue = cursor.getString(cursor.getColumnIndex(DBHelper.PART_PLACE));
            int quantityDocValue = cursor.getInt(cursor.getColumnIndex(DBHelper.PART_QUANTITY_DOC));
            int quantityRealValue = cursor.getInt(cursor.getColumnIndex(DBHelper.PART_QUANTITY_REAL));
            part = new PartItem(artikulValue, nameValue, locationValue, quantityDocValue, quantityRealValue);
            Log.d(TAG, part.getNamePart() + " >>> " + part.getArtPart() + " >>> "
                    + part.getDockQuantityPart() + " >>> " + part.getRealQuantityPart() + " >>> " + part.getLocationPart());
        }
        cursor.close();
        return part;
    }

    public void updateQuantity(Context context, String artikul , int newValue){
        new DBHelper(context).getWritableDatabase().execSQL("UPDATE " + DBHelper.TABLE_PARTS + " SET " +
                DBHelper.PART_QUANTITY_REAL + " = " + DBHelper.PART_QUANTITY_REAL + "+" + newValue + " WHERE "
                + DBHelper.PART_ARTIKUL + "=?", new String[]{artikul});
    }
}

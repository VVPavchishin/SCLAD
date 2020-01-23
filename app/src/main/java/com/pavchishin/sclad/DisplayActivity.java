package com.pavchishin.sclad;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import static com.pavchishin.sclad.DBHelper.DATABASE_PARTS;
import static com.pavchishin.sclad.MainActivity.TAG;

public class DisplayActivity extends AppCompatActivity {

    TextView qDock, qPlace, qScanned, qDifference;
    EditText scanField;
    ImageView status;

    ImageButton buttonBack;
    Button completeScan;

    Context context = this;

    ListView boxListView;
    ListView partsView;

    List<String> boxList;

    PartAdapter partAdapter;
    Sound sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        setNoActionBar(this);

        sound = new Sound(context);

        qDock = findViewById(R.id.title_quant_number);
        qPlace = findViewById(R.id.title_place_number);
        qScanned = findViewById(R.id.title_scan_number);
        qDifference = findViewById(R.id.title_difference_number);

        scanField = findViewById(R.id.scanner);

        status = findViewById(R.id.im_status);

        boxListView = findViewById(R.id.box_list);
        partsView = findViewById(R.id.list_parts);


        fillLeftDisplay();
        fillBoxDisplay();

        buttonBack = findViewById(R.id.button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DisplayActivity.this, MainActivity.class));
                finish();
            }
        });

        completeScan = findViewById(R.id.scan_complete);
        completeScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoActionBar(DisplayActivity.this);
                String msg = "Завершити сканування";
                confirmComplete(msg);
            }
        });

        scanField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoActionBar(DisplayActivity.this);
                String barcode = scanField.getText().toString();
                checkBarcode(barcode);
            }
        });
    }

    private void fillLeftDisplay() {
      qDock.setText(String.valueOf(new DBHelper(context)
              .setDockNumbers(context, DBHelper.TABLE_PLACES)));
      qPlace.setText(String.valueOf(new DBHelper(context)
              .setPlaceNumbers(context, DBHelper.TABLE_PLACES)));
      qScanned.setText(String.valueOf(new DBHelper(context)
              .setOnScanNumbers(context, DBHelper.TABLE_PLACES)));
      qDifference.setText(String.valueOf(new DBHelper(context)
              .setUnScanNumbers(context, DBHelper.TABLE_PLACES)));


    }

    private void fillBoxDisplay() {
        boxList = new DBHelper(context).showData(context, DBHelper.TABLE_PLACES);
        ArrayAdapter<String> boxAdapter = new ArrayAdapter<>(context, R.layout.box_layout, boxList);
        boxListView.setAdapter(boxAdapter);
        boxListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                String selectedBox = (String) parent.getItemAtPosition(position);
                ArrayList<Part> parts = new DBHelper(context).setBoxParts(context, selectedBox);
                PartAdapter partAdapter = new PartAdapter(context, R.layout.part_layout, parts);
                partsView.setAdapter(partAdapter);

            }
        });
    }

    private void checkBarcode(String barcode) {
        String code = "";
        if (barcode.length() < 7){
            wrongBarcode();
        } else {
            code = barcode.trim().substring(0,8);
        }
        Log.d(TAG, code);
        if (boxList.contains(code)){
            status.setBackgroundResource(R.drawable.ok_image);
            sound.okSound();
            scanField.setText("");
            setScanned(code);
            fillBoxDisplay();
            fillLeftDisplay();
            fillRightDisplay(code);
            if (new DBHelper(context).setUnScanNumbers(context, DBHelper.TABLE_PLACES) == 0){
                status.setBackgroundResource(R.drawable.mission_complete);
                partAdapter.clear();
                partAdapter.notifyDataSetChanged();
            }
        } else {
            wrongBarcode();
        }
    }

    private void wrongBarcode() {
        status.setBackgroundResource(R.drawable.not_okey);
        sound.notOkSound();
        scanField.setText("");
        if (partsView.getChildCount() > 0) {
            partsView.setAdapter(null);
        }
    }

    private void fillRightDisplay(String code) {
        ArrayList<Part> parts = new DBHelper(context).setBoxParts(context, code);
        partAdapter = new PartAdapter(context, R.layout.part_layout, parts);
        partsView.setAdapter(partAdapter);
    }

    private void confirmComplete(String msg) {
        final Dialog dialog = new Dialog(DisplayActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.round_corner);

        TextView title = dialog.findViewById(R.id.text_dialog);
        title.setText(msg);

        ImageButton dialogOk = dialog.findViewById(R.id.btn_ok);
        dialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoActionBar(DisplayActivity.this);
                deleteDatabase(DATABASE_PARTS);
                startActivity(new Intent(DisplayActivity.this, MainActivity.class));
                dialog.dismiss();
            }
        });

        ImageButton dialogCancel = dialog.findViewById(R.id.btn_cancel);
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoActionBar(DisplayActivity.this);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void setScanned(String num) {
        new DBHelper(context).setScannedDB(context, num);
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

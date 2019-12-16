package com.pavchishin.sclad;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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

    List<String> numbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        setNoActionBar(this);

        qDock = findViewById(R.id.title_quant_number);
        qPlace = findViewById(R.id.title_place_number);
        qScanned = findViewById(R.id.title_scan_number);
        qDifference = findViewById(R.id.title_difference_number);

        scanField = findViewById(R.id.scanner);

        status = findViewById(R.id.im_status);

        numbers = new ArrayList<>();
        boxListView = findViewById(R.id.box_list);
        partsView = findViewById(R.id.list_parts);
        fillLeftDisplay();
        fillBoxDisplay();

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
        List<String> boxList = new DBHelper(context).showData(context, DBHelper.TABLE_PLACES);
        ArrayAdapter<String> boxAdapter = new ArrayAdapter<>(context, R.layout.box_layout, boxList);
        boxListView.setAdapter(boxAdapter);
        boxListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedBox = (String) parent.getItemAtPosition(position);
                ArrayList<Part> parts = new DBHelper(context).setBoxParts(context, selectedBox);
                PartAdapter partAdapter = new PartAdapter(context, R.layout.part_layout, parts);
                partsView.setAdapter(partAdapter);

            }
        });
    }

    private void checkBarcode(String barcode) {
        for (String num : numbers) {
            if (barcode.contains(num)){
                status.setBackgroundResource(R.drawable.ok_im);
                scanField.setText("");
                numbers.remove(num);
//                showParts(num);
//                centerLayout.removeAllViews();
//                setScanned(num);
//                fillLeftDisplay();
//                fillCentralDisplay();
                break;
            } else {
                status.setBackgroundResource(R.drawable.not_ok_im);
                scanField.setText("");
            }
        }
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

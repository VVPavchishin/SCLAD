package com.pavchishin.sclad;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;

import static com.pavchishin.sclad.CalculateTask.DATE;
import static com.pavchishin.sclad.CalculateTask.NUMBER;
import static com.pavchishin.sclad.MainActivity.OUTPUT_FOLDER;
import static com.pavchishin.sclad.MainActivity.TAG;

public class CalculateActivity extends AppCompatActivity {

    private static final String FILE_INPUT = "OUTPUT.txt";
    private static final String EMPTY = "";

    ImageButton back;
    TextView numberView;
    TextView dataView;
    TextView namePart,
            artikulPart, locationPart, quantityPart,
            quantityDoc, quantityReal, difference,
            titleDoc, titleReal, titleDifference;
    CheckBox plusOne, changeLocation;

    Button upload;

    EditText scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);
        setNoActionBar(this);

        namePart = findViewById(R.id.txt_part_name);
        artikulPart = findViewById(R.id.txt_artikul);
        locationPart = findViewById(R.id.txt_location);
        quantityPart = findViewById(R.id.txt_quantity);

        quantityDoc = findViewById(R.id.txt_doc_quantity);
        quantityReal = findViewById(R.id.txt_real_quantity);
        difference = findViewById(R.id.txt_difference);

        /* Количественные названия полей для ввода */
        titleDoc = findViewById(R.id.doc_quantity);
        titleDoc.setVisibility(View.INVISIBLE);
        titleReal = findViewById(R.id.real_quantity);
        titleReal.setVisibility(View.INVISIBLE);
        titleDifference = findViewById(R.id.plus_mines);
        titleDifference.setVisibility(View.INVISIBLE);

        plusOne = findViewById(R.id.box_plus_one);
        plusOne.setVisibility(View.INVISIBLE);
        plusOne.setChecked(true);
        changeLocation = findViewById(R.id.box_location);
        changeLocation.setVisibility(View.INVISIBLE);

        numberView = findViewById(R.id.txt_number_doc);
        dataView = findViewById(R.id.txt_date_doc);
        setNumberAndDate();

        upload = findViewById(R.id.btn_upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDrive();
            }
        });

        scanner = findViewById(R.id.edt_barcode);
        scanner.setHintTextColor(Color.GREEN);
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoActionBar(CalculateActivity.this);
                String scanText = scanner.getText().toString();
                if(checkDatabase(scanText)){
                    plusOne.setVisibility(View.VISIBLE);
                    changeLocation.setVisibility(View.VISIBLE);
                    titleDoc.setVisibility(View.VISIBLE);
                    titleReal.setVisibility(View.VISIBLE);
                    titleDifference.setVisibility(View.VISIBLE);
                    namePart.setTextColor(Color.WHITE);
                    scanner.setText(EMPTY);
                    scanner.setHint(EMPTY);
                    String pName = artikulPart.getText().toString();
                    setQuantity(pName);
                    setLocation(pName);
                } else {
                    namePart.setTextColor(Color.RED);
                    namePart.setText("Данна запчастина вiдсутня в списку.");
                    setEmptyFields();
                }

            }
        });


        back = findViewById(R.id.b_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalculateActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean checkDatabase(String scanText) {
        return true;
    }

    private void setQuantity(final String pName) {
        plusOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setNoActionBar(CalculateActivity.this);
                if (!isChecked) {
                    quantityPart.setBackgroundColor(Color.DKGRAY);
                    quantityPart.setEnabled(true);
                    quantityPart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //showQuantityDialog(pName);
                        }
                    });
                } else {
                    quantityPart.setBackgroundColor(Color.BLACK);
                    quantityPart.setEnabled(false);
                }
            }
        });
    }

    private void setLocation(final String pName) {
        changeLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setNoActionBar(CalculateActivity.this);
                if (isChecked){
                    locationPart.setBackgroundColor(Color.DKGRAY);
                    locationPart.setEnabled(true);
                    locationPart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setNoActionBar(CalculateActivity.this);
                            //showLocationDialog(pName);
                        }
                    });
                } else {
                    locationPart.setBackgroundColor(Color.BLACK);
                    locationPart.setEnabled(false);
                }
            }
        });
    }

    private void setEmptyFields() {
        scanner.setText(EMPTY);
        scanner.setHint(EMPTY);
        artikulPart.setText(EMPTY);
        locationPart.setText(EMPTY);
        quantityDoc.setText(EMPTY);
        quantityReal.setText(EMPTY);
        difference.setText(EMPTY);
        quantityPart.setText(EMPTY);
        plusOne.setVisibility(View.INVISIBLE);
        changeLocation.setVisibility(View.INVISIBLE);
        titleDoc.setVisibility(View.INVISIBLE);
        titleReal.setVisibility(View.INVISIBLE);
        titleDifference.setVisibility(View.INVISIBLE);
    }

    private void saveToDrive() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d(TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + OUTPUT_FOLDER);
        File sdFile = new File(folder, FILE_INPUT);
        if (sdFile.exists()){
            sdFile.delete();
            Log.d(TAG, sdFile.getName() + " удален!");
        }
    }

    private void setNumberAndDate() {
        Intent intent = getIntent();
        String number = intent.getStringExtra(NUMBER);
        String date = intent.getStringExtra(DATE);
        if (number != null) {
            numberView.setTextColor(Color.GREEN);
            numberView.setText(number.substring(5));
        }
        dataView.setText(date);
        dataView.setTextColor(Color.GREEN);

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

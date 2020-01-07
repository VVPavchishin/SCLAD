package com.pavchishin.sclad;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.io.File;
import java.util.Objects;

import static com.pavchishin.sclad.CalculateTask.DATE;
import static com.pavchishin.sclad.CalculateTask.NUMBER;
import static com.pavchishin.sclad.MainActivity.OUTPUT_FOLDER;
import static com.pavchishin.sclad.MainActivity.TAG;

public class CalculateActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {

    private static final String FILE_INPUT = "OUTPUT.txt";
    private static final String EMPTY = "";

    Context context = this;

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
        setNoActionBar();

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
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoActionBar();
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
        boolean flag;
        String subScan = scanText.replace(" ", "");
        String scanValue;
        int scanLength = subScan.length();
        if (scanLength == 13){
            scanValue = subScan.substring(0, 12);
        } else if (scanLength == 11){
            scanValue = subScan.substring(0, 10);
        } else {
            scanValue = subScan;
        }
        PartItem partItem = new DBHelper(this).getPartValues(this, scanValue);
        if (partItem != null){
            artikulPart.setText(partItem.getArtPart());
            namePart.setText(partItem.getNamePart());
            locationPart.setText(partItem.getLocationPart());
            quantityDoc.setText(String.valueOf(partItem.getDockQuantityPart()));
            quantityReal.setText(String.valueOf(partItem.getRealQuantityPart()));
            quantityPart.setText(String.valueOf(partItem.getRealQuantityPart()));
            difference.setText(String.valueOf(partItem.getDockQuantityPart() - partItem.getRealQuantityPart()));
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    private void setQuantity(final String pName) {
        plusOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setNoActionBar();
                if (!isChecked) {
                    quantityPart.setBackgroundColor(Color.DKGRAY);
                    quantityPart.setEnabled(true);
                    quantityPart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showQuantityDialog(pName);
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
                setNoActionBar();
                if (isChecked){
                    locationPart.setBackgroundColor(Color.DKGRAY);
                    locationPart.setEnabled(true);
                    locationPart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setNoActionBar();
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

    private void showQuantityDialog(final String pName) {
        setNoActionBar();
        final Dialog dialog = new Dialog(CalculateActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_quantity);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button add = dialog.findViewById(R.id.btn_add);
        Button cancel = dialog.findViewById(R.id.btn_cancel);
        final NumberPicker np = dialog.findViewById(R.id.numberPicker);
        np.setMaxValue(100);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoActionBar();
                int docVal = Integer.parseInt(String.valueOf(quantityDoc.getText()));
                int oldValue = Integer.parseInt(String.valueOf(quantityPart.getText()));
                int addQuantity = np.getValue();
                quantityPart.setText(String.valueOf(oldValue + addQuantity));
                updateQuantity(pName, addQuantity);
                quantityPart.setBackgroundColor(Color.parseColor("#161516"));
                quantityReal.setText(String.valueOf(oldValue + addQuantity));
                difference.setText(String.valueOf(docVal - (oldValue + addQuantity)));
                plusOne.setChecked(true);
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plusOne.setChecked(true);
                setNoActionBar();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void updateQuantity(String pName, int addQuantity) {
        new DBHelper(context).updateQuantity(context, pName, addQuantity);
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

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {}
}

package com.example.expensejournal;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    DatabaseHelper mydb;
    Spinner dropdown;
    EditText editAmount;
    EditText editNote;
    EditText editDate;
    TextView showamt;
    Button btnAddData;
    Button btnView;
    Button btnExport;

    private static final String[] items = new String[] {"Food","Hospital","Grocery","Electricity","Donation","Transportation","Gift","Others"};
    private static int totalamt=0;
    private int amt; //current amount added
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mydb = new DatabaseHelper(this);
        editAmount= (EditText)findViewById(R.id.edit_text_amount);
        btnAddData= (Button) findViewById(R.id.btn_add);
        btnView= (Button) findViewById(R.id.btn_view);
        btnExport= (Button) findViewById(R.id.btn_export);
        showamt= (TextView) findViewById(R.id.total_value_text);
        dropdown= (Spinner)findViewById(R.id.spinner_category);
        editNote= (EditText)findViewById(R.id.editTextNote);
        editDate= (EditText)findViewById(R.id.editTextDate);

        ArrayAdapter<String> adapter = new ArrayAdapter<String> (MainActivity.this, android.R.layout.simple_spinner_item,items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

        Calendar calendar = Calendar.getInstance(); // getting the current date
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        AddData();

        editDate.setOnClickListener(new View.OnClickListener() { //date dialog show
        @Override
        public void onClick(View v) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month = month+1;
                    date = dayOfMonth+"/"+month+"/"+year;
                    editDate.setText(date);
                }
            } , year , month , day);
            datePickerDialog.show();
             }
            } );
        //view data

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewExpense.class);
                startActivity(intent);
            }
        });

    }

  @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
              //  i=position;
      //String text1 = dropdown.getSelectedItem().toString();

  }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    public void AddData() {
        btnAddData.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                     boolean isInserted =   mydb.insertData(date,Integer.parseInt(editAmount.getText().toString()),
                             dropdown.getSelectedItem().toString() , editNote.getText().toString() );

                     if (isInserted == true) {
                         Toast.makeText(MainActivity.this, "Data Inserted", Toast.LENGTH_LONG).show();
                         amt= Integer.parseInt(editAmount.getText().toString());
                         totalamt+=amt;
                         if(showamt!= null)
                             showamt.setText(Integer.toString(totalamt));
                         editAmount.setText("");
                         editNote.setText("");
                         editDate.setText("");
                     }
                     else
                         Toast.makeText(MainActivity.this , "Data not Inserted", Toast.LENGTH_LONG).show();




                    }
                }
        );
    }



}




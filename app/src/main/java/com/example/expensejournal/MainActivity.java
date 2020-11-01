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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ajts.androidmads.library.SQLiteToExcel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity  {

    DatabaseHelper mydb;
    Spinner dropdown;
    EditText editAmount;
    EditText editNote;
    EditText editDate;
    TextView showamt;
    Button btnAddData;
    Button btnView;
    Button btnExport;
    Button btnSignout;
    SQLiteToExcel sqLiteToExcel;
    GoogleSignInClient mGoogleSignInClient;

    private static final String[] items = new String[] {"Food","Hospital","Grocery","Electricity","Donation","Transportation","Gift","Others"};
    private static int todayamt=0; //display today
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
        btnSignout= (Button) findViewById(R.id.btn_signout);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) //to signout logged in client
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso); //to signout logged in client

        ArrayAdapter<String> adapter = new ArrayAdapter<String> (MainActivity.this, android.R.layout.simple_spinner_item,items); //adapter for dropdown category
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
       // dropdown.setOnItemSelectedListener(this);

        todayamt = mydb.getTodayAmount();
        showamt.setText(Integer.toString(todayamt));

        date= getCurrentDate();
        editDate.setText(date);

        showDatePickerOnClick();
        AddData(); //Add data to DB on Click
        //Go to View Activity on Click
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ViewExpense.class);
                startActivity(intent);
            }
        });
       //Export SQLite table to Excel(XLS) On Click
        sqLiteToExcel = new SQLiteToExcel(this, "expense.db");
        export();
        //Invoke Sign out on click
        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    // ...
                    case R.id.btn_signout:
                        signOut();
                        break;
                    // ...
                }
            }
        });
    }


 ///////////////////////METHODS/////////////////////////////

    public void showDatePickerOnClick() {  //when editText- Date is clicked
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month+1;
                        date = calendarToDate(dayOfMonth , month , year);
                        editDate.setText(date);
                    }
                } , Calendar.YEAR , Calendar.MONTH+1 , Calendar.DAY_OF_MONTH);
                datePickerDialog.show();
            }
        } );
    }

    public String getCurrentDate() //returns date in YYYY-MM-DD
    {
        Calendar calendar = Calendar.getInstance(); // getting the current date
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH) + 1;
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        String date, dayString, monthString;
        dayString = (day < 10 ) ? "0"+day : ""+ day;
        monthString= (month < 10) ? "0"+month : ""+month;
        date = year+"-"+monthString+"-"+dayString;
        return date;
    }

    public String calendarToDate ( int day , int month, int year) //returns date in YYYY-MM-DD
    {
        String date, dayString, monthString;
        dayString = (day < 10 ) ? "0"+day : ""+ day;
        monthString= (month < 10) ? "0"+month : ""+month;
        date = year+"-"+monthString+"-"+dayString;
        return date;
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "Signed Out Successfully", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    public void AddData()
    {
        btnAddData.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isInserted =   mydb.insertData( date,               //date
                                Integer.parseInt(editAmount.getText().toString()),  //amount
                                dropdown.getSelectedItem().toString() ,             //category
                                editNote.getText().toString() );                    //note

                        if (isInserted == true) {
                            Toast.makeText(MainActivity.this, "Data Inserted", Toast.LENGTH_LONG).show();
                            editAmount.setText("");
                            editNote.setText("");
                            editDate.setText(getCurrentDate());
                        }
                        else
                            Toast.makeText(MainActivity.this , "Data not Inserted", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    public void export()
    {
        btnExport.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<String> columnsToExclude = new ArrayList<String>();
                        columnsToExclude.add("ID");
                        sqLiteToExcel.setExcludeColumns(columnsToExclude);
                        String dateTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                        sqLiteToExcel.exportSingleTable("expense_table", "expense report " + dateTime +".xls", new SQLiteToExcel.ExportListener() {
                            @Override
                            public void onStart() {

                            }
                            @Override
                            public void onCompleted(String filePath) {
                                Toast.makeText(MainActivity.this , "Data Successfully Exported", Toast.LENGTH_LONG).show();

                            }
                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(MainActivity.this , "Error: "+ e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
             );

    }



}




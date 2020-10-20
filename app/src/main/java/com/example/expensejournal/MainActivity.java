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
    Button btnSignout;
    SQLiteToExcel sqLiteToExcel;
    GoogleSignInClient mGoogleSignInClient;

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
        btnSignout= (Button) findViewById(R.id.btn_signout);

        ArrayAdapter<String> adapter = new ArrayAdapter<String> (MainActivity.this, android.R.layout.simple_spinner_item,items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

        totalamt = mydb.getTodayAmount();
        showamt.setText(Integer.toString(totalamt));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Calendar calendar = Calendar.getInstance(); // getting the current date
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH) + 1;
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        date= day+"/"+month+"/"+year;
        editDate.setText(date);

        AddData();

        //Date dialog show.

        editDate.setOnClickListener(new View.OnClickListener() {
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


        //View Activity

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ViewExpense.class);
                startActivity(intent);
            }
        });


       //EXPORT SQLITE TO XLS

        sqLiteToExcel = new SQLiteToExcel(this, "expense.db");
        export();

        //sign out

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

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      Toast.makeText(MainActivity.this, "Signed Out Successfully", Toast.LENGTH_LONG).show();
                      finish();
                       /* Intent intent = new Intent(MainActivity.this , SigninActivity.class);
                        startActivity(intent);*/
                    }
                });

    }

    public void export() {
        btnExport.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<String> columnsToExclude = new ArrayList<String>();
                        columnsToExclude.add("ID");
                        sqLiteToExcel.setExcludeColumns(columnsToExclude);

                        sqLiteToExcel.exportSingleTable("expense_table", "expensetable.xls", new SQLiteToExcel.ExportListener() {
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

    public void AddData() {
        btnAddData.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                     boolean isInserted =   mydb.insertData(date,Integer.parseInt(editAmount.getText().toString()),
                             dropdown.getSelectedItem().toString() , editNote.getText().toString() );

                     if (isInserted == true) {
                         Toast.makeText(MainActivity.this, "Data Inserted", Toast.LENGTH_LONG).show();
                        /* amt= Integer.parseInt(editAmount.getText().toString());
                         totalamt+=amt;*/
                       //  if(showamt!= null)
                             //showamt.setText(Integer.toString(totalamt));
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


    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



}




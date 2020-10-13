package com.example.expensejournal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewExpense extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Intent intent;
    int total=0;
    TextView txtTotal;
    Spinner spinner;
    String[] items = new String[] {"All","Food","Hospital","Grocery","Electricity","Donation","Transportation","Gift","Others"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_view);

        DatabaseHelper db = new DatabaseHelper(this);
        ArrayList<HashMap<String, String>> userList = db.getExpense("All"); //Getting the expense from db

        ListView lv = (ListView) findViewById(R.id.expenses_list_view);
        ListAdapter adapter = new SimpleAdapter(ViewExpense.this,
                userList,
                R.layout.list_view_row,
                new String[]{"date","amount","category","note"},
                new int[]{R.id.date, R.id.amount, R.id.category,R.id.note} );
        lv.setAdapter(adapter);

        //getting total amount in list

        for (HashMap<String, String> entry : userList) {
            int value=0;
            for (String key : entry.keySet()) {
                value = Integer.parseInt(entry.get("amount"));
            }
            total+=value;
        }
        txtTotal = (TextView) findViewById(R.id.total_expense_sum_text_view);
        txtTotal.setText(Integer.toString(total));

        //updating listview by category

        spinner = (Spinner) findViewById(R.id.view_spinner);
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(ViewExpense.this, android.R.layout.simple_spinner_item,items);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinner_adapter);
        spinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String category = spinner.getSelectedItem().toString();
        DatabaseHelper db = new DatabaseHelper(this);
        ArrayList<HashMap<String, String>> userList = db.getExpense(category); //Getting the expense from db

        ListView lv = (ListView) findViewById(R.id.expenses_list_view);
        ListAdapter adapter = new SimpleAdapter(ViewExpense.this,
                userList,
                R.layout.list_view_row,
                new String[]{"date","amount","category","note"},
                new int[]{R.id.date, R.id.amount, R.id.category,R.id.note} );
        lv.setAdapter(adapter);
        int total1=0;
        //total expense update
        for (HashMap<String, String> entry : userList) {
            int value=0;
            for (String key : entry.keySet()) {
                value = Integer.parseInt(entry.get("amount"));
            }
            total1+=value;
        }
        txtTotal = (TextView) findViewById(R.id.total_expense_sum_text_view);
        txtTotal.setText(Integer.toString(total1));

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

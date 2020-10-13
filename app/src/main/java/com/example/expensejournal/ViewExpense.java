package com.example.expensejournal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewExpense extends AppCompatActivity {
    Intent intent;
    int total=0;
    TextView txtTotal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_view);

        DatabaseHelper db = new DatabaseHelper(this);
        ArrayList<HashMap<String, String>> userList = db.getExpense();

        ListView lv = (ListView) findViewById(R.id.expenses_list_view);
        ListAdapter adapter = new SimpleAdapter(ViewExpense.this, userList, R.layout.list_view_row,new String[]{"date","amount","category","note"},
                new int[]{R.id.date, R.id.amount, R.id.category,R.id.note});
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


    }

}

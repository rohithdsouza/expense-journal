package com.example.expensejournal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class DatabaseHelper extends SQLiteOpenHelper {
    TextView txtAmt;
    MainActivity obj = new MainActivity();
    //database table fields
    public static final String DATABASE_NAME = "expense.db";
    public static final String TABLE_NAME = "expense_table";
    public static final String COL_0 = "ID";
    public static final String COL_1 = "DATE";
    public static final String COL_2 = "AMOUNT";
    public static final String COL_3 = "CATEGORY";
    public static final String COL_4 = "NOTE";

    public DatabaseHelper( Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "( ID INTEGER PRIMARY KEY AUTOINCREMENT, DATE TEXT, AMOUNT INTEGER , CATEGORY TEXT, NOTE TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(("DROP TABLE IF EXISTS " + TABLE_NAME +";"));
            onCreate(db);
    }

    public boolean insertData(String date  , int amount , String category , String Note )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, date);
        contentValues.put(COL_2,amount);
        contentValues.put(COL_3, category);
        contentValues.put(COL_4,Note);
        long result = db.insert(TABLE_NAME,null, contentValues);
            if(result == -1)
                return false;
            else
                return true;
    }

    //get today's amount for MainS
    public int getTodayAmount()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String date=obj.getCurrentDate();
        int total=0;
        String query ="SELECT AMOUNT FROM " + TABLE_NAME + " WHERE DATE LIKE '"+ date+"'";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext() ) {
            int amt= cursor.getInt(cursor.getColumnIndex(COL_2));
            total+=amt;
        }
        return total;
    }

    //returns expense data as a arrayList
    public ArrayList<HashMap<String, String>> getExpense(String category)
    {
        String query;
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> expenseList = new ArrayList<>();
        if (category.equals("All"))
            query = "SELECT DATE, AMOUNT, CATEGORY, NOTE FROM "+ TABLE_NAME + " ORDER BY ID DESC";
        else
            query = "SELECT DATE, AMOUNT, CATEGORY, NOTE FROM "+ TABLE_NAME +" WHERE CATEGORY LIKE '" + category + "' ORDER BY ID DESC" ;

        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("date",cursor.getString(cursor.getColumnIndex(COL_1)));
            user.put("amount",cursor.getString(cursor.getColumnIndex(COL_2)));
            user.put("category",cursor.getString(cursor.getColumnIndex(COL_3)));
            user.put("note",cursor.getString(cursor.getColumnIndex(COL_4)));
            Log.d("myTag", user.get("amount"));
            expenseList.add(user);
        }
        return  expenseList;

    }


    public ArrayList<HashMap<String,String>> getExpenseByDateRange (String from , String to , String category)
    {
        SQLiteDatabase db= this.getWritableDatabase();
        String query;
        ArrayList<HashMap<String,String>> expenseList = new ArrayList<>();
        if(category == "All")
        query = "SELECT DATE , AMOUNT , CATEGORY , NOTE " + TABLE_NAME + " WHERE DATE BETWEEN "+ from + " AND " +to+" ORDER BY ID DESC";
        else
            query= "SELECT DATE , AMOUNT , CATEGORY , NOTE " + TABLE_NAME + " WHERE DATE BETWEEN "+ from + " AND " +to+
                    " AND CATEGORY LIKE '" + category + "'"+" ORDER BY ID DESC";

        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext())
        {
            HashMap<String,String> expenseElement = new HashMap<>();
            expenseElement.put("date",cursor.getString(cursor.getColumnIndex(COL_1)));
            expenseElement.put("amount",cursor.getString(cursor.getColumnIndex(COL_2)));
            expenseElement.put("category",cursor.getString(cursor.getColumnIndex(COL_3)));
            expenseElement.put("note",cursor.getString(cursor.getColumnIndex(COL_4)));
            expenseList.add(expenseElement);
        }
        return expenseList;
    }

    public ArrayList<HashMap<String,String>> getExpenseByDate (String date , String category)
    {
        SQLiteDatabase db= this.getWritableDatabase();
        String query;
        ArrayList<HashMap<String,String>> expenseList = new ArrayList<>();
        if(category != "All")
         query = "SELECT DATE , AMOUNT , CATEGORY , NOTE " + TABLE_NAME + " WHERE DATE LIKE  '"+ date +
                "' AND CATEGORY LIKE '" + category + "'"+ " ORDER BY ID DESC";
        else
          query ="SELECT DATE, AMOUNT, CATEGORY, NOTE " + TABLE_NAME + " WHERE DATE LIKE '"+ date + "' ORDER BY ID DESC";

        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext())
        {
            HashMap<String,String> expenseElement = new HashMap<>();
            expenseElement.put("date",cursor.getString(cursor.getColumnIndex(COL_1)));
            expenseElement.put("amount",cursor.getString(cursor.getColumnIndex(COL_2)));
            expenseElement.put("category",cursor.getString(cursor.getColumnIndex(COL_3)));
            expenseElement.put("note",cursor.getString(cursor.getColumnIndex(COL_4)));
            expenseList.add(expenseElement);
        }
        return expenseList;
    }

}


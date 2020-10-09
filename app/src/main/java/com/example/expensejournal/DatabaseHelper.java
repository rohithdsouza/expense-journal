package com.example.expensejournal;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {
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

    public boolean insertData(String date  , int amount , String category , String Note ) {
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


}

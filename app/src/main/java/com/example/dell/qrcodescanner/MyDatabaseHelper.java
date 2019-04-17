package com.example.dell.qrcodescanner;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "ResalaCharity";
    static final String TABLE1_NAME = "Data";
    static final String CREATE_DB_DATA = "create table if not exists " + TABLE1_NAME +
            " ( id INTEGER PRIMARY KEY AUTOINCREMENT , " +
            "name TEXT , " +
            "a INTEGER , " +
            "b INTEGER , " +
            "c INTEGER , " +
            "d INTEGER , " +
            "e INTEGER , " +
            "f INTEGER , " +
            "g INTEGER , " +
            "h INTEGER , " +
            "i INTEGER , " +
            "j INTEGER , " +
            "k INTEGER , " +
            "l INTEGER , " +
            "m INTEGER , " +
            "n INTEGER , " +
            "o INTEGER , " +
            "p INTEGER , " +
            "q INTEGER , " +
            "r INTEGER , " +
            "s INTEGER );";
    static final String TABLE2_NAME = "Names";
    static final String CREATE_DB_NAMES = "create table if not exists " + TABLE2_NAME +
            " ( id INTEGER PRIMARY KEY AUTOINCREMENT , " +
            "familyName TEXT );";


    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB_DATA);
        db.execSQL(CREATE_DB_NAMES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE1_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE2_NAME);
        onCreate(db);
    }

    public boolean addData(DataModel dataModel)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name" , dataModel.getName());
        values.put("a" , dataModel.getA());
        values.put("b" , dataModel.getB());
        values.put("c" , dataModel.getC());
        values.put("d" , dataModel.getD());
        values.put("e" , dataModel.getE());
        values.put("f" , dataModel.getF());
        values.put("g" , dataModel.getG());
        values.put("h" , dataModel.getH());
        values.put("i" , dataModel.getI());
        values.put("j" , dataModel.getJ());
        values.put("k" , dataModel.getK());
        values.put("l" , dataModel.getL());
        values.put("m" , dataModel.getM());
        values.put("n" , dataModel.getN());
        values.put("o" , dataModel.getO());
        values.put("p" , dataModel.getP());
        values.put("q" , dataModel.getQ());
        values.put("r" , dataModel.getR());
        values.put("s" , dataModel.getS());

        long result = database.insert(TABLE1_NAME , null , values);

        database.close();
        return result != -1 ? true : false;
    }

    public int getSumOfColumn(char code)
    {
        int sum = 0;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select sum(" + code + ") from " + TABLE1_NAME + " ;" , null);
        if (cursor.moveToFirst())
            sum = cursor.getInt(0);

        cursor.close();
        database.close();

        return sum;
    }

    public void deleteAllData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE1_NAME);
        db.close();
    }


    public boolean addName(String name)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("familyName" , name);

        long result = database.insert(TABLE2_NAME , null , values);

        database.close();
        return result != -1 ? true : false;
    }

    public void deleteAllNames()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE2_NAME);
        db.close();
    }

    public ArrayList<String> getAllNames()
    {
        ArrayList<String> itemsHolder = new ArrayList<>();

        String selectQuery = "select familyName from " + TABLE2_NAME;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery , null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
           itemsHolder.add(cursor.getString(0));
           cursor.moveToNext();
        }

        cursor.close();
        database.close();

        return itemsHolder;
    }


}

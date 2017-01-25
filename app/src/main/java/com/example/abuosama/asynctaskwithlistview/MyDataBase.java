package com.example.abuosama.asynctaskwithlistview;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Abu Osama on 25-01-2017.
 */

public class MyDataBase {

    private MyHelper myHelper;
    private SQLiteDatabase sqLiteDatabase;

    MyDataBase(Context c) {

        myHelper = new MyHelper(c, "techpalle.db", null, 1);
    }

    public void open() {

        sqLiteDatabase = myHelper.getWritableDatabase();
    }

    public void insert(String name, String email, String mobile) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("email", email);
        contentValues.put("mobile", mobile);
        sqLiteDatabase.insert("contacts", null, contentValues);

    }

    public Cursor queryContacts() {

        Cursor cursor = null;
        //read all student deatils
        cursor = sqLiteDatabase.query("contacts", null, null, null, null, null, null);
        //read student with sno

        // cursor =sqLiteDatabase.query("student",null,"_id=",null,null,null,null);

        //q3: read only deatils og andy
        // cursor =sqLiteDatabase.query("student",null,"sname='andy' ",null,null,null,null);

        //
        return cursor;
    }

    //step 3:
    private class MyHelper extends SQLiteOpenHelper {

        public MyHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

            //step4: create all required tables in this method
            sqLiteDatabase.execSQL("create table contacts(_id integer primary key,name text,email text ,mobile text);");

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

    public void close() {

        sqLiteDatabase.close();
    }
}

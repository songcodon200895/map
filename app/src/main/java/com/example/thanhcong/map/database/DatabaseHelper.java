package com.example.thanhcong.map.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.d("database_create","true");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public void query_excute(String query){
        SQLiteDatabase db =this.getReadableDatabase();
        db.execSQL(query);
        Log.e("create_database","success!");
    }

    public Cursor query_data(String query,String selectionArgs[]){
        SQLiteDatabase db =this.getWritableDatabase();
        Cursor cursor =db.rawQuery(query,selectionArgs);
        return cursor;
    }
}

package com.song1.musicno1.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.akquinet.android.androlog.Log;

/**
 * Created by leovo on 2014/4/8.
 */
public class DBHelper extends SQLiteOpenHelper {
  private static final int version = 1;
  private static final String DB_NAME = "song1";

  private static DBHelper dbHelper;
  private DBHelper(Context context) {
    super(context, DB_NAME, null, version);
  }

  public static DBHelper newInstance(Context context){
    if (dbHelper == null){
      dbHelper = new DBHelper(context);
    }
    return dbHelper;
  }
  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    Log.d(this,"OnCreate...");
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    Log.d(this, "onUpgrade...");
  }

}

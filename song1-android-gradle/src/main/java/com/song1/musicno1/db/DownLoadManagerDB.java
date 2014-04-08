package com.song1.musicno1.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by leovo on 2014/4/8.
 */
public class DownLoadManagerDB {

  DBHelper dbHelper;
  SQLiteDatabase database;

  public DownLoadManagerDB(Context context){
    dbHelper = new DBHelper(context);
    database = dbHelper.getWritableDatabase();
  }

  public void selectByState(int state){
  }
}

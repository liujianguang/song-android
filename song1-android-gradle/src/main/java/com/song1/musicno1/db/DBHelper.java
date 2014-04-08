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
  public static final String TB_DOWNLOAD_TASK = "downloadTask";
  private static final String SQL_DOWNLOAD_TAST =
      "drop table if exists " + TB_DOWNLOAD_TASK + ";" +
      "create table " + TB_DOWNLOAD_TASK + "(" +
          "id int not null AUTO_INCREMENT," +
          "filename text not null," +
          "fileUrl text not null," +
          "gotSize long not null," +
          "totalSize long not null," +
          "state int not null" +
          ")";

  public DBHelper(Context context) {
    super(context, DB_NAME, null, version);
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    Log.d(this,"OnCreate...");
    onUpgrade(sqLiteDatabase,version,version);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    Log.d(this,"onUpgrade...");
    sqLiteDatabase.execSQL(SQL_DOWNLOAD_TAST);
  }
}

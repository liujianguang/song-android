package com.song1.musicno1.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by leovo on 2014/4/8.
 */
public class DownLoadManagerDB {

  DBHelper       dbHelper;
  SQLiteDatabase database;

  public DownLoadManagerDB(Context context) {
    dbHelper = new DBHelper(context);
    database = dbHelper.getWritableDatabase();
  }

  public void selectByState(int state) {
    String selection = DownLoadTask.STATE + " = ?";
    Cursor cursor = database.query(DownLoadTask.TB_DOWNLOAD_TASK,
        new String[]{
            DownLoadTask.ID,
            DownLoadTask.FILE_NAME,
            DownLoadTask.GOT_SIZE,
            DownLoadTask.TOTAL_SIZE,
            DownLoadTask.STATE},
        selection,
        new String[]{state + ""}, null, null, null
    );
    List<DownLoadTask> taskList = Lists.newArrayList();

    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
      DownLoadTask task = new DownLoadTask();
      task.setId(cursor.getInt(cursor.getColumnIndex(DownLoadTask.ID)));
      task.setFileName(cursor.getString(cursor.getColumnIndex(DownLoadTask.FILE_NAME)));
      task.setFileUrl(cursor.getString(cursor.getColumnIndex(DownLoadTask.FILE_URL)));
      task.setGotSize(cursor.getLong(cursor.getColumnIndex(DownLoadTask.GOT_SIZE)));
      task.setTotalSize(cursor.getLong(cursor.getColumnIndex(DownLoadTask.TOTAL_SIZE)));
      task.setState(cursor.getInt(cursor.getColumnIndex(DownLoadTask.STATE)));
      taskList.add(task);
    }
  }

  public class DownLoadTask {

    public static final int STATE_IDEA    = 0;
    public static final int STATE_DOWNING = 1;
    public static final int STATE_PAUSE   = 2;
    public static final int STATE_FINISH  = 3;

    public static final String TB_DOWNLOAD_TASK = "downloadTask";
    public static final String ID               = "id";
    public static final String FILE_NAME        = "fileName";
    public static final String FILE_URL         = "fileUrl";
    public static final String GOT_SIZE         = "gotSize";
    public static final String TOTAL_SIZE       = "totalSize";
    public static final String STATE            = "state";

    private int    id;
    private String fileName;
    private String fileUrl;
    private long   gotSize;
    private long   totalSize;
    private int    state;

    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }

    public String getFileName() {
      return fileName;
    }

    public void setFileName(String fileName) {
      this.fileName = fileName;
    }

    public String getFileUrl() {
      return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
      this.fileUrl = fileUrl;
    }

    public long getGotSize() {
      return gotSize;
    }

    public void setGotSize(long gotSize) {
      this.gotSize = gotSize;
    }

    public long getTotalSize() {
      return totalSize;
    }

    public void setTotalSize(long totalSize) {
      this.totalSize = totalSize;
    }

    public int getState() {
      return state;
    }

    public void setState(int state) {
      this.state = state;
    }
  }
}

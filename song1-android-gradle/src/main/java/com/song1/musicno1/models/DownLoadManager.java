package com.song1.musicno1.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.song1.musicno1.db.DBHelper;
import com.song1.musicno1.util.FileUtil;
import de.akquinet.android.androlog.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by leovo on 2014/4/9.
 */
public class DownLoadManager {

  public static final String DOWN_DIR = "song1/download/";
  private static File              downDir;
  private static Context           context;
  private static DownLoadManager   downLoadManager;
  private static DownLoadManagerDB downLoadManagerDB;

  List<Task>                  taskList    = Lists.newArrayList();
  List<DownLoadListener>      listenerList = Lists.newArrayList();


  public interface DownLoadListener {
    void taskListChange(List<Task> taskList);
    void delTask(Task task);
  }

  private DownLoadManager(Context context) {
    downDir = FileUtil.getDir(DOWN_DIR);
    this.context = context;
    downLoadManagerDB = new DownLoadManagerDB(context);
  }

  public static DownLoadManager getDownLoadManager(Context context) {
    if (downLoadManager == null) {
      downLoadManager = new DownLoadManager(context);
    }
    return downLoadManager;
  }
  public Task newTask(String fileName, String fileUrl) {
    if (!fileName.endsWith(".mp3")) {
      fileName += ".mp3";
    }
    Task task = new Task(fileName, fileUrl);
    return task;
  }

  public void startTask(Task task) {
    Log.d(this, "startTask : " + task.state);
    if ((task.state != Task.STATE_IDEA) && (task.state != Task.STATE_STOP)){
      return;
    }
    if (task.id != 0){
      downLoadManagerDB.delTask(task);
    }
    task.start();
    taskList.add(task);
    dispaterTaskListChange();
  }

  public void stopTask(Task task) {
    Log.d(this, "stopTask : " + task.state);
    if (task.state != Task.STATE_DOWNING){
      return;
    }
    task.stop();
    downLoadManagerDB.addTask(task);
    taskList.remove(task);
    dispaterTaskListChange();
  }
  public void deleteTask(Task task){
    Log.d(this,"deleteTask : " + task.id);
    if (task.state == Task.STATE_DOWNING){
      task.stop();
      taskList.remove(task);
      dispaterTaskListChange();
    }else{
      downLoadManagerDB.delTask(task);
      dispaterDelTask(task);
    }
  }
  public List<Task> getTaskListOnRunning(){
    return taskList;
  }
  public List<Task> getTaskListOnStop(){
    return downLoadManagerDB.selectByState(Task.STATE_STOP);
  }
  public List<Task> getTaskListOnFinish(){
    return downLoadManagerDB.selectByState(Task.STATE_FINISH);
  }
  public long getTaskListOnRunningCount(){
    return taskList.size();
  }
  public long getTaskListOnStopCount(){
    return downLoadManagerDB.selectCountByState(Task.STATE_STOP);
  }
  public long getTaskListOnFinishCount(){
    return downLoadManagerDB.selectCountByState(Task.STATE_FINISH);
  }
  private void taskResult(Task task) {
    switch (task.state) {
      case Task.STATE_INIT_FAIL:
        Log.d(this, "task init fail : " + task);
        break;
      case Task.STATE_FINISH:
        Log.d(this, "task finish : " + task);
        downLoadManagerDB.addTask(task);
        taskList.remove(task);
        dispaterTaskListChange();
        break;
    }
  }
  public void registerDownLoadListener(DownLoadListener listener){
    listenerList.add(listener);
  }
  public void unRegisterDownLoadListener(DownLoadListener listener){
    listenerList.remove(listener);
  }
  private void dispaterTaskListChange(){
    for(DownLoadListener listener : listenerList){
      listener.taskListChange(taskList);
    }
  }
  private void dispaterDelTask(Task task){
    for(DownLoadListener listener : listenerList){
      listener.delTask(task);
    }
  }

  Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      Task task = (Task) msg.obj;
      switch (task.state) {
        case Task.STATE_INIT_FAIL:
        case Task.STATE_STOP:
        case Task.STATE_FINISH:
          taskResult(task);
          break;
      }
    }
  };

  public class Task {
    public static final int STATE_IDEA      = 0;
    public static final int STATE_INIT_ING  = 1;
    public static final int STATE_INIT_FAIL = 2;
    public static final int STATE_DOWNING   = 3;
    public static final int STATE_STOP      = 4;
    public static final int STATE_FINISH    = 5;

    private int    id;
    private String fileName;
    private String fileUrl;
    private long   gotSize;
    private long   totalSize;
    private int state = STATE_IDEA;
    private int oldState;

    File             file;
    URLConnection    conn;
    InputStream      is;
    FileOutputStream fos;
    Message          message;

    private Task() {
    };

    private Task(String fileName, String fileUrl) {
      this.fileName = fileName;
      this.fileUrl = fileUrl;
    }

    private void start() {
      message = new Message();
      message.obj = this;
      oldState = state;
      new Thread(new Runnable() {
        @Override
        public void run() {
          init();
        }
      }).start();
    }

    private void stop() {
      state = STATE_STOP;
      close();
    }

    private void init() {
      Log.d(this,"task init : " + this);
      state = STATE_INIT_ING;
      try {
        URL url = new URL(fileUrl);
        file = FileUtil.getFile(downDir, fileName);
        if (oldState == STATE_STOP) {
          fos = new FileOutputStream(file, true);
        }else{
          fos = new FileOutputStream(file,false);
        }
        conn = url.openConnection();
        conn.setRequestProperty("Range", "bytes=" + file.length() + "-");
        is = conn.getInputStream();
        totalSize = (file.length() + conn.getContentLength());
        read();
      } catch (IOException e) {
        e.printStackTrace();
        state = STATE_INIT_FAIL;
        handler.sendMessage(message);
        close();
      }
    }

    private void read() {
      Log.d(this,"task down : " + this);
      state = STATE_DOWNING;
      try {
        byte[] buff = new byte[1024 * 8];
        int count = -1;
        while ((count = is.read(buff)) != -1) {
          fos.write(buff, 0, count);
          gotSize = file.length();
        }
        state = STATE_FINISH;
        handler.sendMessage(message);
      } catch (IOException e) {
        e.printStackTrace();
      }finally {
        close();
      }
    }

    private void close() {
      try {
        if (is != null) {
          is.close();
        }
        if (fos != null) {
          fos.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    public int getId() {
      return id;
    }

    public String getFileName() {
      return fileName;
    }

    public String getFileUrl() {
      return fileUrl;
    }

    public long getGotSize() {
      return gotSize;
    }

    public long getTotalSize() {
      return totalSize;
    }

    public int getState() {
      return state;
    }
  }

  public class DownLoadManagerDB {

    DBHelper       dbHelper;
    SQLiteDatabase database;

    private DownLoadManagerDB(Context context) {
      dbHelper = DBHelper.newInstance(context);
      database = dbHelper.getWritableDatabase();
    }

    public void clear() {
      database.execSQL("drop table if exists" + TB_DOWNLOAD_TASK);
      database.execSQL(SQL_DOWNLOAD_TAST);
    }

    public void addTask(Task task) {
      Log.d(this, "addTask...");
      ContentValues values = new ContentValues();
      values.put(FILE_NAME, task.getFileName());
      values.put(FILE_URL, task.getFileUrl());
      values.put(GOT_SIZE, task.getGotSize());
      values.put(TOTAL_SIZE, task.getTotalSize());
      values.put(STATE, task.getState());
      long i = database.insert(TB_DOWNLOAD_TASK, null, values);
      Log.d(this, "addTask success？ : " + (i != 0 ? true : false));
    }

    public void delTask(Task task) {
      Log.d(this, "delTask : " + task.id);
      String where = ID + " = ?";
      int count = database.delete(TB_DOWNLOAD_TASK, where, new String[]{task.id + ""});
      Log.d(this, "delTask success? : " + (count != 0 ? true : false));
    }

    public void updTask(Task task) {
      Log.d(this, "updTask" + task.id);
      ContentValues values = new ContentValues();
      values.put(FILE_NAME, task.fileName);
      values.put(FILE_URL, task.fileUrl);
      values.put(GOT_SIZE, task.gotSize);
      values.put(TOTAL_SIZE, task.totalSize);
      values.put(STATE, task.state);
      String where = ID + " = ?";
      int count = database.update(TB_DOWNLOAD_TASK, values, where, new String[]{task.id + ""});
      Log.d(this, "updTask success ? : " + (count != 0 ? true : false));
    }

    public List<Task> selectByState(int state) {
      Log.d(this, "selectByState : " + state);
      String selection = STATE + " = ?";
      Cursor cursor = database.query(TB_DOWNLOAD_TASK,
          new String[]{
              ID,
              FILE_NAME,
              FILE_URL,
              GOT_SIZE,
              TOTAL_SIZE,
              STATE},
          selection,
          new String[]{state + ""}, null, null, null
      );
      List<Task> taskList = Lists.newArrayList();

      for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
        Task task = new Task();
        task.id = cursor.getInt(cursor.getColumnIndex(ID));
        task.fileName = cursor.getString(cursor.getColumnIndex(FILE_NAME));
        task.fileUrl = cursor.getString(cursor.getColumnIndex(FILE_URL));
        task.gotSize = cursor.getLong(cursor.getColumnIndex(GOT_SIZE));
        task.totalSize = cursor.getLong(cursor.getColumnIndex(TOTAL_SIZE));
        task.state = cursor.getInt(cursor.getColumnIndex(STATE));
        taskList.add(task);
      }
      cursor.close();
      Log.d(this, "result : " + taskList);
      return taskList;
    }
    public long selectCountByState(int state){
      Log.d(this,"selectCountByState : " + state);
      long count = 0;
      Cursor cursor = database.rawQuery("select count(*) from " + TB_DOWNLOAD_TASK + " where " + STATE + " = ?",new String[]{state+""});
      cursor.moveToFirst();
      count = cursor.getLong(0);
      cursor.close();
      Log.d(this,"count : " + count);
      return count;
    }

    public static final String TB_DOWNLOAD_TASK  = "downloadTask";
    public static final String SQL_DOWNLOAD_TAST =
        "create table " + TB_DOWNLOAD_TASK + "(" +
            "id integer primary key," +
            "fileName text not null," +
            "fileUrl text not null," +
            "gotSize long not null," +
            "totalSize long not null," +
            "state int not null" +
            ")";

    public static final String ID         = "id";
    public static final String FILE_NAME  = "fileName";
    public static final String FILE_URL   = "fileUrl";
    public static final String GOT_SIZE   = "gotSize";
    public static final String TOTAL_SIZE = "totalSize";
    public static final String STATE      = "state";
  }
}

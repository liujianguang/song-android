package com.song1.musicno1.util;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by leovo on 2014/4/10.
 */
public class FileUtil {


  public static File getDir(String path) {
    File sd = Environment.getExternalStorageDirectory();
    File dir = new File(sd, path);
    if (!dir.exists()){
      dir.mkdirs();
    }
    return dir;
  }

  public static File getFile(File dir,String name){
    File file = new File(dir,name);
    if (!file.exists()){
      try {
        file.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
        return null;
      }
    }
    return file;
  }
}

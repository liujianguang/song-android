package com.song1.musicno1.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by leovo on 2014/4/9.
 */
public class ToastUtil {


  public static void show(Context context,String msg){
    Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
  }
}

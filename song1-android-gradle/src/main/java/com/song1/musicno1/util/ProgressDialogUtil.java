package com.song1.musicno1.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by kate on 14-3-25.
 */
public class ProgressDialogUtil {


  static ProgressDialog create(Context context){
    ProgressDialog progressDialog = new ProgressDialog(context);
    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    return progressDialog;
  }
}

package com.song1.musicno1.dialogs;

import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by leovo on 2014/4/29.
 */
public class Dialog extends DialogFragment {

  @Override
  public void onResume() {
    super.onResume();
    Window window = getDialog().getWindow();
    DisplayMetrics dm = new DisplayMetrics();
    getActivity().getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
    int width = dm.widthPixels;
    //WindowManager.LayoutParams params = window.getAttributes();
    window.setLayout(width - 30, WindowManager.LayoutParams.WRAP_CONTENT);
  }
}

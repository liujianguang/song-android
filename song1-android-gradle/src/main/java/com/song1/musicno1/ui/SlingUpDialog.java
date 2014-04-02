package com.song1.musicno1.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.*;
import com.song1.musicno1.R;

/**
 * Created by leovo on 2014/4/2.
 */
public class SlingUpDialog extends DialogFragment {

  @Override
  public void onResume() {
    super.onResume();
    DisplayMetrics dm = new DisplayMetrics();
    getActivity().getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
    int width = dm.widthPixels;
    int height = dm.heightPixels;
    Window window = getDialog().getWindow();
    window.setWindowAnimations(R.style.slingUpStyle);
    WindowManager.LayoutParams params = window.getAttributes();
    window.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
    window.setGravity(Gravity.BOTTOM);
  }
}

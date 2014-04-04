package com.song1.musicno1.ui;

import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
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
    window.setWindowAnimations(R.style.slingUpAnimation);
    WindowManager.LayoutParams params = window.getAttributes();
    window.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
    window.setGravity(Gravity.BOTTOM);
  }
}

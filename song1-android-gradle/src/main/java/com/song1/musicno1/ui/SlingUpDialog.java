package com.song1.musicno1.ui;

import android.os.Bundle;
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
  public void onActivityCreated(Bundle savedInstanceState) {
    getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
    super.onActivityCreated(savedInstanceState);

  }

  @Override
  public void onResume() {
    super.onResume();
    Window window = getDialog().getWindow();
    window.setBackgroundDrawable(null);
    window.setWindowAnimations(R.style.slingUpAnimation);
    window.setGravity(Gravity.BOTTOM);
  }
}

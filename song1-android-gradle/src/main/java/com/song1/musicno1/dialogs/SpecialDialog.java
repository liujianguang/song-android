package com.song1.musicno1.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.*;
import com.song1.musicno1.R;

/**
 * Created by leovo on 2014/4/4.
 */
public class SpecialDialog extends BaseDialog{



  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
    super.onActivityCreated(savedInstanceState);
    getDialog().getWindow().setWindowAnimations(R.style.dialogAnimation);
    getDialog().getWindow().setBackgroundDrawable(null);
  }

  @Override
  public void show(FragmentManager manager, String tag) {
    super.show(manager, tag);
  }
}

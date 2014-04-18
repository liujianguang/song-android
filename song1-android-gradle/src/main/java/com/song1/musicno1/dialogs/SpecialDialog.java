package com.song1.musicno1.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.Window;
import com.song1.musicno1.R;

/**
 * Created by leovo on 2014/4/4.
 */
public class SpecialDialog extends BaseDialog{

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getDialog().getWindow().setWindowAnimations(R.style.dialogAnimation);
  }

  @Override
  public void onResume() {
    super.onResume();
  }
}

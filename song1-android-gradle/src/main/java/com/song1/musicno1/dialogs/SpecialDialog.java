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

  }

  @Override
  public void onResume() {
    super.onResume();

    Window window = getDialog().getWindow();
    DisplayMetrics dm = new DisplayMetrics();
    Display display = window.getWindowManager().getDefaultDisplay();
    display.getMetrics(dm);
    WindowManager.LayoutParams lp = window.getAttributes();

    window.setGravity(Gravity.LEFT | Gravity.TOP);
    System.out.println("****************width : " + lp.width);
    lp.x = 0; // 新位置X坐标
    lp.y = 0; // 新位置Y坐标
    lp.width = dm.widthPixels; // 宽度
    lp.height = dm.heightPixels; // 高度
    window.setAttributes(lp);
  }

  @Override
  public void show(FragmentManager manager, String tag) {
    super.show(manager, tag);
  }
}

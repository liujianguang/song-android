package com.song1.musicno1.helpers;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by windless on 3/27/14.
 */
public class ViewHelper {
  public static int dp2pixels(Context context, float dp) {
    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
    float pixels = metrics.density * dp;
    return (int) (pixels + 0.5f);
  }

  public static int px2dip(Context context, float pxValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (pxValue / scale + 0.5f);
  }
}

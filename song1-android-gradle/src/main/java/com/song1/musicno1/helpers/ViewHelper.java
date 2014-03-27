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
}

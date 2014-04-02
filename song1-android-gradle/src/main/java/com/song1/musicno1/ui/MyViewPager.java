package com.song1.musicno1.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: mac
 * Date: 13-11-28
 * Time: 下午6:10
 * To change this template use File | Settings | File Templates.
 */
public class MyViewPager extends ViewPager {


  public MyViewPager(Context context) {
    super(context);
  }

  public MyViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
    if (v instanceof MyGridView ) {
      return true;
    }
    return super.canScroll(v, checkV, dx, x, y);
  }


}

package com.song1.musicno1.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created with IntelliJ IDEA.
 * User: mac
 * Date: 13-11-28
 * Time: 下午2:45
 * To change this template use File | Settings | File Templates.
 */
public class MyGridView extends GridView  {

  private Context context;
  private ViewPager pager;

  public MyGridView(Context context)
  {
    super(context);
    this.context=context;
  }

  public MyGridView(Context context, AttributeSet attrs){

    super(context,attrs);
  }

  public void setViewPager(ViewPager p){

        this.pager = p;
  }

  @Override
  public boolean canScrollHorizontally(int direction) {
    this.pager.requestDisallowInterceptTouchEvent(true);
    return true;    //To change body of overridden methods use File | Settings | File Templates.
    // return super.canScrollHorizontally(direction);
  }







}

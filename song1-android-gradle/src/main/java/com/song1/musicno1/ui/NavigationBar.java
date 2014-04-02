package com.song1.musicno1.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;

import java.util.List;

/**
 * Created by leovo on 2014/4/2.
 */
public class NavigationBar extends LinearLayout implements ViewPager.OnPageChangeListener {

  String[]     titles;
  List<Button> buttonList;
  ViewPager    viewPager;
  int             colorResIdNormal   = R.color.content_bg_color;
  int             colorResIdSelected = R.color.title_bg_color;
  OnClickListener onClickListener    = new OnClickListener() {
    @Override
    public void onClick(View view) {
      changePage((Button) view);
    }
  };


  public NavigationBar(Context context) {
    super(context);
  }

  public NavigationBar(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public NavigationBar(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }


  public void setTitles(String[] titles) {
    this.titles = titles;
    createBar();
  }

  public void setViewPager(ViewPager viewPager) {
    this.viewPager = viewPager;
    this.viewPager.setOnPageChangeListener(this);
  }

  private void createBar() {
    buttonList = Lists.newArrayList();
    for (String title : titles) {
      Button button = new Button(getContext());
      button.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1.0f));
      button.setText(title);
      button.setOnClickListener(onClickListener);
      buttonList.add(button);
      addView(button);
    }
    setSelected(buttonList.get(0));
  }

  private void changePage(Button button) {
    if (viewPager != null) {
      viewPager.setCurrentItem(buttonList.indexOf(button));
      setSelected(button);
    }
  }

  private void setSelected(Button button) {
    for (Button btn : buttonList) {
      btn.setBackgroundColor(getResources().getColor(colorResIdNormal));
    }
    button.setBackgroundColor(getResources().getColor(colorResIdSelected));
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

  }

  @Override
  public void onPageSelected(int position) {
    setSelected(buttonList.get(position));
  }

  @Override
  public void onPageScrollStateChanged(int state) {

  }
}

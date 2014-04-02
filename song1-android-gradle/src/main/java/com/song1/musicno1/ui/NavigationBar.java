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
  List<String> titleList;
  List<Button> buttonList;
  ViewPager    viewPager;
  int             colorResIdNormal   = R.color.content_bg_color;
  int             colorResIdSelected = R.color.title_bg_color;
  Button prevButton;
  Button nextButton;
  OnClickListener onClickListener    = new OnClickListener() {
    @Override
    public void onClick(View view) {
      int position = buttonList.indexOf(view);
      if (position == 0 || position == (buttonList.size() - 1)){
        return;
      }
      changePage(position - 1);
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
    titleList = Lists.newArrayList();
    titleList.add("");
    titleList.addAll(Lists.newArrayList(titles));
    titleList.add("");
    createBar();
  }

  public void setViewPager(ViewPager viewPager) {
    this.viewPager = viewPager;
    this.viewPager.setOnPageChangeListener(this);
  }

  private void createBar() {
    buttonList = Lists.newArrayList();
    for (String title : titleList) {
      Button button = createButton(title);
      buttonList.add(button);
      addView(button);
    }
    setSelected(1);
  }
  private Button createButton(String title){
    Button button = new Button(getContext());
    button.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1.0f));
    button.setText(title);
    button.setOnClickListener(onClickListener);
    return button;
  }
  private void changePage(int position) {
    if (viewPager != null) {
      viewPager.setCurrentItem(position);
    }
  }

  private void setSelected(int position) {
    for (Button btn : buttonList) {
      btn.setBackgroundColor(getResources().getColor(colorResIdNormal));
      btn.setVisibility(View.GONE);
    }
    Button prevButton = buttonList.get(position - 1);
    Button button     = buttonList.get(position);
    Button nextButton = buttonList.get(position + 1);

    prevButton.setVisibility(View.VISIBLE);
    button.setVisibility(View.VISIBLE);
    nextButton.setVisibility(View.VISIBLE);
    button.setBackgroundColor(getResources().getColor(colorResIdSelected));
//    button.setBackgroundColor(getResources().getColor(colorResIdSelected));
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

  }

  @Override
  public void onPageSelected(int position) {
    setSelected(position + 1);
  }

  @Override
  public void onPageScrollStateChanged(int state) {

  }
}

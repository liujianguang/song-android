package com.song1.musicno1.fragments.download;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;
import com.song1.musicno1.fragments.base.BaseFragment;
import de.akquinet.android.androlog.Log;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by leovo on 2014/4/8.
 */
public class DownLoadManagerFragment extends BaseFragment {

  @InjectView(R.id.taskDoingButton)
  Button         taskDoingButton;
  @InjectView(R.id.taskDoneButton)
  Button         taskDoneButton;
  @InjectView(R.id.container)
  RelativeLayout container;

  @OnClick(R.id.taskDoingButton)
  public void taskDoingList(Button button)
  {
    show(button);
  }

  @OnClick(R.id.taskDoneButton)
  public void taskDoneList(Button button)
  {
    show(button);
  }

  TaskDoingFragment taskDoingFragment;
  TaskDoneFragment  taskDoneFragment;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Log.d(this,"onCreateView...");
    View view = inflater.inflate(R.layout.fragment_download_manager, null);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Log.d(this,"onActivityCreated...");
    taskDoingFragment = new TaskDoingFragment();
    taskDoneFragment = new TaskDoneFragment();
    show(taskDoingButton);
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(this,"onResume...");
  }

  @Override
  public void onPause() {
    super.onPause();
    Log.d(this,"onPause...");
  }

  private void show(Button button){
    taskDoingButton.setBackgroundColor(Color.TRANSPARENT);
    taskDoneButton.setBackgroundColor(Color.TRANSPARENT);
    button.setBackgroundColor(getResources().getColor(R.color.content_bg_color));
    Fragment fragment;
    if (button.getId() == R.id.taskDoingButton){
      fragment = taskDoingFragment;
    }else{
      fragment = taskDoneFragment;
    }
    getFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
  }
}

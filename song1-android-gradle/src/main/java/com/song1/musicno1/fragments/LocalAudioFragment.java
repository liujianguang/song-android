package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;

import java.lang.reflect.Field;
import java.util.List;

/**
 * User: windless
 * Date: 14-2-7
 * Time: PM2:01
 */
public class LocalAudioFragment extends BaseFragment {

  List<Integer>  titles     = Lists.newArrayList(R.string.song, R.string.album, R.string.artist);
  List<Fragment> fragments  = Lists.newArrayList();
  List<Button>   buttonList = Lists.newArrayList();

  //  @InjectView(R.id.indicator) TitlePageIndicator indicator;
  @InjectView(R.id.pager)        ViewPager viewPager;
  @InjectView(R.id.prevButton)   Button    preButton;
  @InjectView(R.id.songButton)   Button    songButton;
  @InjectView(R.id.albumButton)  Button    albumButton;
  @InjectView(R.id.artistButton) Button    artistButton;
  @InjectView(R.id.nextButton)   Button    nextButton;
  FragmentAdapter adapter;
  int currentPostion = 0;

  @OnClick(R.id.songButton)
  public void songButtonClick(Button button) {
    changePage(button);
  }

  @OnClick(R.id.albumButton)
  public void albumButtonClick(Button button) {
    changePage(button);
  }

  @OnClick(R.id.artistButton)
  public void artistButtonClick(Button button) {
    changePage(button);
  }

  ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
      currentPostion = position;
      changeStyle(buttonList.get(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
  };

  private void changePage(Button button) {
    currentPostion = buttonList.indexOf(button);
    viewPager.setCurrentItem(currentPostion);
  }

  private void changeStyle(Button button) {
    System.out.println("currentPostion : " + currentPostion);
    int color_normal = getResources().getColor(R.color.title_bg_color);
    int color_select = getResources().getColor(R.color.content_bg_color);
    for (Button btn : buttonList) {
      btn.setBackgroundColor(color_normal);
      System.out.println("btn : " + btn);
    }
    System.out.println("button : " + button);
    button.setBackgroundColor(color_select);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    fragments.add(new LocalAudioFrag());
    fragments.add(new LocalAlbumFrag());
    fragments.add(new LocalArtistFrag());
    adapter = new FragmentAdapter(getChildFragmentManager());
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    System.out.println("onActivityCreate...");
//    has_home_button(true);
    setTitle(getString(R.string.local_source));
    buttonList.clear();
    buttonList.add(songButton);
    buttonList.add(albumButton);
    buttonList.add(artistButton);
    viewPager.setAdapter(adapter);
    viewPager.setOnPageChangeListener(onPageChangeListener);
    changeStyle(buttonList.get(currentPostion));
  }

  @Override
  public void onResume() {
    System.out.println("onResume...");
    super.onResume();
  }

  @Override
  public void onDestroy() {
    System.out.println("onDestroy...");
    super.onDestroy();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    System.out.println("onCreateView...");
    View view = inflater.inflate(R.layout.fragment_local_audio, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  class FragmentAdapter extends FragmentPagerAdapter {

    public FragmentAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      Fragment fragment = fragments.get(position);
      return fragment;
    }

    @Override
    public int getCount() {
      return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return getString(titles.get(position));
    }
  }

}

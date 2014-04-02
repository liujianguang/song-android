package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
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


  List<Integer>  titles    = Lists.newArrayList(R.string.song, R.string.album, R.string.artist);
  List<Fragment> fragments = Lists.newArrayList();

  //  @InjectView(R.id.indicator) TitlePageIndicator indicator;
  @InjectView(R.id.pagerTitleStrip) PagerTitleStrip pagerTitleStrip;
  @InjectView(R.id.pager)           ViewPager       viewPager;

  FragmentAdapter adapter;

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
    pagerTitleStrip.setGravity(Gravity.CENTER);
    pagerTitleStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

    viewPager.setAdapter(adapter);
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

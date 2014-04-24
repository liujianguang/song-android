package com.song1.musicno1.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
import com.song1.musicno1.fragments.base.BaseFragment;
import com.song1.musicno1.ui.ArrowView;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.List;

/**
 * User: windless
 * Date: 14-2-7
 * Time: PM2:01
 */
public class LocalAudioContainerFragment extends BaseFragment implements ViewPager.OnPageChangeListener {
  List<Integer> titles = Lists.newArrayList(R.string.song, R.string.album, R.string.artist);

  List<Fragment> fragments;

  @Inject LocalAudioFragment  localAudioFragment;
  @Inject LocalAlbumFragment  localAlbumFragment;
  @Inject LocalArtistFragment localArtistFragment;

  @InjectView(R.id.songButton)   Button    songButotn;
  @InjectView(R.id.artistButton) Button    artistButton;
  @InjectView(R.id.albumButton)  Button    albumButton;
  @InjectView(R.id.arrow)        ArrowView arrowView;
  @InjectView(R.id.pager)        ViewPager viewPager;

  @OnClick(R.id.songButton)
  public void onSongButtonClick(){
    viewPager.setCurrentItem(0);
  }
  @OnClick(R.id.artistButton)
  public void onArtistButtonClick(){
    viewPager.setCurrentItem(1);
  }
  @OnClick(R.id.albumButton)
  public void onAlbumButton(){
    viewPager.setCurrentItem(2);
  }
  FragmentAdapter adapter;

  @Inject
  public LocalAudioContainerFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    fragments = Lists.newArrayList(
        localAudioFragment,
        localAlbumFragment,
        localArtistFragment
    );
    adapter = new FragmentAdapter(getChildFragmentManager());
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setTitle(getString(R.string.local_source));
    viewPager.setAdapter(adapter);
    viewPager.setOnPageChangeListener(this);
    viewPager.setCurrentItem(0);
    //onPageSelected(0);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_local_audio, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    arrowView.setFristPoint(songButotn);
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

  }

  @Override
  public void onPageSelected(int position) {
    Button button = null;
     switch (position){
       case 0:
         button = songButotn;
         break;
       case 1:
         button = artistButton;
         break;
       case 2:
         button = albumButton;
         break;
     }
    if (button != null) {
      songButotn.setTextColor(Color.BLACK);
      artistButton.setTextColor(Color.BLACK);
      albumButton.setTextColor(Color.BLACK);
      button.setTextColor(Color.WHITE);
      arrowView.move(button);
    }
  }

  @Override
  public void onPageScrollStateChanged(int state) {

  }

  class FragmentAdapter extends FragmentPagerAdapter {

    public FragmentAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      return fragments.get(position);
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


  @Override
  public void onDetach() {
    super.onDetach();
    try {
      Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
      childFragmentManager.setAccessible(true);
      childFragmentManager.set(this, null);

    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}

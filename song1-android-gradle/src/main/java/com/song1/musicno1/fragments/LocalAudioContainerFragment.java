package com.song1.musicno1.fragments;

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
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.List;

/**
 * User: windless
 * Date: 14-2-7
 * Time: PM2:01
 */
public class LocalAudioContainerFragment extends BaseFragment {
  List<Integer> titles = Lists.newArrayList(R.string.song, R.string.album, R.string.artist);

  List<Fragment> fragments;

  @Inject LocalAudioFragment  localAudioFragment;
  @Inject LocalAlbumFragment  localAlbumFragment;
  @Inject LocalArtistFragment localArtistFragment;

  @InjectView(R.id.pagerTitleStrip) PagerTitleStrip pagerTitleStrip;
  @InjectView(R.id.pager)           ViewPager       viewPager;

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
    pagerTitleStrip.setGravity(Gravity.CENTER);
    pagerTitleStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

    viewPager.setAdapter(adapter);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

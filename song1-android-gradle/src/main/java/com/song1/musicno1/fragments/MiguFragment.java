package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;

/**
 * Created by windless on 3/31/14.
 */
public class MiguFragment extends Fragment {
  private static final int[] titles = new int[]{R.string.song_menu, R.string.ranking, R.string.album, R.string.search};
  @InjectView(R.id.pager) ViewPager pager;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getActivity().getActionBar().setTitle(R.string.migu_title);
    pager.setAdapter(new MiguAdapter(getChildFragmentManager()));
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_taps, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  class MiguAdapter extends FragmentPagerAdapter {

    public MiguAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      return new TestFragment();
    }

    @Override
    public int getCount() {
      return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return getString(titles[position]);
    }
  }
}

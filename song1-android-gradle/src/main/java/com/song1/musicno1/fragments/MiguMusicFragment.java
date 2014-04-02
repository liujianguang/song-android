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
import com.song1.musicno1.R;
import com.song1.musicno1.fragments.migu.MiguAlbumListFragment;
import com.song1.musicno1.fragments.migu.MiguRankingListFragment;
import com.song1.musicno1.fragments.migu.MiguSearchFragment;
import com.song1.musicno1.fragments.migu.MiguSongListFragment;
import com.song1.musicno1.ui.MyViewPager;
import com.song1.musicno1.ui.NavigationBar;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: mac
 * Date: 13-9-29
 * Time: 下午5:08
 */
public class MiguMusicFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

  private PagerTitleStrip pagerTitleStrip;
  private MyViewPager     vp;

  private String[]           titles    = new String[]{"歌单", "排行榜", "专辑", "搜索"};
  private List<BaseFragment> fragments = new ArrayList<BaseFragment>();

  public MiguMusicFragment() {
    fragments.add(new MiguSongListFragment());
    fragments.add(new MiguRankingListFragment());
    fragments.add(new MiguAlbumListFragment());
    fragments.add(new MiguSearchFragment());
//    for (BaseFragment fragment : fragments) {
////      fragment.parent(this);
//    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.migu_content, container, false);

    vp = (MyViewPager) view.findViewById(R.id.view_page);
    pagerTitleStrip = (PagerTitleStrip) view.findViewById(R.id.pagerTitleStrip);
    pagerTitleStrip.setGravity(Gravity.CENTER);
    pagerTitleStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);

//    MiguArtistsListFrag artists = (MiguArtistsListFrag) fragments.get(fragments.size() - 2);
//    artists.viewPage = vp; // 这个参数传递的是不是大了点
//    tpi = (TitlePageIndicator) view.findViewById(R.id.title_page_indicator);

    has_touch_mode(false);

    return view;

  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    setTitle(getString(R.string.migu_title));
    vp.setAdapter(new MiguMusicAdapter(getChildFragmentManager()));
//    tpi.setViewPager(vp);
//    tpi.setOnPageChangeListener(this);
    vp.setCurrentItem(0);
  }

  class MiguMusicAdapter extends FragmentPagerAdapter {
    public MiguMusicAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      BaseFragment fragment = fragments.get(position);
      fragment.setParent(MiguMusicFragment.this);
      return fragment;
    }

    @Override
    public int getCount() {
      return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return titles[position];
    }
  }


  @Override
  public void onPageSelected(int position) {
  }

  @Override
  public void onPageScrollStateChanged(int state) {
  }


  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
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

package com.song1.musicno1.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.RadioGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.common.collect.Lists;
import com.song1.musicno1.App;
import com.song1.musicno1.R;
import com.song1.musicno1.dialogs.MediaScannerDialog;
import com.song1.musicno1.fragments.base.BaseFragment;
import com.song1.musicno1.models.LocalAudioStore;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.List;

/**
 * User: windless
 * Date: 14-2-7
 * Time: PM2:01
 */
public class LocalAudioContainerFragment extends BaseFragment implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {
  List<Integer> titles = Lists.newArrayList(R.string.song, R.string.album, R.string.artist);

  List<Fragment> fragments;

  LocalAudiosWithIndexFragment localAudioFragment;
  LocalAlbumFragment           localAlbumFragment;
  LocalArtistFragment          localArtistFragment;

  @InjectView(R.id.pager)           ViewPager  viewPager;
  @InjectView(R.id.segment_control) RadioGroup segmentControl;

  FragmentAdapter adapter;

  int selectedSegmentId = R.id.songButton;
  protected LocalAudioStore localAudioStore;

  public FragmentPagerAdapter getAdapter() {
    return adapter;
  }

  public ViewPager getViewPager() {
    return viewPager;
  }

  @Inject
  public LocalAudioContainerFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    localAudioFragment = new LocalAudiosWithIndexFragment();
    localAlbumFragment = new LocalAlbumFragment();
    localArtistFragment = new LocalArtistFragment();
    fragments = Lists.newArrayList(
        localAudioFragment,
        localAlbumFragment,
        localArtistFragment
    );
    adapter = new FragmentAdapter(getChildFragmentManager());
    localAudioStore = App.get(LocalAudioStore.class);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setTitle(getString(R.string.local_source));
    viewPager.setAdapter(adapter);
    viewPager.setOnPageChangeListener(this);

    segmentControl.check(selectedSegmentId);
    segmentControl.setOnCheckedChangeListener(this);

    setHasOptionsMenu(true);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.local_audio, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.refresh:
        showScanConfirmDialog();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void showScanConfirmDialog() {
    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
    alert.setTitle(R.string.notice)
        .setMessage(R.string.need_to_scan)
        .setPositiveButton(android.R.string.ok, (dialog, whichButton) -> {
          dialog.dismiss();
          showScannerDialog();
        })
        .setNegativeButton(android.R.string.cancel, (dialog, whichButton) -> dialog.dismiss())
        .show();
  }

  private void showScannerDialog() {
    MediaScannerDialog dialog = new MediaScannerDialog();
    dialog.onDismiss((isFinish) -> {
      if (isFinish) {
        localAudioStore.cleanCache();
        localAudioFragment.reload();
        localAlbumFragment.reload();
        localArtistFragment.reload();
      }
    });
    dialog.show(getFragmentManager(), "Scanner");
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_local_audio, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
  }

  @Override
  public void onPageSelected(int position) {
    segmentControl.setOnCheckedChangeListener(null);
    switch (position) {
      case 0:
        segmentControl.check(R.id.songButton);
        break;
      case 1:
        segmentControl.check(R.id.albumButton);
        break;
      case 2:
        segmentControl.check(R.id.artistButton);
        break;
    }
    segmentControl.setOnCheckedChangeListener(this);
  }

  @Override
  public void onPageScrollStateChanged(int state) {

  }

  @Override
  public void onCheckedChanged(RadioGroup radioGroup, int i) {
    switch (i) {
      case R.id.songButton:
        viewPager.setCurrentItem(0);
        break;
      case R.id.albumButton:
        viewPager.setCurrentItem(1);
        break;
      case R.id.artistButton:
        viewPager.setCurrentItem(2);
    }
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

package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.CloudArtistPagerAdapter;
import com.song1.musicno1.entity.Artist;

import javax.inject.Inject;

/**
 * User: windless
 * Date: 13-9-9
 * Time: PM4:41
 */
public class CloudArtistDetailFrag extends BaseFragment {
  @InjectView(R.id.pager) ViewPager pager;

  private Artist artist;

  @Inject
  public CloudArtistDetailFrag() {
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.pager_album_song_frag, container, false);
    ButterKnife.inject(this, view);

    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    CloudArtistPagerAdapter adapter = new CloudArtistPagerAdapter(getChildFragmentManager(), getActivity());
    adapter.fragment(this);
    adapter.artist(artist);

    pager.setAdapter(adapter);
  }

  public void artist(Artist artist) {
    this.artist = artist;
    setTitle(artist.name);
  }
}

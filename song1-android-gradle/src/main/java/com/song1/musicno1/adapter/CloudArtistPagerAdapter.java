package com.song1.musicno1.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.song1.musicno1.App;
import com.song1.musicno1.R;
import com.song1.musicno1.entity.Artist;
import com.song1.musicno1.fragments.CloudAlbumsFrag;
import com.song1.musicno1.fragments.CloudArtistDetailFrag;
import com.song1.musicno1.fragments.CloudAudiosFrag;

/**
 * User: windless
 * Date: 13-9-9
 * Time: PM4:46
 */
public class CloudArtistPagerAdapter extends FragmentPagerAdapter {

  private final Context context;

  private Artist                artist;
  private CloudArtistDetailFrag frag;

  public CloudArtistPagerAdapter(FragmentManager fm, Context context) {
    super(fm);
    this.context = context;
  }

  @Override
  public Fragment getItem(int position) {
    switch (position) {
      case 0:
        CloudAudiosFrag audios_frag = App.get(CloudAudiosFrag.class);
        audios_frag.load(artist);
        return audios_frag;
      default:
        CloudAlbumsFrag albums_frag = App.get(CloudAlbumsFrag.class);
        albums_frag.parent(frag);
        albums_frag.load(artist);
        return albums_frag;
    }
  }

  @Override
  public int getCount() {
    return 2;
  }

  @Override
  public CharSequence getPageTitle(int position) {
    switch (position) {
      case 0:
        return context.getString(R.string.song);
      default:
        return context.getString(R.string.album);
    }
  }

  public void artist(Artist artist) {
    this.artist = artist;
  }


  public void fragment(CloudArtistDetailFrag frag) {
    this.frag = frag;
  }
}

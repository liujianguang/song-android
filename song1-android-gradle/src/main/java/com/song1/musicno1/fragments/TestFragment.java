package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.song1.musicno1.R;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Players;

/**
 * Created by windless on 3/28/14.
 */
public class TestFragment extends Fragment {
  private final static String URL = "http://glmusic.oss-cn-hangzhou.aliyuncs.com/music/By2%20-%20%E8%A7%A6%E5%8A%A8%E5%BF%83%EF%BC%8C%E8%A7%A6%E5%8A%A8%E7%88%B1.mp3";

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_test, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @OnClick(R.id.play)
  public void play() {
    Audio audio = new Audio();
    audio.setTitle("触动心，触动爱");
    audio.setRemotePlayUrl(URL);
    audio.setAlbum("");
    audio.setArtist("By2");
    audio.setFrom(Audio.OTHER);
  }

  @OnClick(R.id.pause)
  public void pause() {
    Players.pause();
  }
}

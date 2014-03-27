package com.song1.musicno1.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.CurrentNotworkDeviceActivity;

/**
 * Created by windless on 3/27/14.
 */
public class PlayBarFragment extends Fragment {
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_play_bar, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @OnClick(R.id.player_list)
  public void showPlayerList() {
    startActivity(new Intent(getActivity(), CurrentNotworkDeviceActivity.class));
  }
}

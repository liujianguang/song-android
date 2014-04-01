package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.BaseAdapter;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.models.events.play.CurrentPlaylistEvent;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Players;
import com.squareup.otto.Subscribe;

/**
 * Created by windless on 4/1/14.
 */
public class PlaylistFragment extends Fragment implements AdapterView.OnItemClickListener {
  protected              BaseAdapter<Audio, ViewHolder> adapter;
  @InjectView(R.id.list) ListView                       listView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_playlist, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    adapter = newAdapter();
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(this);
  }

  private BaseAdapter<Audio, ViewHolder> newAdapter() {
    return new BaseAdapter<Audio, ViewHolder>(getActivity(), R.layout.item_text)
        .bind(() -> new ViewHolder())
        .setData((i, audio, holder) -> {
          holder.title.setText(audio.getTitle());
        });
  }

  @Override
  public void onResume() {
    super.onResume();
    MainBus.register(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    MainBus.unregister(this);
  }

  @Subscribe
  public void playlistChanged(CurrentPlaylistEvent event) {
    adapter.setList(event.getPlaylist().getAudios());
    adapter.notifyDataSetChanged();
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    Audio audio = adapter.getElement(i);
    Players.play(audio);
  }

  class ViewHolder extends BaseAdapter.ViewHolder {
    @InjectView(R.id.title) TextView title;

    @Override
    public void inject(View view) {
      ButterKnife.inject(this, view);
    }
  }
}

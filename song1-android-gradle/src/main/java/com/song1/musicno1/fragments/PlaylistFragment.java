package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.BaseAdapter;
import com.song1.musicno1.entity.AudioGroup;
import com.song1.musicno1.event.Event;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.models.events.play.CurrentPlaylistEvent;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Players;
import com.song1.musicno1.models.play.Playlist;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * Created by windless on 4/1/14.
 */
public class PlaylistFragment extends Fragment implements AdapterView.OnItemClickListener {
  protected              BaseAdapter<Audio, ViewHolder> adapter;
  protected              Playlist                       playlist;
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

  static Audio playingAudio;

  @Subscribe
  public void playingAudio(Event.PlayingAudioEvent event){
    playingAudio = event.getAudio();
    adapter.notifyDataSetChanged();
  }

  private BaseAdapter<Audio, ViewHolder> newAdapter() {
    return new BaseAdapter<Audio, ViewHolder>(getActivity(), R.layout.item_text)
        .bind(() -> new ViewHolder())
        .setData((i, audio, holder) -> {
          if (audio == playingAudio){
            holder.currentAudioImageView.setVisibility(View.VISIBLE);
          }else{
            holder.currentAudioImageView.setVisibility(View.INVISIBLE);
          }
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
    playlist = event.getPlaylist();

    List<Audio> list = Lists.newArrayList();
    if (playlist.getAudios() != null) {
      for (Audio audio :playlist.getAudios()){
        if (audio instanceof AudioGroup){
          continue;
        }
        list.add(audio);
      }
    }
    adapter.setList(list);
    adapter.notifyDataSetChanged();
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    Audio audio = adapter.getElement(i);
    playlist.setCurrentAudio(audio);
    Players.play();
  }

  class ViewHolder extends BaseAdapter.ViewHolder {
    @InjectView(R.id.currentAudioImageView) ImageView currentAudioImageView;
    @InjectView(R.id.title) TextView title;

    @Override
    public void inject(View view) {
      ButterKnife.inject(this, view);
    }
  }
}

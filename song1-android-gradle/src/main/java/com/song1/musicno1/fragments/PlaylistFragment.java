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
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.BaseAdapter;
import com.song1.musicno1.adapter.DataAdapter;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Player;
import com.song1.musicno1.models.play.Players;
import com.song1.musicno1.models.play.Playlist;
import com.song1.musicno1.stores.PlayerStore;
import com.squareup.otto.Subscribe;

/**
 * Created by windless on 4/1/14.
 */
public class PlaylistFragment extends Fragment implements AdapterView.OnItemClickListener {
  protected Playlist                       playlist;
  private   DataAdapter<Audio>             audiosAdapter;
  private   Audio                          playingAudio;

  @InjectView(R.id.list) ListView listView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_playlist, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    audiosAdapter = newAdapter();
    listView.setAdapter(audiosAdapter);
    listView.setOnItemClickListener(this);

    updatePlayerInfo(null);
  }

  @Subscribe
  public void updatePlayerInfo(PlayerStore.CurrentPlayerChangedEvent event) {
    updatePlaylist(null);
    updatePlayingAudio(null);
  }

  @Subscribe
  public void updatePlaylist(PlayerStore.PlayerPlaylistChangedEvent event) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer != null) {
      playlist = currentPlayer.getPlaylist();
      if (playlist != null) {
        audiosAdapter.setDataList(playlist.getAudios());
      } else {
        audiosAdapter.setDataList(null);
      }
      audiosAdapter.notifyDataSetChanged();
    }
  }

  @Subscribe
  public void updatePlayingAudio(PlayerStore.PlayerPlayingAudioChangedEvent event) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer != null) {
      playingAudio = currentPlayer.getPlayingAudio();
      audiosAdapter.notifyDataSetChanged();
    }
  }

  private DataAdapter<Audio> newAdapter() {
    return new DataAdapter<Audio>(getActivity()) {
      @Override
      public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
          view = View.inflate(getActivity(), R.layout.item_playlist_item, null);
          holder = new ViewHolder();
          holder.inject(view);
          view.setTag(holder);
        } else {
          holder = (ViewHolder) view.getTag();
        }

        Audio audio = getDataItem(i);
        if (audio == playingAudio) {
          holder.currentAudioImageView.setVisibility(View.VISIBLE);
        } else {
          holder.currentAudioImageView.setVisibility(View.INVISIBLE);
        }
        holder.title.setText(audio.getTitle());

        return view;
      }
    };
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

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    Audio audio = audiosAdapter.getDataItem(i);
    Players.playWithAudio(audio);
  }

  class ViewHolder extends BaseAdapter.ViewHolder {
    @InjectView(R.id.currentAudioImageView) ImageView currentAudioImageView;
    @InjectView(R.id.title)                 TextView  title;

    @Override
    public void inject(View view) {
      ButterKnife.inject(this, view);
    }
  }
}

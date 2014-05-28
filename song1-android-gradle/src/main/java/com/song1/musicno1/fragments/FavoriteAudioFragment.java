package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.DataAdapter;
import com.song1.musicno1.fragments.base.ListFragment;
import com.song1.musicno1.helpers.List8;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.models.Favorite;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Player;
import com.song1.musicno1.models.play.Players;
import com.song1.musicno1.models.play.Playlist;
import com.song1.musicno1.stores.PlayerStore;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * Created by windless on 14-4-10.
 */
public class FavoriteAudioFragment extends ListFragment<Audio> implements AdapterView.OnItemClickListener {
  private   Favorite favorite;
  protected Audio    playingAudio;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (favorite != null) {
      setTitle(favorite.name);
    }
    getListView().setOnItemClickListener(this);
    setHasOptionsMenu(true);
  }

  @Override
  protected List<Audio> onLoad(int loadPage) {
    if (favorite == null) {
      Favorite redHeart = Favorite.load(Favorite.class, 1);
      return List8.newList(redHeart.audios()).map((favoriteAudio) -> favoriteAudio.toAudio());
    } else {
      return List8.newList(favorite.audios()).map((favoriteAudio) -> favoriteAudio.toAudio());
    }
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.no_menu, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public void onResume() {
    super.onResume();
    reload();
    MainBus.register(this);
    updateCurrentPlayerState(null);
  }

  @Override
  public void onPause() {
    super.onPause();
    MainBus.unregister(this);
  }

  @Subscribe
  public void updateCurrentPlayerState(PlayerStore.CurrentPlayerChangedEvent event) {
    updatePlayingAudio(null);
  }

  @Subscribe
  public void updatePlayingAudio(PlayerStore.PlayerPlayingAudioChangedEvent event) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer != null) {
      playingAudio = currentPlayer.getPlayingAudio();
    }
    getAdapter().notifyDataSetChanged();
  }

  @Override
  protected DataAdapter<Audio> newAdapter() {
    return new DataAdapter<Audio>(getActivity()) {
      @Override
      public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
          view = View.inflate(getActivity(), R.layout.item_audio, null);
          holder = new ViewHolder(view);
          view.setTag(holder);
        } else {
          holder = (ViewHolder) view.getTag();
        }


        Audio audio = getDataItem(i);
        holder.index.setText("" + (i + 1));
        holder.title.setText(audio.getTitle());
        holder.subtitle.setText(audio.getSubtitle(context));

        if (audio.isEqual(playingAudio)) {
          holder.index.setVisibility(View.GONE);
          holder.playingFlag.setVisibility(View.VISIBLE);
        } else {
          holder.index.setVisibility(View.VISIBLE);
          holder.playingFlag.setVisibility(View.GONE);
        }

        if (audio.isLossless()) {
          holder.loseless.setVisibility(View.VISIBLE);
        } else {
          holder.loseless.setVisibility(View.GONE);
        }

        return view;
      }
    };
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    Playlist playlist = new Playlist(List8.newList(getDataList()), getDataItem(i));
    Players.setPlaylist(playlist, getFragmentManager());
  }

  public void setFavorite(Favorite favorite) {
    this.favorite = favorite;
  }

  class ViewHolder {
    @InjectView(R.id.title)        TextView    title;
    @InjectView(R.id.subtitle)     TextView    subtitle;
    @InjectView(R.id.menu_btn)     ImageButton menu_btn;
    @InjectView(R.id.index)        TextView    index;
    @InjectView(R.id.menu)         View        menu;
    @InjectView(R.id.playing_flag) View        playingFlag;
    @InjectView(R.id.loseless)     View        loseless;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
      menu.setVisibility(View.GONE);
      menu_btn.setVisibility(View.GONE);
      index.setVisibility(View.VISIBLE);
    }
  }
}

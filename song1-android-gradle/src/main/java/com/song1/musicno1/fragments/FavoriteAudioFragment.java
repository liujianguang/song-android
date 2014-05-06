package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.DataAdapter;
import com.song1.musicno1.fragments.base.ListFragment;
import com.song1.musicno1.helpers.List8;
import com.song1.musicno1.models.Favorite;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Players;
import com.song1.musicno1.models.play.Playlist;

import java.util.List;

/**
 * Created by windless on 14-4-10.
 */
public class FavoriteAudioFragment extends ListFragment<Audio> implements AdapterView.OnItemClickListener {
  private Favorite favorite;


  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (favorite != null) {
      setTitle(favorite.name);
    }
    getListView().setOnItemClickListener(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = super.onCreateView(inflater,container,savedInstanceState);
    view.findViewById(R.id.underLine).setVisibility(View.VISIBLE);
    return view;
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
  public void onResume() {
    super.onResume();
    reload();
  }

  @Override
  protected DataAdapter<Audio> newAdapter() {
    return new DataAdapter<Audio>(getActivity()) {
      @Override
      public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
          view = View.inflate(getActivity(), R.layout.item_text, null);
          holder = new ViewHolder(view);
          view.setTag(holder);
        } else {
          holder = (ViewHolder) view.getTag();
        }

        Audio audio = getDataItem(i);
        holder.title.setText(audio.getTitle());

        return view;
      }
    };
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    Playlist playlist = new Playlist(List8.newList(getDataList()), getDataItem(i));
    Players.setPlaylist(playlist);
  }

  public void setFavorite(Favorite favorite) {
    this.favorite = favorite;
  }

  class ViewHolder {
    @InjectView(R.id.title) TextView title;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}

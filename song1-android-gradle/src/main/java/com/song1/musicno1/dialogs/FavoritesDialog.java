package com.song1.musicno1.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.activeandroid.query.Select;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.DataAdapter;
import com.song1.musicno1.models.Favorite;
import com.song1.musicno1.models.FavoriteAudio;
import com.song1.musicno1.models.play.Audio;

import java.util.List;

/**
 * Created by windless on 14-4-11.
 */
public class FavoritesDialog extends DialogFragment implements AdapterView.OnItemClickListener {
  @InjectView(R.id.list) ListView listView;

  protected DataAdapter<Favorite> adapter;
  private   Audio                 addingAudio;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_favorites, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    listView.setAdapter(newAdapter());
    listView.setOnItemClickListener(this);
    loadFavorite();
  }

  private void loadFavorite() {
    adapter.setDataList(Favorite.getAll());
    adapter.notifyDataSetChanged();
  }

  private ListAdapter newAdapter() {
    adapter = new DataAdapter<Favorite>(getActivity()) {
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

        Favorite favorite = getDataItem(i);
        holder.title.setText(favorite.name);
        return view;
      }
    };
    return adapter;
  }

  public void setAddingAudio(Audio addingAudio) {
    this.addingAudio = addingAudio;
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    Favorite favorite = adapter.getDataItem(i);
    if (favorite.isContain(addingAudio)) {
      Toast.makeText(getActivity(),
          String.format(getString(R.string.favorite_contains_audio), addingAudio.getTitle(), favoriteName(favorite)),
          Toast.LENGTH_SHORT).show();
    } else {
      favorite.add(addingAudio);
      Toast.makeText(getActivity(),
          String.format(getString(R.string.add_to_favorite), addingAudio.getTitle(), favoriteName(favorite)),
          Toast.LENGTH_SHORT)
          .show();
    }
    dismiss();
  }

  private String favoriteName(Favorite favorite) {
    if (favorite.getId() == 1) {
      return getString(R.string.red_heart);
    } else {
      return favorite.name;
    }
  }

  class ViewHolder {
    @InjectView(R.id.title) TextView title;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }

  @OnClick(R.id.create_new)
  public void createNewFavorite() {
    InputDialog.openWithTitle(getString(R.string.please_input_favorite_name))
        .onConfirmed((name) -> {
          Favorite favorite = Favorite.create(name);
          FavoriteAudio favoriteAudio = new FavoriteAudio(addingAudio);
          favoriteAudio.favorite = favorite;
          favoriteAudio.save();
          Toast.makeText(getActivity(),
              String.format(getString(R.string.add_to_favorite), addingAudio.getTitle(), favorite.name),
              Toast.LENGTH_SHORT)
              .show();
          dismiss();
        }).show(getFragmentManager(), "");
  }
  @OnClick(R.id.cancel)
  public void cancelClick(){
      dismiss();
  }
}

package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.activeandroid.query.Select;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.adapter.DataAdapter;
import com.song1.musicno1.dialogs.InputDialog;
import com.song1.musicno1.fragments.base.DataFragment;
import com.song1.musicno1.models.Favorite;

import java.util.List;

/**
 * Created by windless on 14-4-9.
 */
public class FavoritesFragment extends DataFragment<Favorite> implements AdapterView.OnItemClickListener {
  @InjectView(R.id.list) ListView listView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_favorites, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setTitle(getString(R.string.favorite));
    listView.setAdapter(getAdapter());
    listView.setOnItemClickListener(this);
    setHasOptionsMenu(true);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.favorites, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.add:
        createFavorite();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void createFavorite() {
    InputDialog.openWithTitle(getString(R.string.please_input_favorite_name))
        .onConfirmed((name) -> {
          Favorite.create(name);
          reload();
        }).show(getFragmentManager(), "");
  }

  @Override
  protected List<Favorite> onLoad(int loadPage) {
    return new Select().from(Favorite.class).execute();
  }

  @Override
  protected DataAdapter<Favorite> newAdapter() {
    return new DataAdapter<Favorite>(getActivity()) {
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
        if (favorite.getId() == 1) {
          holder.title.setText(R.string.red_heart);
        } else {
          holder.title.setText(favorite.name);
        }
        return view;
      }
    };
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    FavoriteAudioFragment fragment = new FavoriteAudioFragment();
    if (i == 0) {
      fragment.setTitle(getString(R.string.red_heart));
    } else {
      fragment.setTitle(getDataItem(i).name);
    }
    fragment.setFavorite(getDataItem(i));
    MainActivity activity = (MainActivity) getActivity();
    activity.push(FavoriteAudioFragment.class.getName(), fragment);
  }

  class ViewHolder {
    @InjectView(R.id.title) TextView title;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}

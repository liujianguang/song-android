package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.activeandroid.query.Select;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.adapter.DataAdapter;
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
    if (i == 0) { // 我喜欢
      RedheartFragment fragment = new RedheartFragment();
      MainActivity activity = (MainActivity) getActivity();
      activity.push(RedheartFragment.class.getName(), fragment);
    }
  }

  class ViewHolder {
    @InjectView(R.id.title) TextView title;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}

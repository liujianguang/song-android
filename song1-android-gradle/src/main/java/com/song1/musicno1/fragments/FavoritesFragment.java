package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.view.*;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.common.collect.Maps;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.adapter.DataAdapter;
import com.song1.musicno1.dialogs.InputDialog;
import com.song1.musicno1.fragments.base.DataFragment;
import com.song1.musicno1.helpers.ActiveHelper;
import com.song1.musicno1.models.Favorite;

import java.util.List;
import java.util.Map;

/**
 * Created by windless on 14-4-9.
 */
public class FavoritesFragment extends DataFragment<Favorite> implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
  @InjectView(R.id.list) ListView listView;

  Map<Integer, Favorite> selectedItem = Maps.newHashMap();

  ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
      actionMode.getMenuInflater().inflate(R.menu.favorite_context, menu);
      return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
      return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
      switch (menuItem.getItemId()) {
        case R.id.delete:
          ActiveHelper.transition(() -> {
            for (Favorite favorite : selectedItem.values()) {
              favorite.destroy();
            }
          });
          actionMode.finish();
          reload();
          return true;
      }
      return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
      FavoritesFragment.this.actionMode = null;
      selectedItem.clear();
      getAdapter().notifyDataSetChanged();
      MainActivity activity = (MainActivity) getActivity();
      activity.showPlayBar();
    }
  };
  protected ActionMode actionMode;

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
    listView.setOnItemLongClickListener(this);
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
    List<Favorite> all = Favorite.getAll();
    return all;
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

        holder.checkBox.setTag(i);

        holder.checkBox.setChecked(selectedItem.get(i) != null);

        if (actionMode != null) {
          holder.checkBox.setVisibility(View.VISIBLE);
        } else {
          holder.checkBox.setVisibility(View.GONE);
        }
        return view;
      }
    };
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    if (actionMode != null) {
      ViewHolder holder = (ViewHolder) view.getTag();
      holder.checkBox.setChecked(!holder.checkBox.isChecked());
      return;
    }

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

  @Override
  public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
    if (actionMode != null) {
      return false;
    }

    MainActivity activity = (MainActivity) getActivity();
    activity.hidePlayBar();

    actionMode = startActionMode(actionModeCallback);
    actionMode.setTitle(getString(R.string.selected_items, selectedItem.size()));
    selectedItem.put(i, getDataItem(i));
    getAdapter().notifyDataSetChanged();
    return true;
  }

  class ViewHolder implements CompoundButton.OnCheckedChangeListener {
    @InjectView(R.id.title)    TextView title;
    @InjectView(R.id.checkbox) CheckBox checkBox;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
      checkBox.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
      int index = (int) buttonView.getTag();
      if (isChecked) {
        selectedItem.put(index, getDataItem(index));
      } else {
        selectedItem.remove(index);
      }
      if (actionMode != null) {
        actionMode.setTitle(getString(R.string.selected_items, selectedItem.size()));
      }
    }
  }
}

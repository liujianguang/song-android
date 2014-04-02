package com.song1.musicno1.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.common.base.Strings;
import com.song1.musicno1.R;
import com.song1.musicno1.entity.Album;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;
import java.util.List;

/**
 * User: windless
 * Date: 13-9-3
 * Time: PM5:10
 */
public class LocalAlbumAdapter extends BaseAdapter {
  private final Context _context;

  private List<Album> albums;

  @Inject
  public LocalAlbumAdapter(Context context) {
    _context = context;
  }

  @Override
  public int getCount() {
    return albums == null ? 0 : albums.size();
  }

  @Override
  public Object getItem(int position) {
    return albums.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View view, ViewGroup parent) {
    ViewHolder holder;
    if (view == null) {
      view = View.inflate(_context, R.layout.local_album_item, null);
      holder = new ViewHolder(view);
      view.setTag(holder);
    } else {
      holder = (ViewHolder) view.getTag();
    }
    Album album = albums.get(position);
    if ("<unknown>".equals(album.title)) {
      holder.title.setText(R.string.unknown);
    } else {
      holder.title.setText(album.title);
    }

    if (Strings.isNullOrEmpty(album.album_art)) {
      Picasso.with(_context).load(R.drawable.album_art_default_small).into(holder.image);
    } else {
      Picasso.with(_context).load(album.album_art).into(holder.image);
    }
    return view;
  }

  public void albums(List<Album> data) {
    albums = data;
    notifyDataSetChanged();
  }

  public void add(List<Album> albums) {
    if (this.albums == null) {
      this.albums = albums;
    } else {
      this.albums.addAll(albums);
    }
    notifyDataSetChanged();
  }

  class ViewHolder {
    @InjectView(R.id.title) TextView title;
    @InjectView(R.id.image) ImageView image;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}

package com.song1.musicno1.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;
import com.song1.musicno1.entity.Artist;
import com.song1.musicno1.helpers.ViewHelper;
import java.util.List;

/**
 * User: windless
 * Date: 13-9-3
 * Time: PM8:22
 */
public class LocalArtistAdapter extends BaseAdapter {
  private final Context context;

  private List<Artist> artists;

  public LocalArtistAdapter(Context context) {
    this.context = context;
  }

  public void artists(List<Artist> artists) {
    this.artists = artists;
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return artists == null ? 0 : artists.size();
  }

  @Override
  public Object getItem(int position) {
    return artists.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View view, ViewGroup parent) {
    ViewHolder holder;

    if (view == null) {
      view = View.inflate(context, R.layout.local_artist_list_item, null);
      holder = new ViewHolder(view);
      view.setTag(holder);
    } else {
      holder = (ViewHolder) view.getTag();
    }

    Artist artist = artists.get(position);

//    ViewHelper.set_title(context, holder.title, artist.name);
    holder.title.setText(artist.name);
    return view;
  }

  class ViewHolder {
    @InjectView(R.id.title) TextView title;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}

package com.song1.musicno1.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;
import com.song1.musicno1.entity.Artist;

/**
 * User: windless
 * Date: 13-9-3
 * Time: PM8:22
 */
public class LocalArtistAdapter extends DataAdapter<Artist> {

  public LocalArtistAdapter(Context context) {
    super(context);
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

    Artist artist = getDataItem(position);

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

package com.song1.musicno1.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.common.base.Strings;
import com.song1.musicno1.R;
import com.song1.musicno1.entity.Album;

import java.io.File;

/**
 * User: windless
 * Date: 13-12-10
 * Time: PM6:18
 */
public class AlbumAdapter extends DataAdapter<Album> {
  public AlbumAdapter(Context context) {
    super(context);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      convertView = View.inflate(context, R.layout.local_album_list_item, null);
      holder = new ViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    Album album = getDataItem(position);
    if ("<unknown>".equals(album.title)) {
      holder.title.setText(R.string.unknown);
    } else {
      holder.title.setText(album.title);
    }

    if (Strings.isNullOrEmpty(album.album_art)) {
//      Picasso.with(context).load(R.drawable.album_art_default_big).into(holder.image);
      holder.image.setImageResource(R.drawable.album_art_default_big);
    } else {
//      Picasso.with(context).load(new File(album.album_art)).into(holder.image);
      holder.image.setImageURI(Uri.fromFile(new File(album.album_art)));
//      holder.image.setImageResource(R.drawable.album_art_default_big);
    }

    return convertView;
  }

  class ViewHolder {
    @InjectView(R.id.image) ImageView image;
    @InjectView(R.id.title) TextView  title;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}

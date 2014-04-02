package com.song1.musicno1.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.song1.musicno1.R;
import com.song1.musicno1.entity.AlbumInfo;
import com.squareup.picasso.Picasso;

/**
 * User: windless
 * Date: 13-12-4
 * Time: PM4:59
 */
public class MiguAlbumAdapter extends YYAdapter<AlbumInfo> {
  public MiguAlbumAdapter(Context context) {
    super(context);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      convertView = View.inflate(context, R.layout.migu_album_list_item, null);
      holder = new ViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    AlbumInfo info = getDataItem(position);
    holder.title.setText(info.name);
    if (info.img != null && info.img.length() > 0) {
      Picasso.with(context).load(info.img).into(holder.image);
    } else {
      Picasso.with(context).load(R.drawable.album_art_default_small).into(holder.image);
    }

    return convertView;
  }
}

package com.song1.musicno1.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.song1.musicno1.R;
import com.song1.musicno1.entity.SubjectInfo;
import com.squareup.picasso.Picasso;

/**
 * User: windless
 * Date: 13-12-4
 * Time: PM4:29
 */
public class MiguSongListAdapter extends YYAdapter<SubjectInfo> {
  public MiguSongListAdapter(Context context) {
    super(context);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      convertView = View.inflate(context, R.layout.migu_playlist_list_item, null);
      holder = new ViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    SubjectInfo dataItem = getDataItem(position);
    holder.title.setText(dataItem.name);
    holder.subtitle.setText(dataItem.remark);

    if (dataItem.img != null && dataItem.img.length() > 0) {
      Picasso.with(context).load(dataItem.img).into(holder.image);
    } else {
      Picasso.with(context).load(R.drawable.album_art_default_big).into(holder.image);
    }

    return convertView;
  }
}

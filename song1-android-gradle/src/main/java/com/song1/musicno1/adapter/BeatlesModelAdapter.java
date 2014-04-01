package com.song1.musicno1.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.common.base.Strings;
import com.song1.musicno1.R;
import com.song1.musicno1.entity.BeatlesModel;
import com.squareup.picasso.Picasso;

/**
 * User: windless
 * Date: 13-12-12
 * Time: PM3:41
 */
public class BeatlesModelAdapter extends YYAdapter<BeatlesModel> {
  public BeatlesModelAdapter(Context context) {
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

    BeatlesModel model = getDataItem(position);
    holder.title.setText(model.title);
    if (Strings.isNullOrEmpty(model.albumArt)) {
      Picasso.with(context).load(R.drawable.album_art_default_small).into(holder.image);
    } else {
      Picasso.with(context).load(model.albumArt).into(holder.image);
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

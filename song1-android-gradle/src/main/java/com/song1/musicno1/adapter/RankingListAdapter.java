package com.song1.musicno1.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;
import com.song1.musicno1.entity.RankingListInfo;
import com.squareup.picasso.Picasso;

/**
 * User: windless
 * Date: 13-12-4
 * Time: PM3:49
 */
public class RankingListAdapter extends DataAdapter<RankingListInfo> {

  public RankingListAdapter(Context context) {
    super(context);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;

    if (convertView == null) {
      convertView = View.inflate(context, R.layout.migu_ranking_list_item, null);
      holder = new ViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    RankingListInfo info = getDataItem(position);
    holder.title.setText(info.name);
    holder.des.setText(info.remark);
    if (info.img != null && info.img.length() > 0) {
      Picasso.with(context).load(info.img).into(holder.image);
    } else {
      Picasso.with(context).load(R.drawable.album_art_default_small).into(holder.image);
    }
    return convertView;
  }

  class ViewHolder {
    @InjectView(R.id.ranking_item_text)  TextView  title;
    @InjectView(R.id.ranking_item_desc)  TextView  des;
    @InjectView(R.id.ranking_item_image) ImageView image;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}

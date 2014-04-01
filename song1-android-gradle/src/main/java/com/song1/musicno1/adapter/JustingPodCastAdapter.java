package com.song1.musicno1.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;
import com.song1.musicno1.entity.JustingPodCast;
import com.squareup.picasso.Picasso;

/**
 * User: windless
 * Date: 13-12-12
 * Time: PM3:05
 */
public class JustingPodCastAdapter extends YYAdapter<JustingPodCast> {

  public JustingPodCastAdapter(Context context) {
    super(context);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      convertView = View.inflate(context, R.layout.item_justing_pod_cast, null);
      holder = new ViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    JustingPodCast podCast = getDataItem(position);
    holder.title.setText(podCast.title);
    holder.des.setText(podCast.description);
    Picasso.with(context).load(podCast.image).into(holder.image);
    return convertView;
  }

  class ViewHolder {
    @InjectView(R.id.title) TextView  title;
    @InjectView(R.id.des)   TextView  des;
    @InjectView(R.id.image) ImageView image;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}

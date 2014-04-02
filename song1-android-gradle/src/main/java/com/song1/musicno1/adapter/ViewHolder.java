package com.song1.musicno1.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;

/**
 * User: windless
 * Date: 13-12-4
 * Time: PM4:32
 */
public class ViewHolder {
  @InjectView(R.id.image)
  public ImageView image;
  @InjectView(R.id.title)
  public TextView  title;
  @InjectView(R.id.subtitle)
  public TextView  subtitle;

  public ViewHolder(View view) {
    ButterKnife.inject(this, view);
  }
}

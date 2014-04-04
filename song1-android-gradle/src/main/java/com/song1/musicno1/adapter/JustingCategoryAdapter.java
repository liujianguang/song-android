package com.song1.musicno1.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;
import com.song1.musicno1.entity.JustingCategory;

/**
 * User: windless
 * Date: 13-12-12
 * Time: PM2:35
 */
public class JustingCategoryAdapter extends DataAdapter<JustingCategory> {
  public JustingCategoryAdapter(Context context) {
    super(context);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      convertView = View.inflate(context, R.layout.item_justing_category, null);
      holder = new ViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    JustingCategory category = getDataItem(position);
    holder.title.setText(category.title);

    return convertView;
  }

  class ViewHolder {
    @InjectView(R.id.title) TextView title;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}

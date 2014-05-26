package com.song1.musicno1.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;
import com.song1.musicno1.models.play.MediaServer;

import java.util.List;

/**
 * User: windless
 * Date: 14-2-6
 * Time: PM4:15
 */
public class NavigationAdapter extends BaseAdapter {
  private final int VIEW_TYPE_TITLE = 0;
  private final int VIEW_TYPE_ITEM  = 1;

  private   List<Object>      items;
  private   Context           context;
  private   List<Object>      channels;
  protected List<MediaServer> serverList;
  private   Object            currentChannel;

  public NavigationAdapter(Context context) {
    this.context = context;
  }

  public void setChannels(List<Object> channels) {
    this.channels = channels;
    update();
  }

  private void update() {
    items = Lists.newArrayList();
    items.addAll(channels);
    if (serverList != null && serverList.size() > 0) {
      items.add(context.getString(R.string.shared_device));
      items.addAll(serverList);
    }

    items.add("");
    items.add(R.string.exit);
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return items == null ? 0 : items.size();
  }

  @Override
  public Object getItem(int position) {
    return items.get(position);
  }

  @Override
  public int getViewTypeCount() {
    return 2;
  }

  @Override
  public int getItemViewType(int position) {
    Object item = items.get(position);
    if (item instanceof String) {
      return VIEW_TYPE_TITLE;
    }
    return VIEW_TYPE_ITEM;
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;

    if (convertView == null) {
      if (getItemViewType(position) == VIEW_TYPE_TITLE) {
        convertView = View.inflate(context, R.layout.item_navigation_title, null);
      } else {
        convertView = View.inflate(context, R.layout.item_navigation_item, null);
      }
      holder = new ViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    Object item = items.get(position);
    if (item instanceof String) {
      String title = (String) item;
      holder.title.setText(title);
    } else if (item instanceof Integer) {
      Integer stringId = (Integer) item;
      holder.title.setText(stringId);
    } else if (item instanceof MediaServer) {
      MediaServer device = (MediaServer) item;
      holder.title.setText(device.getName());
    }

    if (holder.flagView != null) {
      if (currentChannel == item && position != getCount() - 1) {
        holder.flagView.setVisibility(View.VISIBLE);
      } else {
        holder.flagView.setVisibility(View.INVISIBLE);
      }
    }
    return convertView;
  }

  @Override
  public boolean isEnabled(int position) {
    return getItemViewType(position) == VIEW_TYPE_ITEM;
  }

  public void setMediaServers(List<MediaServer> serverList) {
    this.serverList = serverList;
    update();
  }

  public void setCurrentChannel(Object currentChannel) {
    this.currentChannel = currentChannel;
  }

  class ViewHolder {
    @InjectView(R.id.title) TextView  title;
    @Optional
    @InjectView(R.id.flag)  ImageView flagView;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}

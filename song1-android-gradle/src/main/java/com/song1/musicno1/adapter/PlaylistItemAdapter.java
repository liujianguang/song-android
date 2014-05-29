package com.song1.musicno1.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;
import com.song1.musicno1.models.play.Audio;

/**
 * Created by windless on 14-5-29.
 */
public class PlaylistItemAdapter extends DataAdapter<Audio> {
  protected Audio playingAudio;

  public PlaylistItemAdapter(Context context) {
    super(context);
  }

  @Override
  public View getView(int i, View view, ViewGroup viewGroup) {
    ViewHolder holder;
    if (view == null) {
      view = View.inflate(context, R.layout.item_playlist_item, null);
      holder = new ViewHolder(view);
      view.setTag(holder);
    } else {
      holder = (ViewHolder) view.getTag();
    }

    Audio audio = getDataItem(i);
    if (audio == playingAudio) {
      holder.playingFlag.setVisibility(View.VISIBLE);
    } else {
      holder.playingFlag.setVisibility(View.INVISIBLE);
    }
    holder.title.setText(audio.getTitle());

    return view;
  }

  public void setPlayingAudio(Audio audio) {
    playingAudio = audio;
    notifyDataSetChanged();
  }

  class ViewHolder {
    @InjectView(R.id.playing_flag) ImageView playingFlag;
    @InjectView(R.id.title)        TextView  title;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}

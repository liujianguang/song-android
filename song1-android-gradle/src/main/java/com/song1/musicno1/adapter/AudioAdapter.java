package com.song1.musicno1.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.song1.musicno1.R;
import com.song1.musicno1.models.FavoriteAudio;
import com.song1.musicno1.models.play.Audio;

/**
 * Created by windless on 14-4-10.
 */
public class AudioAdapter extends DataAdapter<Audio> {
  protected Audio selectedAudio;

  public AudioAdapter(Context context) {
    super(context);
  }

  @Override
  public View getView(int i, View view, ViewGroup viewGroup) {
    ViewHolder holder;
    if (view == null) {
      view = View.inflate(context, R.layout.item_audio, null);
      holder = new ViewHolder(view);
      view.setTag(holder);
    } else {
      holder = (ViewHolder) view.getTag();
    }
    Audio audio = getDataItem(i);
    holder.menuBtn.setTag(audio);
    holder.redHeartBtn.setTag(audio);
    holder.title.setText(audio.getTitle());

    if (selectedAudio == audio) {
      holder.menu.setVisibility(View.VISIBLE);
      if (FavoriteAudio.isFavorite(audio)) {
        holder.redHeartBtn.setBackgroundResource(R.color.red);
      } else {
        holder.redHeartBtn.setBackgroundResource(android.R.color.transparent);
      }
    } else {
      holder.menu.setVisibility(View.GONE);
    }
    return view;
  }

  class ViewHolder {
    @InjectView(R.id.title)     TextView    title;
    @InjectView(R.id.menu)      View        menu;
    @InjectView(R.id.menu_btn)  ImageButton menuBtn;
    @InjectView(R.id.red_heart) Button      redHeartBtn;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    @OnClick(R.id.menu_btn)
    public void openMenu(View btn) {
      Audio tagAudio = (Audio) btn.getTag();
      if (selectedAudio == tagAudio) {
        selectedAudio = null;
      } else {
        selectedAudio = tagAudio;
      }
      notifyDataSetChanged();
    }

    @OnClick(R.id.red_heart)
    public void onRedHeartClick(View view) {
      Audio audio = (Audio) view.getTag();
      FavoriteAudio.toggleRedHeart(audio);
      notifyDataSetChanged();
    }
  }
}

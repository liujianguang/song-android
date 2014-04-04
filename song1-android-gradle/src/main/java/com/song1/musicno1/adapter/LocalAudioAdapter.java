package com.song1.musicno1.adapter;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.song1.musicno1.R;
import com.song1.musicno1.models.play.Audio;

/**
 * User: windless
 * Date: 13-8-29
 * Time: PM9:04
 */
public class LocalAudioAdapter extends DataAdapter<Audio> {

  public LocalAudioAdapter(Context context) {
    super(context);
  }

  @Override
  public View getView(int position, View view, ViewGroup parent) {
    ViewHolder holder;

    if (view == null) {
      view = View.inflate(context, R.layout.local_audio_list_item, null);
      holder = new ViewHolder(view);
      view.setTag(holder);
    } else {
      holder = (ViewHolder) view.getTag();
    }

    Audio audio = getDataItem(position);

    holder.title.setText(audio.getTitle());
    holder.more.setTag(audio);
    holder.subtitle.setText(audio.getArtist());
    return view;
  }

  class ViewHolder implements PopupMenu.OnMenuItemClickListener {
    @InjectView(R.id.title)    TextView    title;
    @InjectView(R.id.subtitle) TextView    subtitle;
    @InjectView(R.id.more)     ImageButton more;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    @OnClick(R.id.more)
    public void on_more_click(View view) {
//      PopupMenu menu = new PopupMenu(activity, view);
//      menu.inflate(R.menu.local_audio_action);
//      menu.setOnMenuItemClickListener(this);
//      menu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
//      switch (item.getItemId()) {
//        case R.id.add_to_playlist:
//          if (listener != null) listener.add_to_playlist((Audio) more.getTag());
//          break;
//      }
      return true;
    }
  }
}

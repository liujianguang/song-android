package com.song1.musicno1.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.song1.musicno1.R;
import com.song1.musicno1.helpers.ViewHelper;
import com.song1.musicno1.models.play.Audio;

import java.util.List;

/**
 * User: windless
 * Date: 13-8-29
 * Time: PM9:04
 */
public class LocalAudioAdapter extends BaseAdapter {
  public interface Listener {
    void add_to_playlist(Audio audio);
  }

  private final Context context;

  private List<Audio> audios;
  private Activity    activity;
  private Listener    listener;

  public LocalAudioAdapter(Context context) {
    this.context = context;
  }

  public void listener(Listener listener) {
    this.listener = listener;
  }

  public void activity(Activity activity) {
    this.activity = activity;
  }

  @Override
  public int getCount() {
    return audios == null ? 0 : audios.size();
  }

  @Override
  public Object getItem(int position) {
    return audios.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
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

    Audio audio = audios.get(position);

    holder.title.setText(audio.getTitle());
    holder.more.setTag(audio);
    holder.subtitle.setText(audio.getArtist());
//    ViewHelper.set_title(context, holder.subtitle, audio.subtitle());
    return view;
  }

  public void setData(List<Audio> data) {
    audios = data;
    notifyDataSetChanged();
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

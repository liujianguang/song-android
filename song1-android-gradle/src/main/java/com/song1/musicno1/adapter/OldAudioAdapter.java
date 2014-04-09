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
import com.song1.musicno1.models.play.Audio;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * User: windless
 * Date: 13-9-6
 * Time: PM2:25
 */
public class OldAudioAdapter extends BaseAdapter {


  public void clear() {
    audios.clear();
    notifyDataSetChanged();
  }

  public interface MoreMenuListener {
    void on_menu_item_click(int menu_item_id, Audio audio);
  }

  private final Context context;

  private List<Audio> audios = new ArrayList<Audio>();

  private Activity         activity;
  private int              menu_id;
  private MoreMenuListener listener;

  @Inject
  public OldAudioAdapter(Context context) {
    this.context = context;
  }

  public void add_audios(List<Audio> audios) {
    this.audios.addAll(audios);
    notifyDataSetChanged();
  }

  public void activity(Activity activity) {
    this.activity = activity;
  }

  public void drop_down(int menu_id) {
    this.menu_id = menu_id;
  }

  public void listen_menu_item_click(MoreMenuListener listener) {
    this.listener = listener;
  }
  public List<Audio> getAudiosList()
  {
    return  this.audios;
  }

  @Override
  public int getCount() {
    return audios.size();
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
    holder.subtitle.setText(audio.getArtist());
    holder.more.setTag(audio);

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
    public void no_more_click(View view) {
      PopupMenu menu = new PopupMenu(activity, view);
      menu.inflate(menu_id);
      menu.setOnMenuItemClickListener(this);
      menu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
      if (listener != null) {
        listener.on_menu_item_click(item.getItemId(), (Audio) more.getTag());
      }
      return true;
    }
  }
}

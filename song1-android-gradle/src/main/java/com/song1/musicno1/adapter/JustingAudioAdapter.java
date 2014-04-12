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
import com.song1.musicno1.entity.JustingAudio;
import com.song1.musicno1.models.DownLoadManager;
import com.song1.musicno1.util.ToastUtil;

/**
 * User: windless
 * Date: 13-12-13
 * Time: PM3:37
 */
public class JustingAudioAdapter extends YYAdapter<JustingAudio> {

  public JustingAudioAdapter(Context context) {
    super(context);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      convertView = View.inflate(context, R.layout.item_justing_audio, null);
      holder = new ViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    JustingAudio audio = getDataItem(position);
    holder.title.setText(audio.title);
    holder.author.setText(context.getString(R.string.author, audio.author));
    holder.actor.setText(context.getString(R.string.actor, audio.actor));
    holder.more.setTag(audio);
    return convertView;
  }

  class ViewHolder implements PopupMenu.OnMenuItemClickListener {
    @InjectView(R.id.title)  TextView    title;
    @InjectView(R.id.author) TextView    author;
    @InjectView(R.id.actor)  TextView    actor;
    @InjectView(R.id.more)   ImageButton more;

    private JustingAudio audio;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    @OnClick(R.id.more)
    public void onMoreClick(View view) {
      audio = (JustingAudio) view.getTag();
      PopupMenu popupMenu = new PopupMenu(context, view);
      popupMenu.inflate(R.menu.migu_audio_action);
      popupMenu.setOnMenuItemClickListener(this);
      popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
      switch (item.getItemId()) {
//        case R.id.add_to_playlist:
//          playerAction.add_to_playlist(audio.toAudio());
//          break;
        case R.id.download:
          try {
//            DownloadManager.Request request = downloadStore.newRequest(Uri.parse(audio.toAudio().remote_play_uri), audio.title);
//            downloadStore.start(context, request, audio.title);
            System.out.println(audio.url);
            System.out.println(audio.toAudio().getRemotePlayUrl());
            //ToastUtil.show(context,audio.title + "," + audio.toAudio().getRemotePlayUrl());
            ToastUtil.show(context,audio.title);
            DownLoadManager downLoadManager = DownLoadManager.getDownLoadManager(context);
            DownLoadManager.Task task = downLoadManager.newTask(audio.title, audio.toAudio().getRemotePlayUrl());
            downLoadManager.startTask(task);
          } catch (IllegalStateException e) {
//            Toast.makeText(context, context.getString(R.string.download_failed_no_sdcard, audio.title), Toast.LENGTH_LONG).show();
          }
          break;
      }
      return true;
    }
  }
}

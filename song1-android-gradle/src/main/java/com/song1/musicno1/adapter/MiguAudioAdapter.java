package com.song1.musicno1.adapter;

import android.app.Activity;
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
import com.song1.musicno1.entity.SongInfo;
import com.song1.musicno1.models.DownLoadManager;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.util.ToastUtil;

import javax.inject.Inject;

/**
 * User: windless
 * Date: 13-9-5
 * Time: PM10:24
 */
public class MiguAudioAdapter extends DataAdapter<Audio> {

  protected Activity activity;

  @Inject
  public MiguAudioAdapter(Context context) {
    super(context);
  }

  public void setActivity(Activity activity) {
    this.activity = activity;
  }

  @Override
  public View getView(int position, View view, ViewGroup parent) {
    ViewHolder holder;
    if (view == null) {
      view = View.inflate(context, R.layout.item_migu_audio, null);
      holder = new ViewHolder(view);
      view.setTag(holder);
    } else {
      holder = (ViewHolder) view.getTag();
    }

    Audio audio = getDataItem(position);
    holder.title.setText(audio.getTitle());
    holder.subtitle.setText(audio.getArtist());
    holder.more.setTag(audio);
    return view;
  }

  class ViewHolder implements PopupMenu.OnMenuItemClickListener {
    @InjectView(R.id.title)    TextView    title;
    @InjectView(R.id.subtitle) TextView    subtitle;
    @InjectView(R.id.more)     ImageButton more;

    private Audio audio;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    @OnClick(R.id.more)
    public void onMoreClick(View view) {
      audio = (Audio) view.getTag();
      PopupMenu popupMenu = new PopupMenu(activity, view);
      popupMenu.inflate(R.menu.migu_audio_action);
      popupMenu.setOnMenuItemClickListener(this);
      popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
      switch (item.getItemId()) {
        case R.id.add_to_playlist:
          //playerAction.add_to_playlist(info.toAudio());
          break;
        case R.id.download:
          if (audio.getFrom() == Audio.MIGU) {
//            musicStore.download_url(activity, info.toAudio(), new GetDownloadUrlCallback() {
//              @Override
//              public void OnUrlGot(String code, String msg, String url) {
//                if (url != null) {
//                  download(url, info.musicName);
//                }
//              }
//            });
          } else {
//            download(info.toAudio().remote_play_uri, info.musicName);
            ToastUtil.show(context,audio.getTitle());
            DownLoadManager downLoadManager = DownLoadManager.getDownLoadManager(context);
            DownLoadManager.Task task = downLoadManager.newTask(audio.getTitle(),audio.getRemotePlayUrl());
            downLoadManager.startTask(task);
          }
      }
      return true;
    }

    private void download(String url, String name) {
//      try {
//        DownloadManager.Request request = downloadStore.newRequest(Uri.parse(url), name);
//        downloadStore.start(context, request, name);
//      } catch (IllegalStateException e) {
//        Toast.makeText(context, context.getString(R.string.download_failed_no_sdcard, name), Toast.LENGTH_LONG).show();
//      }
    }
  }
}

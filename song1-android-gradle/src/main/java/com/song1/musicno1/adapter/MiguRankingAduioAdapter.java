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
import com.song1.musicno1.entity.SongInfo;
import com.song1.musicno1.models.play.Audio;

import javax.inject.Inject;

/**
 * User: windless
 * Date: 13-12-9
 * Time: AM11:42
 */
public class MiguRankingAduioAdapter extends MiguAudioAdapter {
  @Inject
  public MiguRankingAduioAdapter(Context context) {
    super(context);
  }

  @Override
  public View getView(int position, View view, ViewGroup parent) {
    ViewHolder holder;
    if (view == null) {
      view = View.inflate(context, R.layout.item_migu_ranking_audio, null);
      holder = new ViewHolder(view);
      view.setTag(holder);
    } else {
      holder = (ViewHolder) view.getTag();
    }

    Audio audio = getDataItem(position);
    holder.number.setText("" + (position + 1));
    holder.title.setText(audio.getTitle());
    holder.subtitle.setText(audio.getArtist());
    holder.more.setTag(audio);

    return view;
  }

  class ViewHolder implements PopupMenu.OnMenuItemClickListener {
    @InjectView(R.id.title)    TextView    title;
    @InjectView(R.id.subtitle) TextView    subtitle;
    @InjectView(R.id.more)     ImageButton more;
    @InjectView(R.id.number)   TextView    number;

    private SongInfo info;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    @OnClick(R.id.more)
    public void onMoreClick(View view) {
      info = (SongInfo) view.getTag();
      PopupMenu popupMenu = new PopupMenu(activity, view);
      popupMenu.inflate(R.menu.migu_audio_action);
      popupMenu.setOnMenuItemClickListener(this);
      popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
      switch (item.getItemId()) {
        case R.id.add_to_playlist:
//          playerAction.add_to_playlist(info.toAudio());
          break;
//        case R.id.download:
//          musicStore.downlo ad_url(activity, info.toAudio(), new GetDownloadUrlCallback() {
//            @Override
//            public void OnUrlGot(String code, String msg, String url) {
//              if (url != null) {
//                try {
//                  DownloadManager.Request request = downloadStore.newRequest(Uri.parse(url), info.musicName);
//                  downloadStore.start(context, request, info.musicName);
//                } catch (IllegalStateException e) {
//                  Toast.makeText(context, context.getString(R.string.download_failed_no_sdcard, info.musicName), Toast.LENGTH_LONG).show();
//                }
//              }
//            }
//          });
      }
      return true;
    }
  }
}

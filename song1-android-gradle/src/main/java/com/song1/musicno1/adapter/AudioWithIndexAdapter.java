package com.song1.musicno1.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.song1.musicno1.R;
import com.song1.musicno1.controllers.AddFavoritesController;
import com.song1.musicno1.entity.AudioGroup;
import com.song1.musicno1.helpers.TimeHelper;
import com.song1.musicno1.models.FavoriteAudio;
import com.song1.musicno1.models.LocalAudioStore;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.util.Global;

import java.util.List;
import java.util.Map;

/**
 * Created by windless on 14-4-10.
 */
public class AudioWithIndexAdapter extends DataAdapter<Audio> {
  private final static int GROUP = 0;
  private final static int AUDIO = 1;

  protected final LocalAudioStore store;

  private Audio selectedAudio;
  private Audio playingAudio;

  public AudioWithIndexAdapter(Context context, LocalAudioStore store) {
    super(context);
    this.store = store;
  }

  private Map<String, Integer> mapGroupPosition = Maps.newHashMap();
  private String               firstGroupName   = null;

  @Override
  public void setDataList(List<Audio> dataList) {
    super.setDataList(dataList);
    for (Audio audio : dataList) {
      if (audio instanceof AudioGroup) {
        mapGroupPosition.put(audio.getTitle(), dataList.indexOf(audio));
        if (firstGroupName == null) {
          firstGroupName = audio.getTitle();
        }
      }
    }
  }

  @Override
  public List<Audio> getDataList() {
    List<Audio> newList = Lists.newArrayList();
    List<Audio> audioList = super.getDataList();
    for (Audio audio : audioList) {
      if (audio instanceof AudioGroup) {
        continue;
      }
      newList.add(audio);
    }
    return newList;
  }

  public Integer getGroupPositionByName(String name) {
    return mapGroupPosition.get(name);
  }

  public void setSelectedAudio(Audio selectedAudio) {
    this.selectedAudio = selectedAudio;
    notifyDataSetChanged();
  }

  public void setPlayingAudio(Audio playingAudio) {
    this.playingAudio = playingAudio;
    notifyDataSetChanged();
  }

  @Override
  public int getViewTypeCount() {
    return 2;
  }

  @Override
  public int getItemViewType(int position) {
    return getDataItem(position) instanceof AudioGroup ? GROUP : AUDIO;
  }

  @Override
  public View getView(int i, View view, ViewGroup viewGroup) {
    ViewHolder holder;
    if (view == null) {
      if (getItemViewType(i) == GROUP) {
        view = View.inflate(context, R.layout.audio_group_title, null);
        holder = new ViewHolder(view);
        view.setTag(holder);
      } else {
        view = View.inflate(context, R.layout.item_audio_without_index, null);
        holder = new ViewHolder(view);
        view.setTag(holder);
      }
    } else {
      holder = (ViewHolder) view.getTag();
    }

    Audio audio = getDataItem(i);

    holder.title.setText(audio.getTitle());
    if (getItemViewType(i) == GROUP) {
      return view;
    }

    if (audio.isEqual(playingAudio)) {
      holder.playingFlag.setVisibility(View.VISIBLE);
    } else {
      holder.playingFlag.setVisibility(View.GONE);
    }

    if (selectedAudio == audio) {
      holder.menu.setVisibility(View.VISIBLE);
      if (FavoriteAudio.isFavorite(audio)) {
        holder.redHeartBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_heart_choose, 0, 0);
      } else {
        holder.redHeartBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_heart_normal, 0, 0);
      }
    } else {
      holder.menu.setVisibility(View.GONE);
    }

    if (audio.isLossless()) {
      holder.loseless.setVisibility(View.VISIBLE);
    } else {
      holder.loseless.setVisibility(View.GONE);
    }

    holder.subtitle.setText(audio.getSubtitle(context));

    holder.menuBtn.setOnClickListener((button) -> toggleMenu(audio));
    holder.addToBtn.setOnClickListener((button) -> addToFavorites(audio));
    holder.redHeartBtn.setOnClickListener((button) -> addToRedHeart(audio));
    holder.infoBtn.setOnClickListener((button) -> showInfo(audio));
    holder.deleteBtn.setOnClickListener((button) -> deleteAudio(audio));

    holder.title.setText(audio.getTitle());
    return view;
  }

  private void deleteAudio(Audio audio) {
    AlertDialog.Builder alert = new AlertDialog.Builder(context);
    alert.setTitle(R.string.notice);
    alert.setMessage(R.string.confirm_delete);
    alert.setPositiveButton(android.R.string.ok, (dialog, whichButton) -> {
      if (store.deleteAudio(audio)) {
        remove(audio);
      } else {
        Toast.makeText(context, R.string.delete_failed, Toast.LENGTH_SHORT).show();
      }
      notifyDataSetChanged();
      dialog.dismiss();
    });
    alert.setNegativeButton(android.R.string.cancel, (dialog, whichButton) -> dialog.dismiss());
    alert.show();
  }

  private void showInfo(Audio audio) {
    AlertDialog.Builder alert = new AlertDialog.Builder(context);
    alert.setTitle(R.string.audio_info);
    StringBuilder sb = new StringBuilder();
    sb.append(context.getString(R.string.songName)).append(audio.getTitle()).append("\r\n");
    sb.append(context.getString(R.string.fileType)).append(audio.getMimeType()).append("\r\n");
    sb.append(context.getString(R.string.fileSize)).append(Global.format(audio.getSize())).append("\r\n");
    sb.append(context.getString(R.string.duration)).append(TimeHelper.milli2str((int) audio.getDuration())).append("\r\n");
    sb.append(context.getString(R.string.path)).append(audio.getLocalPlayUri());
    alert.setMessage(sb.toString());
    alert.setPositiveButton(android.R.string.ok, (dialog, w) -> dialog.dismiss());
    alert.show();
  }

  private void addToRedHeart(Audio audio) {
    if (FavoriteAudio.toggleRedHeart(audio)) {
      Toast.makeText(context, R.string.added_to_red_heart, Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(context, R.string.removed_frome_red_heart, Toast.LENGTH_SHORT).show();
    }
    notifyDataSetChanged();
  }

  private void addToFavorites(Audio audio) {
    AddFavoritesController addFavoritesController = new AddFavoritesController(context);
    addFavoritesController.addToFavorite(audio);
  }

  private void toggleMenu(Audio audio) {
    if (selectedAudio == audio) {
      selectedAudio = null;
    } else {
      selectedAudio = audio;
    }
    notifyDataSetChanged();
  }


  @Override
  public boolean isEnabled(int position) {
    return !(getDataItem(position) instanceof AudioGroup);
  }

  class ViewHolder {
    @InjectView(R.id.title)                  TextView    title;
    @Optional @InjectView(R.id.menu)         View        menu;
    @Optional @InjectView(R.id.menu_btn)     ImageButton menuBtn;
    @Optional @InjectView(R.id.red_heart)    Button      redHeartBtn;
    @Optional @InjectView(R.id.add_to)       Button      addToBtn;
    @Optional @InjectView(R.id.lookInfo)     Button      infoBtn;
    @Optional @InjectView(R.id.delete)       Button      deleteBtn;
    @Optional @InjectView(R.id.subtitle)     TextView    subtitle;
    @Optional @InjectView(R.id.playing_flag) ImageView   playingFlag;
    @Optional @InjectView(R.id.lossless)     TextView    loseless;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

  }
}

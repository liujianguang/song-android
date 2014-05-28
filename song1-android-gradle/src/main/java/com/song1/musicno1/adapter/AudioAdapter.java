package com.song1.musicno1.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;
import com.song1.musicno1.controllers.AddFavoritesController;
import com.song1.musicno1.helpers.TimeHelper;
import com.song1.musicno1.models.FavoriteAudio;
import com.song1.musicno1.models.LocalAudioStore;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.util.Global;
import de.akquinet.android.androlog.Log;

/**
 * Created by windless on 14-5-27.
 */
public class AudioAdapter extends DataAdapter<Audio> {
  protected final LocalAudioStore store;
  private         boolean         hasIndex;
  private         Audio           playingAudio;
  private         Audio           selectedAudio;

  public AudioAdapter(Context context, LocalAudioStore store) {
    super(context);
    this.store = store;
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

    holder.index.setText(String.valueOf(i + 1));
    Log.d(this, "index: " + i);

    if (selectedAudio == audio) {
      holder.menu.setVisibility(View.VISIBLE);
      if (FavoriteAudio.isFavorite(audio)) {
        holder.redHeartButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_heart_choose, 0, 0);
      } else {
        holder.redHeartButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_heart_normal, 0, 0);
      }
    } else {
      holder.menu.setVisibility(View.GONE);
    }

    if (hasIndex) {
      holder.index.setVisibility(View.VISIBLE);
    } else {
      holder.index.setVisibility(View.GONE);
    }

    if (audio.isEqual(playingAudio)) {
      holder.playingFlag.setVisibility(View.VISIBLE);
      holder.index.setVisibility(View.GONE);
    } else {
      holder.playingFlag.setVisibility(View.INVISIBLE);
      if (hasIndex) {
        holder.index.setVisibility(View.VISIBLE);
      }
    }

    if (audio.isLossless()) {
      holder.lossless.setVisibility(View.VISIBLE);
    } else {
      holder.lossless.setVisibility(View.GONE);
    }

    holder.title.setText(audio.getTitle());
    holder.subtitle.setText(audio.getSubtitle(context));

    holder.menuBtn.setOnClickListener((button) -> toggleMenu(audio));
    holder.redHeartButton.setOnClickListener((button) -> addToRedHeart(audio));
    holder.infoButton.setOnClickListener((button) -> showInfo(audio));
    holder.deleteButton.setOnClickListener((button) -> deleteAudio(audio));
    holder.addToButton.setOnClickListener((button) -> addToFavorites(audio));

    return view;
  }

  private void addToFavorites(Audio audio) {
    AddFavoritesController addFavoritesController = new AddFavoritesController(context);
    addFavoritesController.addToFavorite(audio);
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

  public void setHasIndex(boolean hasIndex) {
    this.hasIndex = hasIndex;
    notifyDataSetChanged();
  }

  public void setPlayingAudio(Audio audio) {
    playingAudio = audio;
    notifyDataSetChanged();
  }

  private void addToRedHeart(Audio audio) {
    if (FavoriteAudio.toggleRedHeart(audio)) {
      Toast.makeText(context, R.string.added_to_red_heart, Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(context, R.string.removed_frome_red_heart, Toast.LENGTH_SHORT).show();
    }
    notifyDataSetChanged();
  }

  private void toggleMenu(Audio audio) {
    if (selectedAudio == audio) {
      selectedAudio = null;
    } else {
      selectedAudio = audio;
    }
    notifyDataSetChanged();
  }

  public void setSelectedAudio(Audio audio) {
    selectedAudio = audio;
    notifyDataSetChanged();
  }

  class ViewHolder {
    @InjectView(R.id.index)        TextView    index;
    @InjectView(R.id.playing_flag) ImageView   playingFlag;
    @InjectView(R.id.title)        TextView    title;
    @InjectView(R.id.subtitle)     TextView    subtitle;
    @InjectView(R.id.loseless)     TextView    lossless;
    @InjectView(R.id.menu)         View        menu;
    @InjectView(R.id.menu_btn)     ImageButton menuBtn;
    @InjectView(R.id.add_to)       Button      addToButton;
    @InjectView(R.id.red_heart)    Button      redHeartButton;
    @InjectView(R.id.lookInfo)     Button      infoButton;
    @InjectView(R.id.delete)       Button      deleteButton;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}

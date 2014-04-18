package com.song1.musicno1.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;
import com.song1.musicno1.dialogs.FavoritesDialog;
import com.song1.musicno1.entity.AudioGroup;
import com.song1.musicno1.models.FavoriteAudio;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.util.AudioUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by windless on 14-4-10.
 */
public class AudioAdapter extends DataAdapter<Audio> {
  protected Audio selectedAudio;


  List<Audio> allAudioList = Lists.newArrayList();

  public AudioAdapter(Context context) {
    super(context);
  }

  @Override
  public void setDataList(List<Audio> dataList) {
    super.setDataList(doGroup(dataList));
  }

  private List<Audio> doGroup(List<Audio> dataList) {
    Map<Character, List<Audio>> audioListMap = AudioUtil.doAudioGroup(dataList);
    List<Character> allKeyList = Lists.newArrayList(audioListMap.keySet());
    Collections.sort(allKeyList);

    for (Character key : allKeyList) {
      List<Audio> list = audioListMap.get(key);
      allAudioList.add(new AudioGroup(key.toString()));
      allAudioList.addAll(list);
    }

    return allAudioList;
  }

  @Override
  public View getView(int i, View view, ViewGroup viewGroup) {
    Audio audio = getDataItem(i);
    ViewHolder holder = null;
    TitleViewHolder titleViewHolder = null;

    if (audio instanceof AudioGroup) {
      if (view == null || (view.getTag() instanceof ViewHolder)) {
        view = View.inflate(context, R.layout.audio_group_title, null);
        titleViewHolder = new TitleViewHolder(view);
        view.setTag(titleViewHolder);
      } else {
        titleViewHolder = (TitleViewHolder) view.getTag();
      }
      AudioGroup audioGroup = (AudioGroup) audio;
      titleViewHolder.title.setText(audioGroup.getName());
      return view;
    }
    if (view == null || (view.getTag() instanceof TitleViewHolder)) {
      view = View.inflate(context, R.layout.item_audio, null);
      holder = new ViewHolder(view);
      view.setTag(holder);
    } else {
      holder = (ViewHolder) view.getTag();
    }
    holder.menuBtn.setTag(audio);
    holder.addToBtn.setTag(audio);
    holder.redHeartBtn.setTag(audio);
    holder.title.setText(audio.getTitle());
    holder.art.setText(audio.getArtist() + "-" + audio.getAlbum());
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

  class TitleViewHolder {
    @InjectView(R.id.title)
    TextView title;

    public TitleViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }

  class ViewHolder {
    @InjectView(R.id.title)     TextView    title;
    @InjectView(R.id.menu)      View        menu;
    @InjectView(R.id.menu_btn)  ImageButton menuBtn;
    @InjectView(R.id.red_heart) Button      redHeartBtn;
    @InjectView(R.id.add_to)    Button      addToBtn;
    @InjectView(R.id.tune)      ImageView   tuneImg;
    @InjectView(R.id.art)       TextView    art;

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

    @OnClick(R.id.add_to)
    public void addToFavorite(View view) {
      Audio audio = (Audio) view.getTag();
      showFavoritesDialog(audio);
    }

    private void showFavoritesDialog(Audio audio) {
      if (context instanceof FragmentActivity) {
        FragmentActivity activity = (FragmentActivity) context;
        FavoritesDialog favoritesDialog = new FavoritesDialog();
        favoritesDialog.setAddingAudio(audio);
        favoritesDialog.show(activity.getSupportFragmentManager(), "");
      }
    }
  }
}

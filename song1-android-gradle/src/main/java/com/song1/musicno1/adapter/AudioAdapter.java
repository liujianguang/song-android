package com.song1.musicno1.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.song1.musicno1.R;
import com.song1.musicno1.dialogs.FavoritesDialog;
import com.song1.musicno1.dialogs.PromptDialog;
import com.song1.musicno1.entity.AudioGroup;
import com.song1.musicno1.event.Event;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.helpers.TimeHelper;
import com.song1.musicno1.models.FavoriteAudio;
import com.song1.musicno1.models.LocalAudioStore;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.util.Global;
import com.song1.musicno1.util.ToastUtil;
import com.squareup.otto.Subscribe;

import java.util.List;
import java.util.Map;

/**
 * Created by windless on 14-4-10.
 */
public class AudioAdapter extends DataAdapter<Audio> {
  private final static int GROUP = 0;
  private final static int AUDIO = 1;

  LocalAudioStore localAudioStore;

  protected Audio selectedAudio;
  Fragment fragment;

  FragmentActivity activity;

  public AudioAdapter(Context context) {
    super(context);
    activity = (FragmentActivity) context;
    localAudioStore = new LocalAudioStore(context);
  }

  public void setFragment(Fragment fragment) {
    this.fragment = fragment;
  }

  private Map<String, Integer> mapGroupPosition = Maps.newHashMap();
  private String               firstGroupName   = null;

  @Override
  public void setDataList(List<Audio> dataList) {
    super.setDataList(dataList);
    for (Audio audio : dataList) {
      //System.out.println("--------------------" + audio.getTitle());
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

  public String getFirstGroupName() {
    return firstGroupName;
  }

  public Integer getGroupPositionByName(String name) {
    return mapGroupPosition.get(name);
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
    if (view == null) {
      if (getItemViewType(i) == GROUP) {
        view = View.inflate(context, R.layout.audio_group_title, null);
        view.setTag(new TitleViewHolder(view));
      } else {
        view = View.inflate(context, R.layout.item_audio, null);
        view.setTag(new ViewHolder(view));
      }
    }

    Audio audio = getDataItem(i);

    if (view.getTag() instanceof TitleViewHolder) {
      TitleViewHolder holder = (TitleViewHolder) view.getTag();
      holder.title.setText(audio.getTitle());
    } else {
      ViewHolder holder = (ViewHolder) view.getTag();
      holder.setAudio(audio);
      holder.menuBtn.setTag(audio);
      holder.addToBtn.setTag(audio);
      holder.redHeartBtn.setTag(audio);
      holder.title.setText(audio.getTitle());
      holder.art.setText(audio.getArtist() + "-" + audio.getAlbum());
      holder.currentImageView.setVisibility(View.GONE);

      if (audio == playingAudio){
        holder.currentImageView.setVisibility(View.VISIBLE);
      }

      if (selectedAudio == audio) {
        holder.menu.setVisibility(View.VISIBLE);
        Drawable drawableNormal = context.getResources().getDrawable(R.drawable.ic_heart_normal);
        Drawable drawableChoose = context.getResources().getDrawable(R.drawable.ic_heart_choose);
        if (FavoriteAudio.isFavorite(audio)) {
          holder.redHeartBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableChoose, null, null);
        } else {
          holder.redHeartBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableNormal, null, null);
        }
      } else {
        holder.menu.setVisibility(View.GONE);
      }
    }
    return view;
  }

  Audio playingAudio;
  @Subscribe
  public void playingAudio(Event.PlayingAudioEvent event){
    playingAudio = event.getAudio();
    notifyDataSetChanged();
    ToastUtil.show(context,"audio : " + playingAudio);
  }

  @Override
  public boolean isEnabled(int position) {
    Object obj = getDataItem(position);
    if (obj instanceof AudioGroup) {
      return false;
    }
    return true;
  }

  class TitleViewHolder {
    @InjectView(R.id.title)
    TextView title;

    public TitleViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }

  PromptDialog          detailDialog;
  AudioDetailViewHolder detailViewHolder;

  class ViewHolder {
    @InjectView(R.id.title)     TextView    title;
    @InjectView(R.id.menu)      View        menu;
    @InjectView(R.id.menu_btn)  ImageButton menuBtn;
    @InjectView(R.id.red_heart) Button      redHeartBtn;
    @InjectView(R.id.add_to)    Button      addToBtn;
    //@InjectView(R.id.tune)      ImageView   tuneImg;
    @InjectView(R.id.art)       TextView    art;
    @InjectView(R.id.currentAudioImageView) ImageView currentImageView;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    Audio audio;

    public void setAudio(Audio audio) {
      this.audio = audio;
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
      if (FavoriteAudio.toggleRedHeart(audio)) {
        Toast.makeText(context, R.string.added_to_red_heart, Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(context, R.string.removed_frome_red_heart, Toast.LENGTH_SHORT).show();
      }
      notifyDataSetChanged();
    }

    @OnClick(R.id.add_to)
    public void addToFavorite(View view) {
      Audio audio = (Audio) view.getTag();
      showFavoritesDialog(audio);
    }

    @OnClick(R.id.lookInfo)
    public void lookInfo(View view) {
      if (context instanceof FragmentActivity) {
        FragmentActivity activity = (FragmentActivity) context;
//        if (detailDialog == null){
        detailDialog = new PromptDialog(context);
        detailViewHolder = new AudioDetailViewHolder();
        detailDialog.setTitle(R.string.songInfo);
        detailDialog.setCancelText(R.string.close);
        detailDialog.setConfirmText(R.string.update);
        detailDialog.setClickListener(detailViewHolder);
        detailDialog.setCustomView(detailViewHolder.view);
//        }
        detailViewHolder.setData(audio);
        detailDialog.show(activity.getSupportFragmentManager(), "AudioDetail");
      }
    }


    @OnClick(R.id.delete)
    public void delete(View view) {
      //ToastUtil.show(context, audio.getLocalPlayUri());
      if (localAudioStore.deleteAudio(audio))
      {
        //ToastUtil.show(context,"删除成功!");
        remove(audio);
      }
      else{
        //ToastUtil.show(context,"删除失败!");
      }
      notifyDataSetChanged();
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

  class AudioDetailViewHolder implements View.OnClickListener {
    View view;
    @InjectView(R.id.songName)
    TextView songName;
    @InjectView(R.id.fileType)
    TextView fileType;
    @InjectView(R.id.fileSize)
    TextView fileSize;
    @InjectView(R.id.duration)
    TextView duration;
    @InjectView(R.id.path)
    TextView path;

    Audio audio;

    private AudioDetailViewHolder() {
      view = LayoutInflater.from(context).inflate(R.layout.audio_detail, null);
      ButterKnife.inject(this, view);
    }

    public void setData(Audio audio) {
      this.audio = audio;
      songName.setText(audio.getTitle());
      path.setText(audio.getLocalPlayUri());
      fileSize.setText(Global.format(audio.getSize()));
      duration.setText(TimeHelper.milli2str((int) audio.getDuration()));
      fileType.setText(audio.getMimiType());
    }

    @Override
    public void onClick(View view) {
      switch (view.getId()) {
        case R.id.cancel:
          detailDialog.dismiss();
          break;
        case R.id.confirm:
          detailDialog.dismiss();
          PromptDialog editDialog = new PromptDialog(context);
          View editView = LayoutInflater.from(context).inflate(R.layout.audio_edit,null);
          EditText songNameEditView = (EditText) editView.findViewById(R.id.songName);
          songNameEditView.setText(audio.getTitle());
          songNameEditView.setSelection(audio.getTitle().length());

          editDialog.setTitle(R.string.updateTitle);
          editDialog.setCustomView(editView);
          editDialog.setConfirmClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              audio.setTitle(songNameEditView.getText().toString());
              Audio newAudio = localAudioStore.update(audio);
              if (newAudio == null){
                remove(audio);
              }else{
                audio.setLocalPlayUri(newAudio.getLocalPlayUri());
              }
              notifyDataSetChanged();
              editDialog.dismiss();
            }
          });
          editDialog.show(activity.getSupportFragmentManager(),"EditDialog");
          break;
      }
    }
  }
}

package com.song1.musicno1.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.song1.musicno1.R;
import com.song1.musicno1.dialogs.FavoritesDialog;
import com.song1.musicno1.entity.AudioGroup;
import com.song1.musicno1.models.FavoriteAudio;
import com.song1.musicno1.models.play.Audio;

import java.util.List;
import java.util.Map;

/**
 * Created by windless on 14-4-10.
 */
public class AudioAdapter extends DataAdapter<Audio> {
  private final static int GROUP = 0;
  private final static int AUDIO = 1;

  protected Audio selectedAudio;

  public AudioAdapter(Context context) {
    super(context);
  }

  private Map<String, Integer> mapGroupPosition = Maps.newHashMap();
  private String firstGroupName = null;
  @Override
  public void setDataList(List<Audio> dataList) {
    super.setDataList(dataList);
    for (Audio audio : dataList) {
      //System.out.println("--------------------" + audio.getTitle());
      if (audio instanceof AudioGroup) {
        mapGroupPosition.put(audio.getTitle(),dataList.indexOf(audio));
        if (firstGroupName == null){
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

  public String  getFirstGroupName(){
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
      holder.menuBtn.setTag(audio);
      holder.addToBtn.setTag(audio);
      holder.redHeartBtn.setTag(audio);
      holder.title.setText(audio.getTitle());
      holder.art.setText(audio.getArtist() + "-" + audio.getAlbum());

      if (selectedAudio == audio) {
        holder.menu.setVisibility(View.VISIBLE);
        Drawable drawableNormal = context.getResources().getDrawable(R.drawable.ic_heart_normal);
        Drawable drawableChoose = context.getResources().getDrawable(R.drawable.ic_heart_choose);
        if (FavoriteAudio.isFavorite(audio)) {

          holder.redHeartBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableChoose, null, null);
        } else {
          holder.redHeartBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableNormal, null, null);
        }
      }else{
        holder.menu.setVisibility(View.GONE);
      }
    }
    return view;
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

  class ViewHolder {
    @InjectView(R.id.title)     TextView    title;
    @InjectView(R.id.menu)      View        menu;
    @InjectView(R.id.menu_btn)  ImageButton menuBtn;
    @InjectView(R.id.red_heart) Button      redHeartBtn;
    @InjectView(R.id.add_to)    Button      addToBtn;
    //@InjectView(R.id.tune)      ImageView   tuneImg;
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

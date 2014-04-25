package com.song1.musicno1.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.AudioAdapter;
import com.song1.musicno1.adapter.DataAdapter;
import com.song1.musicno1.entity.Album;
import com.song1.musicno1.entity.Artist;
import com.song1.musicno1.entity.AudioGroup;
import com.song1.musicno1.fragments.base.ListFragment;
import com.song1.musicno1.helpers.List8;
import com.song1.musicno1.loader.LoadData;
import com.song1.musicno1.models.LocalAudioStore;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Players;
import com.song1.musicno1.models.play.Playlist;
import com.song1.musicno1.util.ToastUtil;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * User: windless
 * Date: 13-8-29
 * Time: PM3:07
 */
public class LocalAudioFragment extends ListFragment<Audio> implements AdapterView.OnItemClickListener{
  @Inject LocalAudioStore localAudioStore;
  private Album           album;
  private Artist          artist;
  private TextView        audioTotalTextView;
  private ListView        listView;

  AudioAdapter audioAdapter;
  int audioTotal = 0;

  Map<String, Button> mapNumberButton = Maps.newHashMap();
  Button currentNumberButton;

  @Inject
  public LocalAudioFragment() {
  }

  @Override
  protected List<Audio> onLoad(int loadPage) {
    setTotalPage(1);

    List<Audio> audioList;
    if (album != null) {
      audioList = localAudioStore.get_audios_by_album(album);
      audioTotal = audioList.size();
    } else if (artist != null) {
      audioList = localAudioStore.audios_by_artist(artist);
      audioTotal = audioList.size();
    } else {
      audioList = localAudioStore.getAudiosWithIndex();
      audioTotal = localAudioStore.audios_count();
//      audioList = Lists.newArrayList();
//      for (Audio audio :audioList){
//        System.out.println("*************" + audio.getTitle());
//      }
    }
    return audioList;
  }

  @Override
  public void onLoadFinished(Loader<LoadData<Audio>> loader, LoadData<Audio> data) {
    super.onLoadFinished(loader, data);
    String firstGroupName = audioAdapter.getFirstGroupName();
    setCurrentNumberButton(firstGroupName);
  }

  @Override
  public void showContent() {
    super.showContent();
    if (!isDataEmpty() && getListView().getHeaderViewsCount() == 0) {
      View headerView = View.inflate(getActivity(), R.layout.header_local_audio, null);
      headerView.setOnClickListener((view) -> {
        List<Audio> dataList = getDataList();
        Random random = new Random();
        int randomIndex = random.nextInt(dataList.size());
        Players.setPlaylist(new Playlist(List8.newList(dataList), dataList.get(randomIndex)));
      });
      getListView().addHeaderView(headerView);
      audioTotalTextView = (TextView) headerView.findViewById(R.id.audioTotal);
    }

    String str = String.format(getString(R.string.allAudios), audioTotal);
    audioTotalTextView.setText(str);
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    RelativeLayout view = (RelativeLayout) super.onCreateView(inflater, container, savedInstanceState);
    view.addView(createNumberNegative());
    return view;
  }

  private View createNumberNegative() {
    List<String> chars = Lists.newArrayList(getResources().getStringArray(R.array.chars));
    chars.add("#");
    LinearLayout linearLayout = new LinearLayout(getActivity());
    linearLayout.setBackgroundColor(Color.GRAY);
    linearLayout.getBackground().setAlpha(120);
    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(24, ViewGroup.LayoutParams.MATCH_PARENT);
    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    linearLayout.setLayoutParams(layoutParams);
    linearLayout.setOrientation(LinearLayout.VERTICAL);
    linearLayout.setGravity(Gravity.CENTER);
    for (String ch : chars) {
      Button button = new Button(getActivity());
      button.setLayoutParams(new ViewGroup.LayoutParams(24, 22));
      button.setText(ch);
      button.setTextSize(11);
      button.setTextColor(getResources().getColor(R.color.number_color));
      button.setGravity(Gravity.CENTER);
      button.setOnClickListener(numberButtonClickListener);
      button.setTag(chars.indexOf(ch));
      linearLayout.addView(button);
      mapNumberButton.put(ch,button);
    }
    return linearLayout;
  }

  private void setCurrentNumberButton(String number){
    if (number != null){
      if (currentNumberButton != null){
        currentNumberButton.setBackgroundColor(Color.TRANSPARENT);
      }
      currentNumberButton = mapNumberButton.get(number);
      currentNumberButton.setBackgroundColor(getResources().getColor(R.color.title_bg_color));
    }
  }
  @Override
  protected DataAdapter<Audio> newAdapter() {
    audioAdapter = new AudioAdapter(getActivity());
    return audioAdapter;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getListView().setOnItemClickListener(this);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Audio audio = getDataItem(position - 1); // 为什么 header view 要影响 position????
    Playlist playlist = new Playlist(List8.newList(getDataList()), audio);
    Players.setPlaylist(playlist);
  }

  public void setAlbum(Album album) {
    this.album = album;
  }

  public void setArtist(Artist artist) {
    this.artist = artist;
  }

  private View.OnClickListener numberButtonClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      Button button = (Button) view;
      //ToastUtil.show(getActivity(),button.getText().toString());
      Integer position = audioAdapter.getGroupPositionByName(button.getText().toString());
      if (position != null) {
        getListView().setSelection(position);
      }
    }
  };

  @Override
  public void onScroll(AbsListView absListView, int firstVisibleItem,
                       int visibleItemCount, int totalItemCount) {
    if (audioAdapter.getCount() == 0){
      return;
    }
    Audio audio = audioAdapter.getDataItem(firstVisibleItem);
    if (audio instanceof AudioGroup){
      setCurrentNumberButton(audio.getTitle());
    }
  }
}

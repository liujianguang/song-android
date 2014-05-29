package com.song1.musicno1.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.common.collect.Lists;
import com.song1.musicno1.App;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.AudioWithIndexAdapter;
import com.song1.musicno1.adapter.DataAdapter;
import com.song1.musicno1.dialogs.MediaScannerDialog;
import com.song1.musicno1.fragments.base.DataFragment;
import com.song1.musicno1.helpers.List8;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.models.LocalAudioStore;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Player;
import com.song1.musicno1.models.play.Players;
import com.song1.musicno1.models.play.Playlist;
import com.song1.musicno1.stores.PlayerStore;
import com.song1.musicno1.util.AudioUtil;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * Created by windless on 14-5-15.
 */
public class LocalAudiosWithIndexFragment extends DataFragment<Audio> implements View.OnTouchListener, AdapterView.OnItemClickListener {
  @InjectView(R.id.list)    ListView listView;
  @InjectView(R.id.index)   TextView indexView;
  @InjectView(R.id.loading) View     loadingView;

  TextView audioTotalTextView;

  private AudioWithIndexAdapter audioAdapter;
  private List<String>          indexStrings;

  private int index = 0;
  protected int audiosCount;
  private boolean isLoaded = false;
  protected LocalAudioStore localAudioStore;

  @Override
  protected List<Audio> onLoad(int loadPage) {
    setTotalPage(1);
    List<Audio> audios = localAudioStore.getAll();
    audiosCount = audios.size();
    return AudioUtil.doAudioGroup(audios);
  }

  @Override
  protected DataAdapter<Audio> newAdapter() {
    audioAdapter = new AudioWithIndexAdapter(getActivity(), App.get(LocalAudioStore.class));
    return audioAdapter;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    localAudioStore = App.get(LocalAudioStore.class);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    listView.setAdapter(getAdapter());
    listView.setOnItemClickListener(this);
    indexView.setOnTouchListener(this);

    indexStrings = Lists.newArrayList(getResources().getStringArray(R.array.chars));
    StringBuilder sb = new StringBuilder();
    for (String indexString : indexStrings) {
      sb.append(indexString);
    }
    indexView.setText(sb.toString());
  }

  @Override
  public void onResume() {
    super.onResume();
    MainBus.register(this);
    updateCurrentPlayerState(null);
  }

  @Subscribe
  public void updateCurrentPlayerState(PlayerStore.CurrentPlayerChangedEvent event) {
    updatePlayingAudio(null);
  }

  @Subscribe
  public void updatePlayingAudio(PlayerStore.PlayerPlayingAudioChangedEvent event) {
    Player currentPlayer = PlayerStore.INSTANCE.getCurrentPlayer();
    if (currentPlayer != null) {
      audioAdapter.setPlayingAudio(currentPlayer.getPlayingAudio());
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    MainBus.unregister(this);
  }

  public void playAll() {
    List<Audio> dataList = getDataList();
    if (dataList.size() > 0) {
      Players.randomPlay(new Playlist(List8.newList(dataList), dataList.get(0)), getFragmentManager());
    }
  }

  @Override
  protected void showContent() {
    isLoaded = true;

    loadingView.setVisibility(View.GONE);
    listView.setVisibility(View.VISIBLE);
    indexView.setVisibility(View.VISIBLE);
    audioTotalTextView.setText(getString(R.string.allAudios, audiosCount));
    getActivity().invalidateOptionsMenu();
  }

  @Override
  protected void showLoading() {
    isLoaded = false;
    loadingView.setVisibility(View.VISIBLE);
    listView.setVisibility(View.GONE);
    indexView.setVisibility(View.GONE);
    getActivity().invalidateOptionsMenu();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_list_with_index, container, false);
    ButterKnife.inject(this, view);

    View headerView = View.inflate(getActivity(), R.layout.item_local_header, null);
    audioTotalTextView = (TextView) headerView.findViewById(R.id.audioTotal);
    listView.addHeaderView(headerView);

    return view;
  }

  @Override
  public boolean onTouch(View view, MotionEvent motionEvent) {
    if (getIndexStrings().size() == 0) return false;

    int lineHeight = indexView.getLineHeight();

    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN ||
        motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
      float y = motionEvent.getY() - indexView.getPaddingTop();
      int index = (int) (y / lineHeight);

      if (index >= 0 && index < getIndexStrings().size()) {
        setIndex(index);
      }
    }
    return true;
  }

  private void setIndex(int index) {
    if (this.index != index) {
      this.index = index;
      Integer groupIndex = audioAdapter.getGroupPositionByName(getIndexStrings().get(index));
      if (groupIndex != null) {
        listView.setSelection(groupIndex + 1);
      }
    }
  }

  protected List<String> getIndexStrings() {
    return indexStrings;
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    if (i == 0) {
      playAll();
    } else {
      Playlist playlist = new Playlist(List8.newList(getDataList()), audioAdapter.getDataItem(i - 1));
      Players.setPlaylist(playlist, getFragmentManager());
    }
    audioAdapter.setSelectedAudio(null);
  }
}

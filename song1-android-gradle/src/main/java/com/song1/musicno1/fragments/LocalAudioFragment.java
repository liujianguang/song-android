package com.song1.musicno1.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.*;
import android.widget.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nineoldandroids.view.animation.AnimatorProxy;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.AudioAdapter;
import com.song1.musicno1.adapter.DataAdapter;
import com.song1.musicno1.entity.Album;
import com.song1.musicno1.entity.Artist;
import com.song1.musicno1.entity.AudioGroup;
import com.song1.musicno1.fragments.base.ListFragment;
import com.song1.musicno1.helpers.List8;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.helpers.ViewHelper;
import com.song1.musicno1.loader.LoadData;
import com.song1.musicno1.models.LocalAudioStore;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Players;
import com.song1.musicno1.models.play.Playlist;
import com.song1.musicno1.stores.PlayerStore;
import com.song1.musicno1.util.ToastUtil;
import de.akquinet.android.androlog.Log;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.os.Environment.getExternalStorageDirectory;

/**
 * User: windless
 * Date: 13-8-29
 * Time: PM3:07
 */
public class LocalAudioFragment extends ListFragment<Audio> implements AdapterView.OnItemClickListener {
  @Inject LocalAudioStore localAudioStore;
  private Album           album;
  private Artist          artist;

  private LinearLayout playAllLayout;
  private TextView     audioTotalTextView;

  AudioAdapter audioAdapter;
  int audioTotal = 0;

  Map<String, Button> mapNumberButton = Maps.newHashMap();
  Button currentNumberButton;

  BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      System.out.println("action : " + action);
      if (Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action)) {
        clearLoadData();
        showLoading();
      } else if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {
        showContent();
        reload();
        isRefreshing = false;
      }
    }
  };

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
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    System.out.println("onCreateOptionMenu...");
    if (album == null && artist == null) {
      inflater.inflate(R.menu.local_audio, menu);
    }
    super.onCreateOptionsMenu(menu, inflater);
  }

  boolean isRefreshing = false;

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.refresh:
        if (isRefreshing) {
          return true;
        }
        System.out.println("refresh...");
        //reload();
        isRefreshing = true;
        getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + getExternalStorageDirectory().getAbsolutePath())));
//        Toast.makeText(getActivity(), "refresh", Toast.LENGTH_SHORT).show();
        break;
    }
    return true;
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

    if (isDataEmpty()) {
//      if (playHeaderView != null) {
      // getListView().removeHeaderView(playHeaderView);
      playHeaderView.setVisibility(View.GONE);
//      }
    } else {
      playHeaderView.setVisibility(View.VISIBLE);
//      if (playHeaderView == null) {
//        playHeaderView = View.inflate(getActivity(), R.layout.header_local_audio, null);
//        playHeaderView.setOnClickListener((view) -> {
//          List<Audio> dataList = getDataList();
//          if (dataList.size() > 0) {
//            Random random = new Random();
//            int randomIndex = random.nextInt(dataList.size());
//            Players.setPlaylist(new Playlist(List8.newList(dataList), dataList.get(randomIndex)));
//          }
//        });
//        getListView().addHeaderView(playHeaderView);
//      }
      String str = String.format(getString(R.string.allAudios), audioTotal);
      audioTotalTextView.setText(str);
    }
  }

  private View playHeaderView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    RelativeLayout root = (RelativeLayout) super.onCreateView(inflater, container, savedInstanceState);
    LinearLayout headerLayout = (LinearLayout) root.findViewById(R.id.headerLayout);
    playHeaderView = inflater.inflate(R.layout.header_local_audio, headerLayout);
    audioTotalTextView = (TextView) playHeaderView.findViewById(R.id.audioTotal);
    playAllLayout = (LinearLayout) playHeaderView.findViewById(R.id.playAll);
    playAllLayout.setOnClickListener((view) -> {
      List<Audio> dataList = getDataList();
      if (dataList.size() > 0) {
        Random random = new Random();
        int randomIndex = random.nextInt(dataList.size());
        Players.setPlaylist(new Playlist(List8.newList(dataList), dataList.get(randomIndex)), getFragmentManager());
      }
    });
    if (album == null && artist == null) {
      root.addView(createNumberNegative());
    }
    return root;
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(this, "onResume...");
  }

  @Override
  public void onPause() {
    super.onPause();
    Log.d(this, "onPause...");
  }

  private View createNumberNegative() {
    List<String> chars = Lists.newArrayList(getResources().getStringArray(R.array.chars));
    chars.add("#");
    LinearLayout linearLayout = new LinearLayout(getActivity());
    linearLayout.setBackgroundColor(Color.GRAY);
    AnimatorProxy.wrap(linearLayout).setAlpha(120);

    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    linearLayout.setLayoutParams(layoutParams);
    linearLayout.setOrientation(LinearLayout.VERTICAL);
    linearLayout.setGravity(Gravity.CENTER);
    linearLayout.setPadding(0, 10, 0, 0);
    for (String ch : chars) {
      Button button = new Button(getActivity());
      button.setLayoutParams(new LinearLayout.LayoutParams(ViewHelper.dp2pixels(getActivity(), 18f), 0, 1));
      button.setText(ch);
      button.setTextSize(ViewHelper.dp2pixels(getActivity(), 5f));
      button.setTextColor(getResources().getColor(R.color.number_color));
      button.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
      button.setOnClickListener(numberButtonClickListener);
      button.setTag(chars.indexOf(ch));
      linearLayout.addView(button);
      mapNumberButton.put(ch, button);
    }
    return linearLayout;
  }

  private void setCurrentNumberButton(String number) {
    if (number != null) {
      if (currentNumberButton != null) {
        currentNumberButton.setBackgroundColor(Color.TRANSPARENT);
      }
      currentNumberButton = mapNumberButton.get(number);
      currentNumberButton.setBackgroundColor(getResources().getColor(R.color.title_bg_color));
    }
  }

  @Override
  protected DataAdapter<Audio> newAdapter() {
    audioAdapter = new AudioAdapter(getActivity());
    audioAdapter.setFragment(this);
    return audioAdapter;
  }

  public void refreshData() {
    audioAdapter.notifyDataSetChanged();
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getListView().setOnItemClickListener(this);
    setHasOptionsMenu(true);

    IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
    intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
    intentFilter.addDataScheme("file");
    getActivity().registerReceiver(refreshReceiver, intentFilter);
    MainBus.register(audioAdapter);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    getActivity().unregisterReceiver(refreshReceiver);
    MainBus.unregister(audioAdapter);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Audio audio = getDataItem(position); // 为什么 header view 要影响 position????
    Playlist playlist = new Playlist(List8.newList(getDataList()), audio);
    Players.setPlaylist(playlist, getFragmentManager());
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
    if (audioAdapter.getCount() == 0) {
      return;
    }
    Audio audio = audioAdapter.getDataItem(firstVisibleItem);
    if (audio instanceof AudioGroup) {
      setCurrentNumberButton(audio.getTitle());
    }
  }
}

package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.MiguAudioAdapter;
import com.song1.musicno1.entity.BeatlesAudio;
import com.song1.musicno1.entity.SongInfo;
import com.song1.musicno1.models.play.Audio;

import java.util.List;

/**
 * User: windless
 * Date: 13-12-12
 * Time: PM4:08
 */
public class BeatlesDetailFragment extends BaseFragment implements AdapterView.OnItemClickListener {
  private List<BeatlesAudio> audios;

  MiguAudioAdapter adapter;

  @InjectView(R.id.list) ListView listView;

  public void setAudios(List<BeatlesAudio> audios) {
    this.audios = audios;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_migu_audio, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    adapter = new MiguAudioAdapter(getActivity());
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(this);

    adapter.setActivity(getActivity());
    List<SongInfo> songInfos = Lists.transform(audios, new Function<BeatlesAudio, SongInfo>() {
      @Override
      public SongInfo apply(BeatlesAudio beatlesAudio) {
        return beatlesAudio.toSongInfo();
      }
    });
    adapter.setDataList(songInfos);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Audio audio = adapter.getDataItem(position).toAudio();
//    adapter.getPlayAction().play(audio);
  }
}

package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.MiguAudioAdapter;
import com.song1.musicno1.entity.BeatlesAudio;
import com.song1.musicno1.fragments.base.BaseFragment;
import com.song1.musicno1.helpers.List8;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Players;
import com.song1.musicno1.models.play.Playlist;

import java.util.List;

/**
 * User: windless
 * Date: 13-12-12
 * Time: PM4:08
 */
public class BeatlesDetailFragment extends BaseFragment implements AdapterView.OnItemClickListener {
  private List<Audio> audios;

  MiguAudioAdapter adapter;

  @InjectView(R.id.list) ListView listView;

  public void setAudios(List<BeatlesAudio> audios) {
    this.audios = List8.newList(audios).map((beatlesAudio) -> beatlesAudio.toAudio());
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
    adapter.setDataList(audios);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Audio audio = adapter.getDataItem(position);
    Playlist playlist = new Playlist(List8.newList(audios), audio);
    Players.setPlaylist(playlist, getFragmentManager());
  }
}

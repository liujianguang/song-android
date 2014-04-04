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
import com.song1.musicno1.adapter.JustingAudioAdapter;
import com.song1.musicno1.entity.JustingAudio;
import com.song1.musicno1.fragments.base.BaseFragment;
import com.song1.musicno1.helpers.List8;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Players;
import com.song1.musicno1.models.play.Playlist;

import java.util.List;

/**
 * User: windless
 * Date: 13-12-13
 * Time: PM3:10
 */
public class JustingAudioFragment extends BaseFragment implements AdapterView.OnItemClickListener {
  @InjectView(R.id.list) ListView listView;

  private List<JustingAudio>  audios;
  private JustingAudioAdapter adapter;
  private List8<Audio>        normalAudios;

  public JustingAudioFragment() {

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_only_list, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    adapter = new JustingAudioAdapter(getActivity());
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    adapter.setDataList(audios);
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(this);
  }

  public void setAudios(List<JustingAudio> audios) {
    this.audios = audios;
    List8<JustingAudio> justingAudios = List8.newList(audios);
    normalAudios = justingAudios.map((justingAudio) -> justingAudio.toAudio());
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Players.setPlaylist(new Playlist(normalAudios, normalAudios.get(position)));
  }
}

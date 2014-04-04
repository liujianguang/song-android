package com.song1.musicno1.fragments.migu;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import com.song1.musicno1.adapter.DataAdapter;
import com.song1.musicno1.adapter.MiguAudioAdapter;
import com.song1.musicno1.entity.SongInfo;
import com.song1.musicno1.entity.SubjectInfo;
import com.song1.musicno1.fragments.base.ListFragment;
import com.song1.musicno1.helpers.List8;
import com.song1.musicno1.models.MiguService;
import com.song1.musicno1.models.RspException;
import com.song1.musicno1.models.play.Audio;
import com.song1.musicno1.models.play.Players;
import com.song1.musicno1.models.play.Playlist;

import java.io.IOException;
import java.util.List;

/**
 * User: windless
 * Date: 13-12-6
 * Time: PM6:01
 */
public class MiguSongListDetailFragment extends ListFragment<Audio> implements AdapterView.OnItemClickListener {
  private SubjectInfo subjectInfo;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getListView().setOnItemClickListener(this);
  }

  @Override
  protected List<Audio> onLoad(int loadPage) {
    try {
      setTotalPage(1);
      SongInfo[] rsp = MiguService.getInstance().Init(getActivity()).fetchSongBySubjectId(subjectInfo.id);
      return List8.newList(rsp).map((songInfo) -> songInfo.toAudio());
    } catch (IOException e) {
      return null;
    } catch (RspException e) {
      return null;
    }
  }

  @Override
  protected DataAdapter<Audio> newAdapter() {
    return new MiguAudioAdapter(getActivity());
  }


  public void setSubjectInfo(SubjectInfo subjectInfo) {
    this.subjectInfo = subjectInfo;
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Playlist playlist = new Playlist(List8.newList(getDataList()), getDataItem(position));
    Players.setPlaylist(playlist);
  }
}

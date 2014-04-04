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
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.adapter.JustingPodCastAdapter;
import com.song1.musicno1.entity.JustingPodCast;
import com.song1.musicno1.fragments.base.BaseFragment;

import java.util.List;

/**
 * User: windless
 * Date: 13-12-12
 * Time: PM3:02
 */
public class JustingPodCastFragment extends BaseFragment implements AdapterView.OnItemClickListener {
  @InjectView(R.id.list) ListView listView;

  private JustingPodCastAdapter adapter;

  private List<JustingPodCast> podCasts;

  public JustingPodCastFragment() {
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
    adapter = new JustingPodCastAdapter(getActivity());
    adapter.setDataList(podCasts);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(this);
  }

  public void setPodCasts(List<JustingPodCast> podCasts) {
    this.podCasts = podCasts;
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    JustingPodCast podCast = adapter.getDataItem(position);
    JustingAudioFragment fragment = new JustingAudioFragment();
    fragment.setTitle(podCast.title);
    fragment.setAudios(podCast.audios);
    MainActivity mainActivity = (MainActivity) getActivity();
    mainActivity.push(JustingAudioFragment.class.getName(), fragment);
  }
}

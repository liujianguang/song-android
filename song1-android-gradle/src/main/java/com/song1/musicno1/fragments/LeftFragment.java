package com.song1.musicno1.fragments;

import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.adapter.NavigationAdapter;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * User: windless
 * Date: 14-2-6
 * Time: PM4:11
 */
public class LeftFragment extends Fragment implements AdapterView.OnItemClickListener {

  private NavigationAdapter adapter;

  @InjectView(R.id.left_list)
  ListView listView;
//    @InjectView(R.id.current_version)
//    TextView currentVersionView;
  MainActivity mainActivity;
  private Handler handler = new Handler();

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_left, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    adapter = new NavigationAdapter(getActivity());
    listView.setAdapter(adapter);

    List<Object> items = Lists.newArrayList();

    items.add(getString(R.string.my_music));
    items.add(R.string.local_source);
    items.add(R.string.download_music);

    items.add(getString(R.string.cloud_source));
    items.add(R.string.migu_title);
    items.add(R.string.beatles_music);
    items.add(R.string.justing);

    adapter.setChannels(items);
    listView.setOnItemClickListener(this);
    mainActivity = (MainActivity) getActivity();
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
    Object obj = adapter.getItem(position);
    int resid;
    if (obj instanceof Integer){
      resid = Integer.parseInt(obj.toString());
    }else {
      return;
    }
    switch (resid){
      case R.string.local_source:
        mainActivity.show(new LocalAudioFragment().setTitle(getString(R.string.local_source)));
        break;
      case R.string.download_music:
        break;
      case R.string.migu_title:
        break;
      case R.string.beatles_music:
        break;
      case R.string.justing:
        break;
      default:
        break;
    }
  }
}

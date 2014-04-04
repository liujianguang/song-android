package com.song1.musicno1.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.song1.musicno1.App;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.adapter.NavigationAdapter;
import com.song1.musicno1.dialogs.LoadingDialog;
import com.song1.musicno1.models.migu.MiguIniter;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * User: windless
 * Date: 14-2-6
 * Time: PM4:11
 */
public class LeftFragment extends Fragment implements AdapterView.OnItemClickListener {

  private Map<Integer, Fragment> mapFragment = Maps.newHashMap();

  @Inject MiguIniter miguIniter;

  @InjectView(R.id.left_list)

  ListView listView;
  MainActivity      mainActivity;
  NavigationAdapter adapter;

  private Handler handler = new Handler();

  @Inject
  public LeftFragment() {
  }

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
//    items.add(R.string.download_music);

    items.add(getString(R.string.cloud_source));
    items.add(R.string.migu_title);
    items.add(R.string.beatles_music);
    items.add(R.string.justing);

    adapter.setChannels(items);
    listView.setOnItemClickListener(this);
    mainActivity = (MainActivity) getActivity();

    showFragment(R.string.local_source);
  }

  private void showFragment(int resId) {
    Fragment fragment = mapFragment.get(resId);
    if (fragment != null) {
      mainActivity.replaceMain(fragment);
      return;
    }

    switch (resId) {
      case R.string.local_source:
        fragment = App.get(LocalAudioContainerFragment.class);
        break;
      case R.string.download_music:
        break;
      case R.string.migu_title:
        initMigu(() -> {
          MiguMusicFragment miguMusicFragment = new MiguMusicFragment();
          mapFragment.put(R.string.migu_title, miguMusicFragment);
          mainActivity.replaceMain(miguMusicFragment);
        });
        break;
      case R.string.beatles_music:
        fragment = new BeatlesFrag();
        break;
      case R.string.justing:
        fragment = new JustingCategoryFragment();
        break;
    }

    if (fragment != null) {
      mapFragment.put(resId, fragment);
      mainActivity.replaceMain(fragment);
    }
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
    Object obj = adapter.getItem(position);
    int resId;
    if (obj instanceof Integer) {
      resId = Integer.parseInt(obj.toString());
      showFragment(resId);
    } else {
      return;
    }
  }

  private void initMigu(Runnable runnable) {
    if (!miguIniter.isInited()) {
      LoadingDialog loadingDialog = new LoadingDialog();
      loadingDialog.setText(getString(R.string.init_migu));
      loadingDialog.show(getFragmentManager(), null);

      new Thread(() -> {
        if (true) {
          handler.post(() -> {
            loadingDialog.dismiss();
            runnable.run();
          });
        } else {
          handler.post(() -> {
            loadingDialog.dismiss();
            Toast.makeText(getActivity(), R.string.init_migu_failed, Toast.LENGTH_LONG).show();
          });
        }
      }).start();
    } else {
      runnable.run();
    }
  }
}

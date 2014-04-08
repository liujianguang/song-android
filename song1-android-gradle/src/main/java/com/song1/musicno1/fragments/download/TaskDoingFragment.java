package com.song1.musicno1.fragments.download;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.song1.musicno1.R;
import com.song1.musicno1.db.DownLoadManagerDB;
import de.akquinet.android.androlog.Log;

/**
 * Created by leovo on 2014/4/8.
 */
public class TaskDoingFragment extends Fragment{

  DownLoadManagerDB downLoadManagerDB;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    System.out.println("onCreateView...");
    View view = inflater.inflate(R.layout.fragment_task_doing,null);
    return view;
  }
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Log.d(this,"onActivityCreated...");
    downLoadManagerDB = new DownLoadManagerDB(getActivity());
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(this,"onResume...");
  }

  @Override
  public void onPause() {
    super.onPause();
    Log.d(this,"onPause...");
  }
  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d(this,"onDestroy...");
  }
}

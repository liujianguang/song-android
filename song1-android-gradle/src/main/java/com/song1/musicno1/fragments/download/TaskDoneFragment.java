package com.song1.musicno1.fragments.download;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.song1.musicno1.R;

/**
 * Created by leovo on 2014/4/8.
 */
public class TaskDoneFragment extends Fragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    System.out.println("onCreateView...");
    View view = inflater.inflate(R.layout.fragment_task_doing,null);
    return view;
  }
}

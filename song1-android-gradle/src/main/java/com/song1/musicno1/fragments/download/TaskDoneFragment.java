package com.song1.musicno1.fragments.download;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.TaskDoneAdapter;
import com.song1.musicno1.models.DownLoadManager;
import com.song1.musicno1.models.DownLoadManager.Task;

import java.util.List;

/**
 * Created by leovo on 2014/4/8.
 */
public class TaskDoneFragment extends Fragment {

  ListView taskListView;

  List<Task>      taskList;
  TaskDoneAdapter taskDoneAdapter;




  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    System.out.println("onCreateView...");
    View view = inflater.inflate(R.layout.fragment_task_done, null);
    taskListView = (ListView) view.findViewById(R.id.taskListView);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    taskDoneAdapter = new TaskDoneAdapter(getActivity());
    taskListView.setAdapter(taskDoneAdapter);
    taskDoneAdapter.loadData();
  }
}

package com.song1.musicno1.fragments.download;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;
import com.song1.musicno1.adapter.TaskDoingAdapter;
import com.song1.musicno1.models.DownLoadManager;
import com.song1.musicno1.util.ToastUtil;
import de.akquinet.android.androlog.Log;
import java.util.List;

/**
 * Created by leovo on 2014/4/8.
 */
public class TaskDoingFragment extends Fragment implements AdapterView.OnItemClickListener{

  private static final int LOAD_FINISH = 0;
  @InjectView(R.id.taskDoingListView)
  ListView taskDoningListView;

  DownLoadManager            downLoadManager;
  List<DownLoadManager.Task> taskList;
  TaskDoingAdapter           taskListAdapter;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    System.out.println("onCreateView...");
    View view = inflater.inflate(R.layout.fragment_task_doing, null);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Log.d(this, "onActivityCreated...");
    downLoadManager = DownLoadManager.getDownLoadManager(getActivity());
    taskListAdapter = new TaskDoingAdapter(getActivity());
    taskDoningListView.setAdapter(taskListAdapter);
    taskDoningListView.setOnItemClickListener(this);
    taskListAdapter.onStart();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    taskListAdapter.onDestory();
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    DownLoadManager.Task task = (DownLoadManager.Task)taskListAdapter.getItem(i);

    ToastUtil.show(getActivity(),task.getId() + "");
  }
}

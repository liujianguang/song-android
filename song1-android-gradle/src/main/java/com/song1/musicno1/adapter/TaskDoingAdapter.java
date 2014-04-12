package com.song1.musicno1.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.BaseAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;
import com.song1.musicno1.models.DownLoadManager;
import com.song1.musicno1.util.Global;
import com.song1.musicno1.models.DownLoadManager.Task;
import de.akquinet.android.androlog.Log;

import java.util.List;

/**
 * Created by leovo on 2014/4/9.
 */
public class TaskDoingAdapter extends BaseAdapter implements DownLoadManager.DownLoadListener {

  DownLoadManager downLoadManager;
  Context         context;
  List<Task> dataList              = Lists.newArrayList();
  List<Task> tempTaskOnRunningList = Lists.newArrayList();
  List<Task> taskOnRunningList     = Lists.newArrayList();
  List<Task> taskOnStopList        = Lists.newArrayList();
  Handler    handler               = new Handler();

  boolean  isRefresh = false;
  Runnable refresh   = new Runnable() {
    @Override
    public void run() {
      System.out.println("notifyDataSetChanged");
      notifyDataSetChanged();
      if (taskOnRunningList.size() != 0) {
        handler.postDelayed(refresh, 500);
      }else{
        isRefresh = false;
      }
    }
  };

  public TaskDoingAdapter(Context context) {
    this.context = context;
    downLoadManager = DownLoadManager.getDownLoadManager(context);
  }

  public void onStart() {
    downLoadManager.registerDownLoadListener(this);
    taskOnRunningList = downLoadManager.getTaskListOnRunning();
    onDataChange();
  }

  public void onDestory() {
    downLoadManager.unRegisterDownLoadListener(this);
    handler.post(refresh);
  }

  public void onDataChange() {
    dataList.clear();
    taskOnStopList = downLoadManager.getTaskListOnStop();
    dataList.addAll(taskOnRunningList);
    dataList.addAll(taskOnStopList);
    if (!isRefresh) {
      isRefresh = true;
      handler.post(refresh);
    }
  }

  @Override
  public int getCount() {
    return dataList != null ? dataList.size() : 0;
  }

  @Override
  public Object getItem(int i) {
    return dataList.get(i);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  @Override
  public View getView(int i, View view, ViewGroup viewGroup) {
    ViewHolder holder;
    if (view == null) {
      view = LayoutInflater.from(context).inflate(R.layout.task_doing_list_item, null);
      holder = new ViewHolder(view);
      view.setTag(holder);
    } else {
      holder = (ViewHolder) view.getTag();
    }
    Task task = (Task) getItem(i);
    holder.setTask(task);
    return view;
  }

  @Override
  public void taskListChange(List<Task> taskList) {
    taskOnRunningList = taskList;
    onDataChange();
  }

  @Override
  public void delTask(Task task) {

  }

  public class ViewHolder {
    Task task;
    @InjectView(R.id.fileNameTextView)
    TextView    fileNameTextView;
    @InjectView(R.id.sizeTextView)
    TextView    sizeTextView;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    @InjectView(R.id.startButton)
    ImageButton startButton;
    @InjectView(R.id.delButton)
    ImageButton delButton;

    @OnClick(R.id.startButton)
    public void onStartClick(ImageButton imageButton) {
      //Toast.makeText(context, "startClick...", Toast.LENGTH_SHORT).show();
      if (task.getState() == Task.STATE_DOWNING) {
        downLoadManager.stopTask(task);
        onDataChange();
      } else if (task.getState() == Task.STATE_STOP) {
        downLoadManager.startTask(task);
        onDataChange();
      }
    }

    @OnClick(R.id.delButton)
    public void onDelClick(ImageButton imageButton) {
      //Toast.makeText(context, "delClick...", Toast.LENGTH_SHORT).show();
      downLoadManager.deleteTask(task);
      onDataChange();
    }

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    public void setTask(Task task) {
      this.task = task;
      //Log.d(this,"taskId : " + task.getId());
      progressBar.setMax((int) task.getTotalSize());
      progressBar.setProgress((int) task.getGotSize());
      fileNameTextView.setText(task.getFileName());
      sizeTextView.setText(Global.format(task.getGotSize()) + "/" + Global.format(task.getTotalSize()));
      if (task.getState() == Task.STATE_DOWNING) {
        startButton.setImageResource(R.drawable.stop);
      } else if (task.getState() == Task.STATE_STOP) {
        startButton.setImageResource(R.drawable.start);
      }
    }
  }
}

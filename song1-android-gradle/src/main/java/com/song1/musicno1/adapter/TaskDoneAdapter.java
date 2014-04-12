package com.song1.musicno1.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.song1.musicno1.R;
import com.song1.musicno1.models.DownLoadManager;
import com.song1.musicno1.models.DownLoadManager.Task;
import com.song1.musicno1.util.Global;
import de.akquinet.android.androlog.Log;

import java.util.List;

/**
 * Created by leovo on 2014/4/10.
 */
public class TaskDoneAdapter extends android.widget.BaseAdapter {

  Context         context;
  DownLoadManager downLoadManager;
  List<Task>      dataList;

  Handler handler = new Handler();

  public TaskDoneAdapter(Context context) {
    this.context = context;
    downLoadManager = DownLoadManager.getDownLoadManager(context);
  }

  Runnable runnable = new Runnable() {
    @Override
    public void run() {
      notifyDataSetChanged();
    }
  };

  public void loadData() {
    // new Thread(new Runnable() {
    // @Override
    //public void run() {
    dataList = downLoadManager.getTaskListOnFinish();
    //handler.post(runnable);
    notifyDataSetChanged();
    // }
    //}).start();
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
    ViewHolder viewHolder;

    if (view == null) {
      view = LayoutInflater.from(context).inflate(R.layout.task_done_list_item, null);
      viewHolder = new ViewHolder(view);
      view.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) view.getTag();
    }
    Task task = (Task) getItem(i);
    viewHolder.setTask(task);
    return view;
  }

  public class ViewHolder {
    Task task;
    @InjectView(R.id.fileNameTextView)
    TextView fileNameTextView;
    @InjectView(R.id.sizeTextView)
    TextView sizeTextView;

    @OnClick(R.id.delButton)
    public void delete() {
      downLoadManager.deleteTask(task);
      loadData();
    }

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    public void setTask(Task task) {
      this.task = task;
      fileNameTextView.setText(task.getFileName());
      sizeTextView.setText(Global.format(task.getTotalSize()) + "");
    }
  }
}

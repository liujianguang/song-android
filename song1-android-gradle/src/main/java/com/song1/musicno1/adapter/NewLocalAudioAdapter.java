package com.song1.musicno1.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.song1.musicno1.models.play.Audio;

import java.util.List;

/**
 * Created by leovo on 2014/4/17.
 */
public class NewLocalAudioAdapter extends DataAdapter {

  private Context mContext;
  List<Audio> mAudioList;

  public NewLocalAudioAdapter(Context context) {
    super(context);
    mContext = context;
  }

  @Override
  public void setDataList(List dataList) {
    mAudioList = dataList;
  }

  @Override
  public View getView(int i, View view, ViewGroup viewGroup) {
    return null;
  }
}

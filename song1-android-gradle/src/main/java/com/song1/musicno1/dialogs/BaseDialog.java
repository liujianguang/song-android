package com.song1.musicno1.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.song1.musicno1.R;

/**
 * Created by kate on 14-3-18.
 */
public class BaseDialog extends DialogFragment {


  LinearLayout titleView;
  TextView     titleTextView;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (getView() == null){
      return;
    }
    titleView = (LinearLayout) getView().findViewById(R.id.titleView);
    titleTextView = (TextView) getView().findViewById(R.id.titleTextView);
    if (titleView != null){
      titleView.setBackgroundResource(R.drawable.title_bg);
    }
  }
  public void setTitle(String title){
    if (titleTextView != null)
    titleTextView.setText(title);
  }
}

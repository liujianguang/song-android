package com.song1.musicno1.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.song1.musicno1.R;
import org.w3c.dom.Text;

/**
 * Created by leovo on 2014/4/29.
 */
public class PromptDialog extends DialogFragment {

  View view;
  @InjectView(R.id.title)   TextView titleView;
  @InjectView(R.id.message) TextView messageView;

  @InjectView(R.id.cancel)  Button cancelButton;
  @InjectView(R.id.confirm) Button confirmButton;

  Context mContext;

  public PromptDialog(Context context) {
    mContext = context;
    view = LayoutInflater.from(context).inflate(R.layout.dialog,null);
    ButterKnife.inject(this, view);

  }

  @Override
  public void onResume() {
    super.onResume();
    Window window = getDialog().getWindow();
    DisplayMetrics dm = new DisplayMetrics();
    getActivity().getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
    int width = dm.widthPixels;
    WindowManager.LayoutParams params = window.getAttributes();
    window.setLayout(width - 30, WindowManager.LayoutParams.WRAP_CONTENT);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return view;
  }

  public PromptDialog setTitle(int resId) {
    titleView.setText(resId);
    return this;
  }

  public PromptDialog setTitle(String title) {
    titleView.setText(title);
    return this;
  }

  public PromptDialog setMessage(int resId){
    messageView.setText(resId);
    return this;
  }
  public PromptDialog setMessage(String message) {
    messageView.setText(message);
    return this;
  }

  public PromptDialog setCancelClick(View.OnClickListener clickListener) {
    cancelButton.setOnClickListener(clickListener);
    return this;
  }
  public PromptDialog setConfirmClick(View.OnClickListener clickListener){
    confirmButton.setOnClickListener(clickListener);
    return this;
  }
}

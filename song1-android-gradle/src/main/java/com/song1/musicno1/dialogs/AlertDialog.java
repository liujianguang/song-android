package com.song1.musicno1.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.song1.musicno1.R;

/**
 * Created by windless on 14-5-16.
 */
public class AlertDialog extends DialogFragment {
  public static final int MODE_OK            = 0;
  public static final int MODE_OK_AND_CANCEL = 1;

  @InjectView(R.id.title)   TextView titleView;
  @InjectView(R.id.message) TextView messageView;
  @InjectView(R.id.cancel)  Button   cancelButton;

  protected String   title;
  protected String   message;
  private   Callback callback;

  protected int     mode = MODE_OK;
  private   boolean isOk = false;

  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_alert, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (title != null) {
      titleView.setText(title);
    }

    if (message != null) {
      messageView.setText(message);
    }

    if (mode == MODE_OK) {
      cancelButton.setVisibility(View.GONE);
    }
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    if (callback != null) {
      callback.onDismiss(isOk);
    }
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setButtonMode(int mode) {
    this.mode = mode;
  }

  @OnClick(R.id.ok)
  public void ok() {
    isOk = true;
    dismiss();
  }

  @OnClick(R.id.cancel)
  public void cancel() {
    dismiss();
  }

  public interface Callback {
    void onDismiss(boolean isOK);
  }

}

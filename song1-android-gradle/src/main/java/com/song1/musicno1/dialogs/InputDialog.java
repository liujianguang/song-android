package com.song1.musicno1.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.common.base.Strings;
import com.song1.musicno1.R;

/**
 * Created by windless on 14-4-11.
 */
public class InputDialog extends DialogFragment {
  protected String title;

  @InjectView(R.id.title) TextView titleView;
  @InjectView(R.id.input) EditText editText;

  protected InputListener listener;

  public static InputDialog openWithTitle(String title) {
    InputDialog dialog = new InputDialog();
    dialog.setTitle(title);
    return dialog;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public InputDialog onConfirmed(InputListener listener) {
    this.listener = listener;
    return this;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_input, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (title != null) {
      titleView.setText(title);
    }
  }

  @OnClick(R.id.cancel)
  public void cancel() {
    dismiss();
  }

  @OnClick(R.id.confirm)
  public void confirm() {
    if (listener != null) {
      if (editText.getText() != null) {
        String value = editText.getText().toString().trim();
        if (!Strings.isNullOrEmpty(value)) {
          listener.onInput(editText.getText().toString());
        }
      }
    }
    dismiss();
  }

  public interface InputListener {
    void onInput(String value);
  }
}

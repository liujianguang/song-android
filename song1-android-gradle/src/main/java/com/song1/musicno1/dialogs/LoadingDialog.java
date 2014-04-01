package com.song1.musicno1.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;

/**
 * Created by windless on 3/31/14.
 */
public class LoadingDialog extends DialogFragment {

  @InjectView(R.id.loading_text) TextView loadingTextView;

  private String loadingText;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_loading, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    return dialog;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setCancelable(false);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (loadingText != null) {
      loadingTextView.setText(loadingText);
    }
  }

  public void setText(String text) {
    this.loadingText = text;
  }
}

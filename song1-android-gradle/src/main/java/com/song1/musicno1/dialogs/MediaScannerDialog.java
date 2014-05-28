package com.song1.musicno1.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.song1.musicno1.R;
import com.song1.musicno1.models.MediaScanService;
import de.akquinet.android.androlog.Log;

import java.io.File;

/**
 * Created by windless on 14-5-15.
 */
public class MediaScannerDialog extends DialogFragment implements MediaScanService.Callback {
  private boolean isFinish = false;

  @InjectView(R.id.message) TextView messageView;

  protected MediaScanService mediaScanService;
  protected Callback         callback;

  protected AlertDialog.Builder builder;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    builder = new AlertDialog.Builder(getActivity());
    View view = View.inflate(getActivity(), R.layout.dialog_text, null);
    ButterKnife.inject(this, view);
    return builder.setTitle(R.string.scan)
        .setView(view)
        .setNegativeButton(android.R.string.cancel, (dialog, whichButton) -> dismiss())
        .create();
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getDialog().setCanceledOnTouchOutside(false);

    mediaScanService = new MediaScanService(getActivity());
    mediaScanService.connect(this);
  }

  public void onDismiss(Callback callback) {
    this.callback = callback;
  }

  @Override
  public void onStart() {
    super.onStart();
    if (mediaScanService.isCancelled()) {
      dismiss();
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    mediaScanService.disconnect();
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    if (mediaScanService != null) {
      mediaScanService.disconnect();
    }

    if (callback != null) {
      callback.onDismiss(isFinish);
    }
  }

  @Override
  public void onConnected(MediaScanService service) {
    service.scanAll();
  }

  @Override
  public void onProgress(File file) {
    messageView.setText(getString(R.string.scanning_file, file.getAbsolutePath()));
  }

  @Override
  public void onCompletion(int added, int removed) {
    Log.d(this, "Scanning finished added: " + added + " removed: " + removed);
    isFinish = true;
    dismiss();

  }

  public interface Callback {
    public void onDismiss(boolean isFinish);
  }
}

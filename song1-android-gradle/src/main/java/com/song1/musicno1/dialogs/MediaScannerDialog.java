package com.song1.musicno1.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.song1.musicno1.R;
import com.song1.musicno1.models.MediaScanService;
import de.akquinet.android.androlog.Log;

import java.io.File;

/**
 * Created by windless on 14-5-15.
 */
public class MediaScannerDialog extends DialogFragment implements MediaScanService.Callback {
  @InjectView(R.id.title)         TextView titleView;
  @InjectView(R.id.scanning)      View     scanningView;
  @InjectView(R.id.notice)        View     noticeView;
  @InjectView(R.id.scanning_file) TextView scanningFileTextView;

  private boolean isFinish = false;
  protected Callback         callback;
  protected MediaScanService mediaScanService;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getDialog().setCanceledOnTouchOutside(false);
    titleView.setText(R.string.scan);

    mediaScanService = new MediaScanService(getActivity());
  }

  public void onDismiss(Callback callback) {
    this.callback = callback;
  }

  @OnClick(value = {R.id.cancel, R.id.cancel_scan})
  public void cancel() {
    dismiss();
  }

  @OnClick(R.id.confirm)
  public void startScanning() {
    noticeView.setVisibility(View.GONE);
    scanningView.setVisibility(View.VISIBLE);
    titleView.setText(R.string.sanning);
    mediaScanService.connect(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_scanner, container, false);
    ButterKnife.inject(this, view);
    return view;
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
    Log.d(this, "Scanning " + file.getAbsolutePath());
    scanningFileTextView.setText(file.getAbsolutePath());
  }

  @Override
  public void onCompletion(int added, int removed) {
    Log.d(this, "Scanning finished added: " + added + " removed: " + removed);
    isFinish = true;
    dismiss();
    Toast.makeText(getActivity(), String.format(getString(R.string.scan_compltion), added, removed), Toast.LENGTH_LONG).show();
  }

  public interface Callback {
    public void onDismiss(boolean isFinish);
  }
}

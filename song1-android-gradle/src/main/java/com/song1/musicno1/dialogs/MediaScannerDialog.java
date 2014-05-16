package com.song1.musicno1.dialogs;

import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.song1.musicno1.R;

/**
 * Created by windless on 14-5-15.
 */
public class MediaScannerDialog extends DialogFragment {
  @InjectView(R.id.title)    TextView titleView;
  @InjectView(R.id.scanning) View     scanningView;
  @InjectView(R.id.notice)   View     noticeView;

  private boolean isFinish = false;
  protected Callback callback;


  BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {
        isFinish = true;
        dismiss();
      }
    }
  };

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getDialog().setCanceledOnTouchOutside(false);
    titleView.setText(R.string.scan);

    IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
    intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
    intentFilter.addDataScheme("file");
    getActivity().registerReceiver(refreshReceiver, intentFilter);

  }

  public void onDismiss(Callback callback) {
    this.callback = callback;
  }

  @OnClick(R.id.cancel)
  public void cancel() {
    dismiss();
  }

  @OnClick(R.id.confirm)
  public void startScanning() {
    noticeView.setVisibility(View.GONE);
    scanningView.setVisibility(View.VISIBLE);
    getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory().getAbsolutePath())));
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
    getActivity().unregisterReceiver(refreshReceiver);
    if (callback != null) {
      callback.onDismiss(isFinish);
    }
  }

  public interface Callback {
    public void onDismiss(boolean isFinish);
  }
}

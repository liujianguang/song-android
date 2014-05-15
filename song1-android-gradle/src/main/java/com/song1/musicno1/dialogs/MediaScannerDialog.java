package com.song1.musicno1.dialogs;

import android.content.DialogInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.common.collect.Lists;
import com.song1.musicno1.R;
import de.akquinet.android.androlog.Log;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by windless on 14-5-15.
 */
public class MediaScannerDialog extends DialogFragment {
  protected ScanTask               scanTask;
  private   MediaScannerConnection connection;
  protected int                    allFileCount;

  @InjectView(R.id.progress) ContentLoadingProgressBar progressBar;
  @InjectView(R.id.count)    TextView                  countView;
  @InjectView(R.id.info)     TextView                  infoTextView;
  @InjectView(R.id.title)    TextView                  titleView;
  @InjectView(R.id.scanning) View                      scanningView;
  @InjectView(R.id.notice)   View                      noticeView;

  Handler handler = new Handler();
  private boolean isCanceled = false;
  private boolean isFinish   = false;
  protected Callback callback;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getDialog().setCanceledOnTouchOutside(false);
    titleView.setText(R.string.scan);
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

    MediaScannerConnection.MediaScannerConnectionClient client = new MediaScannerConnection.MediaScannerConnectionClient() {
      @Override
      public void onMediaScannerConnected() {
        scanTask = new ScanTask();
        scanTask.execute(Environment.getExternalStorageDirectory());
      }

      @Override
      public void onScanCompleted(String s, Uri uri) {
        Log.d(this, "Scanning: " + s);
        handler.post(() -> {
          if (isCanceled) return;

          infoTextView.setText(getString(R.string.scanning_file, s));
          progressBar.setProgress(progressBar.getProgress() + 1);
          countView.setText(progressBar.getProgress() + "/" + allFileCount);

          if (progressBar.getProgress() == allFileCount) {
            isFinish = true;
            dismiss();
          }
        });
      }
    };
    connection = new MediaScannerConnection(getActivity(), client);
    connection.connect();
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
    Log.d(this, "Cancel scanner...");
    isCanceled = true;
    if (scanTask != null) {
      scanTask.cancel(true);
    }
    if (connection != null) {
      connection.disconnect();
    }
    if (callback != null) {
      callback.onDismiss(isFinish);
    }
  }

  private void scanFile(List<File> fileList, File scanFile) {
    if (scanFile.isDirectory()) {
      File[] files = scanFile.listFiles();
      if (files != null) {
        for (File file : files) {
          if (file.isDirectory()) {
            scanFile(fileList, scanFile);
          } else {
            fileList.add(file);
          }
        }
      }
    }
  }

  class ScanTask extends AsyncTask<File, File, File> {


    @Override
    protected File doInBackground(File... files) {
      List<File> allFiles = Lists.newArrayList();

      LinkedList<File> fifo = Lists.newLinkedList();
      fifo.push(files[0]);
      while (fifo.size() > 0 && !isCancelled()) {
        File file = fifo.removeFirst();
        if (file.isDirectory()) {
          File[] childFiles = file.listFiles((listingFile) -> {
            return listingFile.isDirectory() || isAudio(listingFile);
          });
          if (childFiles != null && childFiles.length > 0) {
            fifo.addAll(Lists.newArrayList(childFiles));
          }
        } else {
          allFiles.add(file);
          Log.d(this, "Adding file: " + allFiles.size());
        }
      }

      allFileCount = allFiles.size();
      publishProgress(files[0]);

      for (File file : allFiles) {
        if (isCancelled()) break;

        connection.scanFile(file.getAbsolutePath(), getMimeType(file));
      }
      return null;
    }

    private boolean isAudio(File file) {
      String mimeType = getMimeType(file);
      if (mimeType != null) {
        Log.d(this, file.getAbsolutePath() + " - " + mimeType);
        return mimeType.startsWith("audio/");
      }
      return false;
    }

    private String getMimeType(File file) {
      String url = file.toURI().toASCIIString();
      String extension = MimeTypeMap.getFileExtensionFromUrl(url);
      if (extension != null) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
      }
      return null;
    }

    @Override
    protected void onProgressUpdate(File... values) {
      super.onProgressUpdate(values);
      progressBar.setVisibility(View.VISIBLE);
      countView.setVisibility(View.VISIBLE);

      progressBar.setMax(allFileCount);
      progressBar.setProgress(0);
      countView.setText(0 + "/" + allFileCount);
    }

    @Override
    protected void onPostExecute(File file) {
      super.onPostExecute(file);
    }
  }

  public interface Callback {
    public void onDismiss(boolean isFinish);
  }
}

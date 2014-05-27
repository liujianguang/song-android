package com.song1.musicno1.dialogs;


import android.content.DialogInterface;
import android.os.Bundle;
import android.content.*;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
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
import com.song1.musicno1.models.MediaScanService;
import de.akquinet.android.androlog.Log;

import java.io.File;
import de.akquinet.android.androlog.Log;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by windless on 14-5-15.
 */
public class MediaScannerDialog extends DialogFragment implements MediaScanService.Callback {
  @InjectView(R.id.title)         TextView titleView;
  @InjectView(R.id.scanning)      View     scanningView;
  @InjectView(R.id.notice)        View     noticeView;
  @InjectView(R.id.scanning_file) TextView scanningFileTextView;

  private boolean isFinish = false;

  protected MediaScanService mediaScanService;
  protected Callback callback;

  MediaScannerConnection mediaScannerConnection;
  Handler handler = new Handler();

  Runnable exitRunnable = new Runnable() {
    @Override
    public void run() {
      isFinish = true;
      dismiss();
    }
  };

  BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      System.out.println("action : " + action);
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
//    getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
//        Uri.parse("file://" + Environment.getExternalStorageDirectory().getAbsolutePath())));

    mediaScannerConnection=new MediaScannerConnection(getActivity(), client);
//获取连接
    new Thread(new Runnable() {
      @Override
      public void run() {
        mediaScannerConnection.connect();
      }
    }).start();
    //scanfile(Environment.getExternalStorageDirectory());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_scanner, container, false);
    ButterKnife.inject(this, view);
    return view;
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
    Log.d(this, "Scanning " + file.getAbsolutePath());
    scanningFileTextView.setText(file.getAbsolutePath());
  }

  @Override
  public void onCompletion(int added, int removed) {
    Log.d(this, "Scanning finished added: " + added + " removed: " + removed);
    isFinish = true;
    dismiss();

  }

  MediaScannerConnection.MediaScannerConnectionClient client = new MediaScannerConnection.MediaScannerConnectionClient() {

    public void onScanCompleted(String path, Uri uri) {
      // TODO Auto-generated method stub
      System.out.println("onScanCompleted");
//      new Thread(new Runnable() {/
//        @Override
//        public void run() {
//          mediaScannerConnection.disconnect();
          if (isFinish){
            handler.post(exitRunnable);
          }
//        }
//      }).start();

    }

    public void onMediaScannerConnected() {
      // TODO Auto-generated method stub
      System.out.println("onMediaScannerConnected");
      new Thread(new Runnable() {
        @Override
        public void run() {
          scanfile(Environment.getExternalStorageDirectory());
          isFinish = true;
          mediaScannerConnection.disconnect();
        }
      }).start();
    }
  };

  //将指定路径下的文件列出来，更新到媒体数据库
  private void scanfile(File f) {
    LinkedList<File> fifo = Lists.newLinkedList();
    fifo.add(f);
    while (fifo.size() > 0) {
      File first = fifo.removeFirst();
      if (first.isDirectory()) {
        File[] childFiles = first.listFiles((file) -> {
          if (file.isDirectory()) {
            return true;
          } else {
            String fileExtensionFromUrl = MimeTypeMap.getFileExtensionFromUrl(file.toURI().toASCIIString());
            if (fileExtensionFromUrl != null) {
              String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtensionFromUrl);
              return (mimeTypeFromExtension != null && mimeTypeFromExtension.startsWith("audio/"));
            }
            return false;
          }
        });
        if (childFiles != null && childFiles.length > 0) {
          fifo.addAll(Lists.newArrayList(childFiles));
        }
      } else {
        mediaScannerConnection.scanFile(first.getAbsolutePath(), "audio/*");
      }
    }

//    if (f.isDirectory()) {
//      File[] files = f.listFiles();//将指定文件夹下面的文件全部列出来
//      if (files != null) {
//        for (int i = 0; i < files.length; i++) {
//          if (files[i].isDirectory())
//            scanfile(files[i]);
//          else {
//            //调用mediaScannerConnection.scanFile()方法，更新指定类型的文件到数据库中
//            mediaScannerConnection.scanFile(files[i].getAbsolutePath(), "audio/*");
//          }
//        }
//      }
//    }
  }

  public interface Callback {
    public void onDismiss(boolean isFinish);
  }
}

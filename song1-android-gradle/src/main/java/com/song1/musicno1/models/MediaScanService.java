package com.song1.musicno1.models;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import com.google.common.collect.Lists;
import de.akquinet.android.androlog.Log;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;

import static android.provider.MediaStore.Audio.AudioColumns.*;

/**
 * Created by windless on 14-5-20.
 */
public class MediaScanService extends AsyncTask<File, File, Object> implements MediaScannerConnection.MediaScannerConnectionClient {
  protected final MediaScannerConnection mediaScannerConnection;
  protected final Context                context;

  protected Callback callback;

  private int removed = 0;
  private int added   = 0;

  public MediaScanService(Context context) {
    this.context = context;
    mediaScannerConnection = new MediaScannerConnection(context, this);
  }

  public void connect(Callback onConnectedCallback) {
    this.callback = onConnectedCallback;
    mediaScannerConnection.connect();
  }

  public void disconnect() {
    cancel(true);
    mediaScannerConnection.disconnect();
  }

  public void scanAll() {
    execute();
  }

  private void scanSDCard() {
    File sdCardDir = Environment.getExternalStorageDirectory();
    LinkedList<File> fifo = Lists.newLinkedList();
    fifo.add(sdCardDir);
    while (fifo.size() > 0 && !isCancelled()) {
      File scanFile = fifo.removeFirst();
      publishProgress(scanFile);

      if (scanFile.isDirectory()) {
        File[] childFile = scanFile.listFiles((file) -> file.isDirectory() || (isAudio(file) && !isExistsInMediaStore(file)));
        if (childFile != null && childFile.length > 0) {
          fifo.addAll(Arrays.asList(childFile));
        }
      } else {
        added++;
        mediaScannerConnection.scanFile(scanFile.getAbsolutePath(), "audio/*");
      }
    }
  }

  private boolean isAudio(File file) {
    String fileExtension = MimeTypeMap.getFileExtensionFromUrl(file.toURI().toASCIIString());
    if (fileExtension != null) {
      String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
      return mimeType != null && (
          "audio/mpeg".equals(mimeType) ||
              "audio/wav".equals(mimeType) ||
              "audio/x-wav".equals(mimeType) ||
              "audio/flac".equals(mimeType) ||
              "audio/x-ms-wm".equals(mimeType)
      );
    }
    return false;
  }

  private boolean isExistsInMediaStore(File file) {
    boolean isExists = false;
    Cursor cursor = context.getContentResolver().query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{DATA},
        DATA + "=?", new String[]{file.getAbsolutePath()}, null);
    if (cursor != null) {
      if (cursor.moveToFirst()) {
        isExists = cursor.getCount() > 0;
      }
      cursor.close();
    }
    return isExists;
  }

  private int remove(ContentResolver contentResolver, String dataPath) {
    Log.d(this, "Start deleting " + dataPath);
    return contentResolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        MediaStore.Audio.Media.DATA + "=" + dataPath, null);
  }

  private void scanMediaStore() {
    Cursor cursor = context.getContentResolver().query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        new String[]{TITLE, DURATION, ARTIST, _ID, ALBUM, DATA, ALBUM_ID, MIME_TYPE, SIZE},
        MIME_TYPE + " IN (?,?,?,?,?)",
        new String[]{"audio/mpeg", "audio/wav", "audio/x-wav", "audio/flac", "audio/x-ms-wma"},
        TITLE
    );

    if (cursor != null) {
      for (cursor.moveToFirst(); !cursor.isAfterLast() && !isCancelled(); cursor.moveToNext()) {
        String dataPath = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        File file = new File(dataPath);

        publishProgress(file);

        if (!file.exists()) {
          removed += remove(context.getContentResolver(), dataPath);
          Log.d(this, "Remove audio number: " + removed);
        }
      }
      cursor.close();
    }
  }

  public void scanFile(File file, String mimeType) {
    mediaScannerConnection.scanFile(file.getAbsolutePath(), mimeType);
  }

  @Override
  public void onMediaScannerConnected() {
    if (callback != null) {
      callback.onConnected(this);
    }
  }

  @Override
  public void onScanCompleted(String path, Uri uri) {
    Log.d(this, "Scan completed " + path);
  }

  @Override
  protected Object doInBackground(File... files) {
    scanMediaStore();
    scanSDCard();
    return null;
  }

  @Override
  protected void onProgressUpdate(File... values) {
    super.onProgressUpdate(values);
    if (callback != null) {
      callback.onProgress(values[0]);
    }
  }

  @Override
  protected void onPostExecute(Object o) {
    super.onPostExecute(o);
    if (callback != null) {
      callback.onCompletion(added, removed);
    }
  }

  public interface Callback {
    public void onConnected(MediaScanService service);

    public void onProgress(File file);

    public void onCompletion(int added, int removed);
  }
}

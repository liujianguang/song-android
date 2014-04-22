package com.song1.musicno1.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.song1.musicno1.R;
import com.song1.musicno1.helpers.MainBus;
import com.song1.musicno1.helpers.TimeHelper;
import com.song1.musicno1.models.events.play.StartTimerEvent;
import com.song1.musicno1.models.events.play.TimerEvent;

/**
 * Created by windless on 14-4-22.
 */
public class TimerDialog extends DialogFragment implements SeekBar.OnSeekBarChangeListener {
  private static final String TIME = "time";

  @InjectView(R.id.time_seeker) SeekBar  timeSeek;
  @InjectView(R.id.time)        TextView timeView;
  @InjectView(R.id.stop)        Button   stopBtn;

  protected int time = 60 * 60;

  public static TimerDialog newInstance(int time) {
    TimerDialog dialog = new TimerDialog();
    Bundle args = new Bundle();
    args.putInt(TIME, time);
    dialog.setArguments(args);
    return dialog;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_timer, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Bundle arguments = getArguments();
    if (arguments != null) {
      int arg = arguments.getInt(TIME);
      if (arg != 0) {
        time = arg;
        stopBtn.setText(R.string.stop_timer);
      } else {
        stopBtn.setText(android.R.string.cancel);
      }
    }
    timeView.setText(TimeHelper.secondToString(time));
    timeSeek.setOnSeekBarChangeListener(this);
    timeSeek.setProgress(time);
  }

  @OnClick(R.id.stop)
  public void stopTimer() {
    MainBus.post(new StartTimerEvent(0));
    MainBus.post(new TimerEvent(timeSeek.getProgress()));
    dismiss();
  }

  @OnClick(R.id.ok)
  public void confirm() {
    MainBus.post(new StartTimerEvent(timeSeek.getProgress()));
    MainBus.post(new TimerEvent(timeSeek.getProgress()));
    dismiss();
  }

  @Override
  public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
    timeView.setText(TimeHelper.secondToString(i));
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {

  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {

  }
}

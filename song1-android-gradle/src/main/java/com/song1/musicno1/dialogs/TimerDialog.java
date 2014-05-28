package com.song1.musicno1.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
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

  protected int time = 60 * 60;

  public static TimerDialog newInstance(int time) {
    TimerDialog dialog = new TimerDialog();
    Bundle args = new Bundle();
    args.putInt(TIME, time);
    dialog.setArguments(args);
    return dialog;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
    View view = View.inflate(getActivity(), R.layout.dialog_timer, null);
    ButterKnife.inject(this, view);
    return alert.setTitle(R.string.setting_timer)
        .setView(view)
        .setNegativeButton(R.string.stop_timer, (dialog, i) -> stopTimer())
        .setPositiveButton(android.R.string.ok, (dialog, i) -> confirm())
        .create();
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Bundle arguments = getArguments();
    if (arguments != null) {
      int workingTime = arguments.getInt(TIME);
      if (workingTime != 0) {
        time = workingTime;
      }
    }
    timeView.setText(TimeHelper.secondToString(time));
    timeSeek.setOnSeekBarChangeListener(this);
    timeSeek.setProgress(time);
  }

  public void stopTimer() {
    MainBus.post(new StartTimerEvent(0));
    MainBus.post(new TimerEvent(timeSeek.getProgress()));
    dismiss();
  }

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

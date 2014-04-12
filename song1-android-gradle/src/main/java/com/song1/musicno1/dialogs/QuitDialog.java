package com.song1.musicno1.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.song1.musicno1.R;

/**
 * Created by leovo on 2014/4/10.
 */
public class QuitDialog extends DialogFragment implements View.OnClickListener {

  Button notQuitButton;
  Button quitButton;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_quit,null);
    notQuitButton = (Button) view.findViewById(R.id.notQuitButton);
    quitButton = (Button) view.findViewById(R.id.quitButton);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    notQuitButton.setOnClickListener(this);
    quitButton.setOnClickListener(this);
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()){
      case R.id.notQuitButton:
        dismiss();
        break;
      case R.id.quitButton:
        dismiss();
        getActivity().finish();
        break;
    }
  }
}

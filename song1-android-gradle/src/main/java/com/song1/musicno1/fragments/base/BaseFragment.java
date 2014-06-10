package com.song1.musicno1.fragments.base;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.View;
import android.widget.TextView;
import com.song1.musicno1.R;

/**
 * User: windless
 * Date: 13-8-29
 * Time: PM1:48
 */
public class BaseFragment extends Fragment {
  private String title;

  public BaseFragment() {
  }

  public void setTitle(String title) {
    this.title = title;
    FragmentActivity activity = getActivity();
    if (activity != null) {
      ActionBar actionBar = activity.getActionBar();
      if (actionBar != null) {
        actionBar.setTitle(title);
      }
    }

  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    View view = getActionBarActivity().getSupportActionBar().getCustomView();
    if (title != null) {
      getActivity().getActionBar().setTitle(title);
    }
  }

  public ActionBarActivity getActionBarActivity() {
    return (ActionBarActivity) getActivity();
  }

  public ActionMode startActionMode(ActionMode.Callback callback) {
    return getActionBarActivity().startSupportActionMode(callback);
  }
}

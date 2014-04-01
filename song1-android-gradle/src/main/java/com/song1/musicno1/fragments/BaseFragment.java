package com.song1.musicno1.fragments;

import android.support.v4.app.Fragment;
import com.song1.musicno1.R;

/**
 * User: windless
 * Date: 13-8-29
 * Time: PM1:48
 */
public class BaseFragment extends Fragment {
  private String       _title;
  private Fragment _parent;

  private boolean has_home_button = true;
  private boolean has_touch_mode  = true;


  public BaseFragment(){}

  public BaseFragment(String title){
    this._title = title;
  }
  public BaseFragment setTitle(String title) {
    this._title = title;
    return this;
  }

  public BaseFragment setParent(Fragment parent){
    this._parent = parent;
    return this;
  }
  public Fragment getParent(){
    return _parent;
  }
  public void has_home_button(boolean is) {
    has_home_button = is;
  }

  public void has_touch_mode(boolean is) {
    has_touch_mode = is;
  }

  @Override
  public void onResume() {
    super.onResume();
//    SherlockFragmentActivity activity = getSherlockActivity();
//    if (activity instanceof ContainerActivity) {
//      ContainerActivity container = (ContainerActivity) activity;
//      if (!container.isDrawerOpen()) {
//        showTitle();
//        showHomeBtn();
//      }
//    } else {
//      showTitle();
//      showHomeBtn();
//    }
    showTitle();
    showHomeBtn();
  }

  private void showTitle(){
    if (_title != null)
    getActivity().getActionBar().setTitle(_title);
  }

  private void showHomeBtn() {
    if (has_home_button) {
      if (_parent != null) {
        getActivity().getActionBar().setIcon(R.drawable.home_back);
      } else {
        getActivity().getActionBar().setIcon(R.drawable.home_navigation);
      }
    }
  }

  public String getTitle() {
    return _title;
  }

//  public void show(int containerId, BaseFragment fragment) {
//    if (getSherlockActivity() instanceof ContainerActivity) {
//      ContainerActivity activity = (ContainerActivity) getSherlockActivity();
//      activity.show(containerId, fragment);
//    }
//  }
}

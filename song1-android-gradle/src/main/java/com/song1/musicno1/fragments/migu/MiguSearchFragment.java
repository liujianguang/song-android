package com.song1.musicno1.fragments.migu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.song1.musicno1.R;
import com.song1.musicno1.activities.MainActivity;
import com.song1.musicno1.activities.SearchActivity;
import com.song1.musicno1.fragments.BaseFragment;

/**
 * User: windless
 * Date: 13-12-6
 * Time: AM10:29
 */
public class MiguSearchFragment extends BaseFragment implements View.OnFocusChangeListener {
  @InjectView(R.id.search_edit) EditText searchText;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_migu_search, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    searchText.setOnFocusChangeListener(this);
    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

  }

  @OnClick({
      R.id.chinese_male, R.id.chinese_female, R.id.chinese_band,
      R.id.ea_male, R.id.ea_female, R.id.ea_band,
      R.id.jk_male, R.id.jk_female, R.id.jk_band
  })
  public void onItemClick(View view) {
    MiguArtistFragment fragment = new MiguArtistFragment();
    fragment.setCategoryId(view.getId());
    MainActivity mainActivity = (MainActivity) getActivity();
    mainActivity.replaceMain(fragment);
  }

  @Override
  public void onFocusChange(View v, boolean hasFocus) {
    if (hasFocus) {
      startActivity(new Intent(getActivity(), SearchActivity.class));
      v.clearFocus();
    }
  }
}

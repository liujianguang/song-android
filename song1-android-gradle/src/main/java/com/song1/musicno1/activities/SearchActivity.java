package com.song1.musicno1.activities;

import android.os.Bundle;
import android.view.MenuItem;
import com.song1.musicno1.App;
import com.song1.musicno1.R;
import com.song1.musicno1.fragments.SearchFrag;

import javax.inject.Inject;

/**
 * User: windless
 * Date: 14-2-13
 * Time: AM11:48
 */
public class SearchActivity extends BaseActivity {
//  @Inject PlayerAction playerAction;
  SearchFrag   searchFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);

    searchFragment = new SearchFrag();
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.main, searchFragment)
        .commit();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

}

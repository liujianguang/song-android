package com.song1.musicno1.controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.common.base.Strings;
import com.song1.musicno1.R;
import com.song1.musicno1.helpers.List8;
import com.song1.musicno1.models.Favorite;
import com.song1.musicno1.models.play.Audio;

import java.util.List;

/**
 * Created by windless on 14-5-28.
 */
public class AddFavoritesController {
  protected final Context context;

  @InjectView(R.id.input) EditText editText;

  public AddFavoritesController(Context context) {
    this.context = context;
  }

  public void addToFavorite(Audio audio) {
    List<Favorite> favorites = Favorite.getAll();
    List8<String> strings = List8.newList(favorites).map((favorite) -> favorite.name);
    strings.add(0, context.getString(R.string.create_new_favorite));
    AlertDialog.Builder alert = new AlertDialog.Builder(context);
    alert.setTitle(R.string.choose_favorite)
        .setItems(strings.toArray(new String[strings.size()]), (dialog, i) -> {
          dialog.dismiss();
          if (i > 0) {
            Favorite favorite = favorites.get(i - 1);
            if (favorite.isContain(audio)) {
              String toast = String.format(context.getString(R.string.favorite_contains_audio), audio.getTitle(), favorite.name);
              Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
            } else {
              favorite.add(audio);
              showSuccessToast(favorite.name, audio.getTitle());
            }
          } else {
            addToNewFavorite(audio);
          }
        })
        .show();
  }

  private void showSuccessToast(String favoriteName, String audioTitle) {
    String toast = String.format(context.getString(R.string.add_to_favorite), audioTitle, favoriteName);
    Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
  }

  private void addToNewFavorite(Audio audio) {
    AlertDialog.Builder alert = new AlertDialog.Builder(context);
    View view = View.inflate(context, R.layout.dialog_only_input, null);
    ButterKnife.inject(this, view);
    alert.setTitle(R.string.create_new_favorite)
        .setView(view)
        .setPositiveButton(android.R.string.ok, (dialog, i) -> {
          dialog.dismiss();
          if (editText.getText() != null && !Strings.isNullOrEmpty(editText.getText().toString())) {
            Favorite favorite = Favorite.create(editText.getText().toString());
            favorite.add(audio);
            showSuccessToast(favorite.name, audio.getTitle());
          }
        })
        .setNegativeButton(android.R.string.cancel, (dialog, i) -> {
          dialog.dismiss();
        })
        .show();
  }
}

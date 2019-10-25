package edu.cnm.deepdive.blackjack.controller.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import edu.cnm.deepdive.blackjack.R;
import edu.cnm.deepdive.blackjack.controller.fsm.RoundState.RuleVariation;
import edu.cnm.deepdive.blackjack.controller.fsm.SettingsActivity;
import edu.cnm.deepdive.blackjack.service.GoogleSignInService;
import edu.cnm.deepdive.blackjack.viewmodel.MainViewModel;
import java.util.EnumSet;

public class MainActivity extends AppCompatActivity
    implements SharedPreferences.OnSharedPreferenceChangeListener {

  private MainViewModel viewModel;
  private SharedPreferences preferences;
  private int numDecks;
  private EnumSet<RuleVariation> variations = EnumSet.noneOf(RuleVariation.class);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    getLifecycle().addObserver(viewModel);
    preferences = PreferenceManager.getDefaultSharedPreferences(this);
    preferences.registerOnSharedPreferenceChangeListener(this);
    readDefaults();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.options, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    boolean handled = true;
    Intent intent;
    switch (item.getItemId()) {
      case R.id.sign_out:
        signOut();
        break;
      case R.id.user_settings:
        intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        break;
      default:
        handled = super.onOptionsItemSelected(item);
    }
    return handled;
  }

  private void signOut() {
    GoogleSignInService.getInstance().signOut()
        .addOnCompleteListener((task) -> {
          Intent intent = new Intent(this, LoginActivity.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent);
        });
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
    readSettings();
    viewModel.newGame(numDecks, variations);
  }

  private void readSettings() {
    Resources res = getResources();
    numDecks = preferences.getInt(getString(R.string.decks_per_shoe_key),
        res.getInteger(R.integer.decks_in_shoe_def));
    boolean noHoleCardVariation = preferences.getBoolean(getString(R.string.rule_no_hold_card),
        false);
    boolean standOnSoft17 = preferences.getBoolean(getString(R.string.rule_soft_17), false);
    if (noHoleCardVariation) {
      variations.add(RuleVariation.NO_HOLE_CARD);
    }
    if (standOnSoft17) {
      variations.add(RuleVariation.STAND_ON_SOFT_17);
    }
  }

  private void readDefaults() {
    Resources res = getResources();
    numDecks = res.getInteger(R.integer.decks_in_shoe_def);
  }
}

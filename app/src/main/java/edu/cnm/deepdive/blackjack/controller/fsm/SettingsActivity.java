package edu.cnm.deepdive.blackjack.controller.fsm;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreferenceCompat;
import edu.cnm.deepdive.blackjack.R;

public class SettingsActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.settings_activity);
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.settings, new SettingsFragment())
        .commit();
//    ActionBar actionBar = getSupportActionBar();
//    if (actionBar != null) {
//      actionBar.setDisplayHomeAsUpEnabled(true);
//    }
  }

  public static class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
      setPreferencesFromResource(R.xml.root_preferences, rootKey);
//      PreferenceScreen screen = getPreferenceScreen();
//      SeekBarPreference decksInShoe = screen.findPreference(getString(R.string.decks_per_shoe_key));
//      SwitchPreferenceCompat soft17 = screen.findPreference(getString(R.string.rule_soft_17));
//      SwitchPreferenceCompat noHoldCard = screen.findPreference(getString(R.string.rule_no_hold_card));

    }
  }
}
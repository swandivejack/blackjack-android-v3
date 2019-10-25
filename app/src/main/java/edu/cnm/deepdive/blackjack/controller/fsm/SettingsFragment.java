package edu.cnm.deepdive.blackjack.controller.fsm;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import edu.cnm.deepdive.blackjack.R;

public class SettingsFragment extends PreferenceFragmentCompat {

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.root_preferences, rootKey);
  }
}


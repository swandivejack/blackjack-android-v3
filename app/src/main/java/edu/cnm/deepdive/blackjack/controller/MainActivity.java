package edu.cnm.deepdive.blackjack.controller;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import edu.cnm.deepdive.blackjack.R;
import edu.cnm.deepdive.blackjack.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    Button addDeck = findViewById(R.id.start_round);
    addDeck.setOnClickListener((view) -> viewModel.startRound());
  }

}

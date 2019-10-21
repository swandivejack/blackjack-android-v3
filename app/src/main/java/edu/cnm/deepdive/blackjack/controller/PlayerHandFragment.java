package edu.cnm.deepdive.blackjack.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import edu.cnm.deepdive.blackjack.R;
import edu.cnm.deepdive.blackjack.model.pojo.HandWithCards;
import edu.cnm.deepdive.blackjack.viewmodel.MainViewModel;

public class PlayerHandFragment extends HandFragment {

  private FloatingActionButton hitMe;
  private FloatingActionButton stay;
  private boolean staying;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = super.onCreateView(inflater, container, savedInstanceState);
    hitMe = view.findViewById(R.id.hit_me);
    hitMe.setOnClickListener((v) -> {
      hitMe.hide();
      stay.hide();
      getViewModel().hitPlayer();
    });
    stay = view.findViewById(R.id.stay);
    stay.setOnClickListener((v) -> {
      staying = true;
      hitMe.hide();
      stay.hide();
      getViewModel().startDealer();
    });
    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getViewModel().getRound().observe(getActivity(), (round) -> {
      staying = false;
    });
  }

  @Override
  public LiveData<HandWithCards> handToObserve(MainViewModel viewModel) {
    return viewModel.getPlayerHand();
  }

  @Override
  public int getLayout() {
    return R.layout.fragment_player_hand;
  }

  @Override
  protected void updateValues(HandWithCards hand) {
    super.updateValues(hand);
    if (hand.getHardValue() < 21 && !staying) {
      hitMe.show();
      stay.show();
    } else if (hand.getSoftValue() == 21) {
      staying = true;
      hitMe.hide();
      stay.hide();
      getViewModel().startDealer();
    } else {
      hitMe.hide();
      stay.hide();
    }
  }

}

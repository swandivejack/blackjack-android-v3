package edu.cnm.deepdive.blackjack.controller.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import edu.cnm.deepdive.blackjack.R;
import edu.cnm.deepdive.blackjack.controller.fsm.RoundState;
import edu.cnm.deepdive.blackjack.model.pojo.HandWithCards;
import edu.cnm.deepdive.blackjack.viewmodel.MainViewModel;

public class DealerHandFragment extends HandFragment {

  private View handValues;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = super.onCreateView(inflater, container, savedInstanceState);
    handValues = view.findViewById(R.id.hand_values);
    return view;
  }

  @Override
  protected void setupObservers() {
    super.setupObservers();
    getViewModel().getState().observe(this, (state) -> {
      handValues.setVisibility((state.isTerminal() || state == RoundState.DEALER_ACTION)
          ? View.VISIBLE : View.GONE);
    });
  }

  @Override
  public LiveData<HandWithCards> handToObserve(MainViewModel viewModel) {
    return viewModel.getDealerHand();
  }

  @Override
  public int getLayout() {
    return R.layout.fragment_hand;
  }

}

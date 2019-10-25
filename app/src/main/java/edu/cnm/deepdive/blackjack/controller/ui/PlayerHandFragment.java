package edu.cnm.deepdive.blackjack.controller.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.TooltipCompat;
import androidx.lifecycle.LiveData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import edu.cnm.deepdive.blackjack.R;
import edu.cnm.deepdive.blackjack.controller.fsm.RoundState;
import edu.cnm.deepdive.blackjack.model.pojo.HandWithCards;
import edu.cnm.deepdive.blackjack.viewmodel.MainViewModel;

public class PlayerHandFragment extends HandFragment {

  private static final long MINIMUM_CLICK_INTERVAL = 500;

  private FloatingActionButton nextRound;
  private FloatingActionButton hitMe;
  private FloatingActionButton stay;
  private long lastClick;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = super.onCreateView(inflater, container, savedInstanceState);
    Context context = getContext();
    setupButtons(view, context);
    return view;
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
  protected void setupObservers() {
    super.setupObservers();
    getViewModel().getState().observe(getActivity(), (state) -> {
      hideButtons();
      if (state == RoundState.PLAYER_ACTION) {
        hitMe.show();
        stay.show();
      } else if (state == null || state.isTerminal()) {
        nextRound.show();
      }
    });
  }

  private void setupButtons(View view, Context context) {
    nextRound = view.findViewById(R.id.next);
    TooltipCompat.setTooltipText(nextRound, context.getString(R.string.next_round));
    nextRound.setOnClickListener((v) -> handleClick(() -> getViewModel().startRound()));
    hitMe = view.findViewById(R.id.hit_me);
    TooltipCompat.setTooltipText(hitMe, context.getString(R.string.hit_me));
    hitMe.setOnClickListener((v) -> handleClick(() -> getViewModel().hitPlayer()));
    stay = view.findViewById(R.id.stay);
    TooltipCompat.setTooltipText(stay, context.getString(R.string.stay));
    stay.setOnClickListener((v) -> handleClick(() -> getViewModel().stay()));
  }

  private void hideButtons() {
    nextRound.hide();
    hitMe.hide();
    stay.hide();
  }

  private void handleClick(Runnable runnable) {
    long now = System.currentTimeMillis();
    if (now - lastClick > MINIMUM_CLICK_INTERVAL) {
      lastClick = now;
      hideButtons();
      runnable.run();
    }
  }

}

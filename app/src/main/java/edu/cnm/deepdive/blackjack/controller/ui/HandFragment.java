package edu.cnm.deepdive.blackjack.controller.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.cnm.deepdive.blackjack.R;
import edu.cnm.deepdive.blackjack.model.pojo.HandWithCards;
import edu.cnm.deepdive.blackjack.view.CardRecyclerAdapter;
import edu.cnm.deepdive.blackjack.view.CardRecyclerAdapter.OverlapDecoration;
import edu.cnm.deepdive.blackjack.viewmodel.MainViewModel;

public abstract class HandFragment extends Fragment {

  private MainViewModel viewModel;
  private CardRecyclerAdapter adapter;
  private TextView bustedValue;
  private TextView hardValue;
  private TextView hardSoftDivider;
  private TextView softValue;
  private TextView blackjackValue;
  private HandWithCards hand;
  private RecyclerView cards;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(getLayout(), container, false);
    cards = view.findViewById(R.id.cards);
    cards.addItemDecoration(new OverlapDecoration(0,
        (int) getContext().getResources().getDimension(R.dimen.card_overlap)));
    cards.setLayoutManager(
        new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
    bustedValue = view.findViewById(R.id.busted_value);
    hardValue = view.findViewById(R.id.hard_value);
    hardSoftDivider = view.findViewById(R.id.hard_soft_divider);
    softValue = view.findViewById(R.id.soft_value);
    blackjackValue = view.findViewById(R.id.blackjack_value);
    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupObservers();
  }

  protected void setupObservers() {
    viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
    handToObserve(viewModel).observe(this, (hand) -> {
      this.hand = hand;
      adapter = new CardRecyclerAdapter(getContext(), hand);
      cards.setAdapter(adapter);
      updateValues(hand);
    });
    viewModel.getState().observe(this, (state) -> {
      if (adapter != null) {
        adapter.setComplete(state.isTerminal());
      }
    });
  }

  protected void updateValues(HandWithCards hand) {
    int hard = hand.getHardValue();
    int soft = hand.getSoftValue();
    hardValue.setVisibility(View.GONE);
    hardSoftDivider.setVisibility(View.GONE);
    softValue.setVisibility(View.GONE);
    blackjackValue.setVisibility(View.GONE);
    bustedValue.setVisibility(View.GONE);
    if (hand.isBusted()) {
      bustedValue.setText(Integer.toString(hard));
      bustedValue.setVisibility(View.VISIBLE);
    } else if (hand.isBlackjack()) {
      blackjackValue.setVisibility(View.VISIBLE);
    } else {
      hardValue.setText(Integer.toString(hard));
      hardValue.setVisibility(View.VISIBLE);
      if (hand.isSoft()) {
        softValue.setText(Integer.toString(soft));
        softValue.setVisibility(View.VISIBLE);
        hardSoftDivider.setVisibility(View.VISIBLE);
      }
    }
  }

  public abstract LiveData<HandWithCards> handToObserve(MainViewModel viewModel);

  public abstract int getLayout();

  protected MainViewModel getViewModel() {
    return viewModel;
  }

  protected HandWithCards getHand() {
    return hand;
  }

}

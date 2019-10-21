package edu.cnm.deepdive.blackjack.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.cnm.deepdive.blackjack.R;
import edu.cnm.deepdive.blackjack.model.entity.Card;
import edu.cnm.deepdive.blackjack.model.pojo.HandWithCards;
import edu.cnm.deepdive.blackjack.view.CardRecyclerAdapter;
import edu.cnm.deepdive.blackjack.view.CardRecyclerAdapter.OverlapDecoration;
import edu.cnm.deepdive.blackjack.viewmodel.MainViewModel;

public abstract class HandFragment extends Fragment {

  private MainViewModel viewModel;
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
    viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
    handToObserve(viewModel).observe(this, (hand) -> {
      this.hand = hand;
      CardRecyclerAdapter adapter = new CardRecyclerAdapter(getContext(), hand.getCards());
      cards.setAdapter(adapter);
      updateValues(hand);
    });
  }

  protected void updateValues(HandWithCards hand) {
    int hard = hand.getHardValue();
    int soft = hand.getSoftValue();
    int numberCards = hand.getCards().size();
    hardValue.setVisibility(View.GONE);
    hardSoftDivider.setVisibility(View.GONE);
    softValue.setVisibility(View.GONE);
    blackjackValue.setVisibility(View.GONE);
    bustedValue.setVisibility(View.GONE);
    if (hard > 21) {
      bustedValue.setText(Integer.toString(hard));
      bustedValue.setVisibility(View.VISIBLE);
    } else if (soft == 21 && numberCards == 2) {
      blackjackValue.setVisibility(View.VISIBLE);
    } else {
      hardValue.setText(Integer.toString(hard));
      hardValue.setVisibility(View.VISIBLE);
      if (soft > hard) {
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

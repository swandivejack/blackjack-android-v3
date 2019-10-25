package edu.cnm.deepdive.blackjack.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.Transformations;
import edu.cnm.deepdive.blackjack.controller.fsm.RoundState;
import edu.cnm.deepdive.blackjack.model.entity.Card;
import edu.cnm.deepdive.blackjack.model.entity.Hand;
import edu.cnm.deepdive.blackjack.model.entity.Round;
import edu.cnm.deepdive.blackjack.model.entity.Shoe;
import edu.cnm.deepdive.blackjack.model.pojo.HandWithCards;
import edu.cnm.deepdive.blackjack.service.BlackjackDatabase;
import edu.cnm.deepdive.blackjack.service.DeckOfCardsService;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import java.security.SecureRandom;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainViewModel extends AndroidViewModel implements LifecycleObserver {

  private static final int DECKS_IN_SHOE = 6; // Change to control via preferences.

  private BlackjackDatabase database;
  private Random rng;
  private ExecutorService executor;
  private EnumSet<RoundState.RuleVariation> variations;
  private CompositeDisposable pending = new CompositeDisposable();

  private MutableLiveData<Long> roundId;
  private LiveData<Round> round;
  private MutableLiveData<Long> dealerHandId;
  private MutableLiveData<Long> playerHandId;
  private LiveData<HandWithCards> dealerHand;
  private LiveData<HandWithCards> playerHand;
  private LiveData<RoundState> state;

  private long shoeId;
  private int shufflePoint;
  private String shoeKey;
  private boolean shuffleNeeded;
  private int lastDealerBasis;
  private int lastPlayerBasis;

  public MainViewModel(@NonNull Application application) {
    super(application);
    setupUtilityFields();
    setupBaseLiveData();
    setupMappedLiveData();
    startRound();
  }

  @OnLifecycleEvent(Event.ON_STOP)
  public void disposePending() {
    pending.clear();
  }

  private void setupUtilityFields() {
    database = BlackjackDatabase.getInstance();
    rng = new SecureRandom();
    executor = Executors.newSingleThreadExecutor();
    pending = new CompositeDisposable();
    // Change the line below if any rule variations are employed.
    variations = EnumSet.noneOf(RoundState.RuleVariation.class);
  }

  private void setupBaseLiveData() {
    roundId = new MutableLiveData<>();
    dealerHandId = new MutableLiveData<>();
    playerHandId = new MutableLiveData<>();
  }

  private void setupMappedLiveData() {
    round = Transformations.switchMap(roundId,
        (id) -> database.getRoundDao().getByRoundId(id));
    dealerHand = Transformations.switchMap(dealerHandId,
        (id) -> database.getHandDao().getHandWithCards(id));
    playerHand = Transformations.switchMap(playerHandId,
        (id) -> database.getHandDao().getHandWithCards(id));
    PairOfHandsLiveData stateTrigger = new PairOfHandsLiveData(dealerHand, playerHand);
    state = Transformations.map(stateTrigger, (pair) -> {
      RoundState from = ((state != null && state.getValue() != null) ? state.getValue()
          : RoundState.getInitialState());
      RoundState to = RoundState.DEAL;
      HandWithCards dealer = pair.first;
      HandWithCards player = pair.second;
      if (dealer != null && dealer.getCards().size() >= 2
          && player != null && player.getCards().size() >= 2) {
        if ((to = from.getNextState(dealer, player, variations)) == RoundState.DEALER_ACTION) {
          hitDealer();
        }
      }
      return to;
    });
  }

  public LiveData<Round> getRound() {
    return round;
  }

  public LiveData<HandWithCards> getDealerHand() {
    return dealerHand;
  }

  public LiveData<HandWithCards> getPlayerHand() {
    return playerHand;
  }

  public LiveData<RoundState> getState() {
    return state;
  }

  private void createShoe() {
    pending.add(
        DeckOfCardsService.getInstance().newShoe(DECKS_IN_SHOE)
            .subscribeOn(Schedulers.from(executor))
            .subscribe((shoe) -> {
              randomizeShufflePoint(shoe);
              shoeKey = shoe.getShoeKey();
              shoeId = database.getShoeDao().insert(shoe);
              shuffleNeeded = false;
            })
    );
  }

  private void shuffleShoe() {
    pending.add(
        DeckOfCardsService.getInstance().shuffle(shoeKey)
            .subscribeOn(Schedulers.from(executor))
            .subscribe((shoe) -> {
              randomizeShufflePoint(shoe);
              database.getShoeDao().update(shoeId, new Date(), shufflePoint);
              shuffleNeeded = false;
            })
    );
  }

  private void randomizeShufflePoint(Shoe shoe) {
    int start = shoe.getRemaining() / 4;
    int end = shoe.getRemaining() / 3;
    shoe.setShufflePoint(start + rng.nextInt(end - start + 1));
    shufflePoint = shoe.getShufflePoint();
  }

  public void startRound() {
    if (shoeId == 0) {
      createShoe();
    } else if (shuffleNeeded) {
      shuffleShoe();
    }
    executor.submit(() -> {
      Round round = new Round();
      round.setShoeId(shoeId);
      long roundId = database.getRoundDao().insert(round);
      Hand dealer = new Hand();
      dealer.setRoundId(roundId);
      dealer.setDealer(true);
      Hand player = new Hand();
      player.setRoundId(roundId);
      long[] handIds = database.getHandDao().insert(dealer, player);
      for (long handId : handIds) {
        draw(handId, 2);
      }
      this.roundId.postValue(roundId);
      dealerHandId.postValue(handIds[0]);
      playerHandId.postValue(handIds[1]);
      lastDealerBasis = 0;
      lastPlayerBasis = 0;
    });
  }

  public void hitPlayer() {
    executor.submit(() -> {
      List<Card> cards;
      if (state.getValue() == RoundState.PLAYER_ACTION
          && (cards = playerHand.getValue().getCards()).size() > lastPlayerBasis) {
        lastPlayerBasis = cards.size();
        draw(playerHandId.getValue(), 1);
      }
    });
  }

  private void hitDealer() {
    executor.submit(() -> {
      List<Card> cards;
      if (state.getValue() == RoundState.DEALER_ACTION
          && (cards = dealerHand.getValue().getCards()).size() > lastDealerBasis) {
        lastDealerBasis = cards.size();
        draw(dealerHandId.getValue(), 1);
      }
    });
  }

  public void stay() {
    executor.submit(() -> {
      HandWithCards player = playerHand.getValue();
      if (state.getValue() == RoundState.PLAYER_ACTION && !player.isStaying()) {
        player.setStaying(true);
        player.setUpdated(new Date());
        database.getHandDao().update(player);
      }
    });
  }

  private void draw(long handId, int count) {
    pending.add(
        DeckOfCardsService.getInstance().draw(shoeKey, count)
            .subscribeOn(Schedulers.from(executor))
            .subscribe((draw) -> {
              for (Card card : draw.getCards()) {
                card.setHandId(handId);
              }
              if (draw.getRemaining() <= shufflePoint) {
                shuffleNeeded = true;
              }
              database.getCardDao().insert(draw.getCards());
            })
    );
  }

  private static class PairOfHandsLiveData extends MediatorLiveData<Pair<HandWithCards, HandWithCards>> {

    private PairOfHandsLiveData(LiveData<HandWithCards> dealer, LiveData<HandWithCards> player) {
      addSource(dealer, (hand) -> setValue(Pair.create(hand, player.getValue())));
      addSource(player, (hand) -> setValue(Pair.create(dealer.getValue(), hand)));
    }

  }

}

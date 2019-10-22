package edu.cnm.deepdive.blackjack.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import edu.cnm.deepdive.blackjack.model.dao.CardDao;
import edu.cnm.deepdive.blackjack.model.entity.Card;
import edu.cnm.deepdive.blackjack.model.entity.Card.Rank;
import edu.cnm.deepdive.blackjack.model.entity.Card.Suit;
import edu.cnm.deepdive.blackjack.model.entity.Hand;
import edu.cnm.deepdive.blackjack.model.entity.Round;
import edu.cnm.deepdive.blackjack.model.entity.Shoe;
import edu.cnm.deepdive.blackjack.model.pojo.HandWithCards;
import edu.cnm.deepdive.blackjack.service.BlackjackDatabase;
import edu.cnm.deepdive.blackjack.service.DeckOfCardsService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainViewModel extends AndroidViewModel {

  private final BlackjackDatabase database;
  private long shoeId;
  private int shufflePoint;
  private String shoeKey;
  private boolean shuffleNeeded;
  private Random rng;
  private MutableLiveData<Long> roundId;
  private LiveData<Round> round;
  private MutableLiveData<Long> dealerHandId;
  private MutableLiveData<Long> playerHandId;
  private LiveData<HandWithCards> dealerHand;
  private LiveData<HandWithCards> playerHand;
  private ExecutorService executor;

  public MainViewModel(@NonNull Application application) {
    super(application);
    database = BlackjackDatabase.getInstance();
    rng = new SecureRandom(); // TODO Use Mersenne Twister.
    roundId = new MutableLiveData<>();
    round = Transformations.switchMap(roundId,
        (id) -> database.getRoundDao().getByRoundId(id));
    dealerHandId = new MutableLiveData<>();
    dealerHand = Transformations.switchMap(dealerHandId,
        (id) -> database.getHandDao().getHandWithCards(id));
    playerHandId = new MutableLiveData<>();
    playerHand = Transformations.switchMap(playerHandId,
        (id) -> database.getHandDao().getHandWithCards(id));
    executor = Executors.newSingleThreadExecutor();
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

  private void createShoe() {
    DeckOfCardsService.getInstance().newShoe(6)
        .subscribeOn(Schedulers.from(executor)) // Sets thread scheduler for task execution
        .subscribe((shoe) -> { // Sets lambda to invoke on completion.
          int start = shoe.getRemaining() / 4;
          int end = shoe.getRemaining() / 3;
          shufflePoint = start + rng.nextInt(end - start + 1);
          shoeKey = shoe.getShoeKey();
          shoe.setShufflePoint(shufflePoint);
          shoeId = database.getShoeDao().insert(shoe);
          shuffleNeeded = false;
        });
  }

  private void shuffleShoe() {
    DeckOfCardsService.getInstance().shuffle(shoeKey)
        .subscribeOn(Schedulers.from(executor)) // Sets thread scheduler for task execution
        .subscribe((shoe) -> { // Sets lambda to invoke on completion.
          int start = shoe.getRemaining() / 4;
          int end = shoe.getRemaining() / 3;
          shufflePoint = start + rng.nextInt(end - start + 1);
          database.getShoeDao().update(shoeId, new Date(), shufflePoint);
          shuffleNeeded = false;
        });
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
      this.dealerHandId.postValue(handIds[0]);
      this.playerHandId.postValue(handIds[1]);
    });
  }

  public void hitPlayer() {
    executor.submit(() -> {
      if (getPlayerHand().getValue().getHardValue() < 21) {
        CardDao dao = database.getCardDao();
        long handId = playerHandId.getValue();
        draw(handId, 1);
      }
    });
  }

  private void draw(long handId, int count) {
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
        });
  }

  public void startDealer() {
    HandWithCards dealer = dealerHand.getValue();
    if (dealer.getSoftValue() < 18 && dealer.getHardValue() < 17) {
      List<Card> newCards = new LinkedList<>();
      DeckOfCardsService.getInstance().draw(shoeKey, 1)
          .repeatUntil(() -> dealer.getHardValue() >= 17 || dealer.getSoftValue() >= 18)
          .doFinally(() -> database.getCardDao().insert(newCards))
          .subscribeOn(Schedulers.from(executor))
          .subscribe((draw) -> {
            for (Card card : draw.getCards()) {
              card.setHandId(dealerHandId.getValue());
            }
            dealer.getCards().addAll(draw.getCards());
            newCards.addAll(draw.getCards());
            if (draw.getRemaining() <= shufflePoint) {
              shuffleNeeded = true;
            }
          });
    }
  }

}

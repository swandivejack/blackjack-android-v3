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
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainViewModel extends AndroidViewModel {

  private final BlackjackDatabase database;
  private long shoeId;
  private long markerId;
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
        .subscribeOn(Schedulers.io()) // Sets thread scheduler for task execution
        .observeOn(AndroidSchedulers.mainThread()) // Sets thread scheduler for returning results.
        .subscribe((shoe) -> { // Sets lambda to invoke on completion.

          shoeId = database.getShoeDao().insert(shoe);
        });


    Shoe shoe = new Shoe();
    shoeId = database.getShoeDao().insert(shoe);
    List<Card> cards = new ArrayList<>();
    for (int i = 0; i < 6; i++) { // Repeat for # of decks in shoe
      for (Rank rank : Rank.values()) { // Repeat for each rank
        for (Suit suit : Suit.values()) { // Repeat for each suit
          Card card = new Card();
          card.setShoeId(shoeId);
          card.setRank(rank);
          card.setSuit(suit);
          cards.add(card);
        }
      }
    }
    Collections.shuffle(cards, rng);
    int startIndex = cards.size() * 2 / 3;
    int endIndex = cards.size() * 3 / 4;
    int markerPosition = startIndex + rng.nextInt(endIndex - startIndex);
    List<Long> cardIds = database.getCardDao().insert(cards);
    markerId = cardIds.get(markerPosition);
    shoe.setMarkerId(markerId);
    database.getShoeDao().update(shoe);
    shuffleNeeded = false;
  }

  public void startRound() {
    executor.submit(() -> {
      Round round = new Round();
      if (shoeId == 0 || shuffleNeeded) {
        createShoe();
      }
      round.setShoeId(shoeId);
      long roundId = database.getRoundDao().insert(round);
      Hand dealer = new Hand();
      dealer.setRoundId(roundId);
      dealer.setDealer(true);
      Hand player = new Hand();
      player.setRoundId(roundId);
      long[] handIds = database.getHandDao().insert(dealer, player);
      for (long handId : handIds) {
        for (int i = 0; i < 2; i++) {
          Card card = dealTopCard(handId);
        }
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
        Long handId = playerHandId.getValue();
        Card card = dealTopCard(handId);
      }
    });
  }

  private Card dealTopCard(long handId) {
    CardDao cardDao = database.getCardDao();
    Card card = cardDao.getTopCardInShoe(shoeId);
    card.setShoeId(null);
    card.setHandId(handId);
    cardDao.update(card);
    if (card.getId() == markerId) {
      shuffleNeeded = true;
    }
    return card;
  }

  public void startDealer() {
    executor.submit(() -> {
      long handId = dealerHandId.getValue();
      HandWithCards dealer = dealerHand.getValue();
      List<Card> cards = dealer.getCards();
      while (dealer.getHardValue() < 17 || dealer.getSoftValue() < 18) {
        cards.add(dealTopCard(handId));
      }
    });
  }

}

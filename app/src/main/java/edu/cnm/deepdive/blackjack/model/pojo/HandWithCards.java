package edu.cnm.deepdive.blackjack.model.pojo;

import androidx.room.Ignore;
import androidx.room.Relation;
import edu.cnm.deepdive.blackjack.model.entity.Card;
import edu.cnm.deepdive.blackjack.model.entity.Card.Rank;
import edu.cnm.deepdive.blackjack.model.entity.Hand;
import java.util.List;

public class HandWithCards extends Hand {

  @Ignore
  private final Object lock = new Object();
  @Ignore
  private int hardValue;
  @Ignore
  private int softValue;
  @Ignore
  private int basis;

  @Relation(entity = Card.class, entityColumn = "hand_id", parentColumn = "hand_id")
  private List<Card> cards;

  public List<Card> getCards() {
    return cards;
  }

  public void setCards(List<Card> cards) {
    this.cards = cards;
  }

  public int getHardValue() {
    synchronized (lock) {
      computeValue();
    }
    return hardValue;
  }

  public int getSoftValue() {
    synchronized (lock) {
      computeValue();
    }
    return softValue;
  }

  private void computeValue() {
    if (basis != cards.size()) {
      basis = cards.size();
      hardValue = 0;
      boolean hasAce = false;
      for (Card card : cards) {
        hardValue += Math.min(card.getRank().ordinal(), 9) + 1;
        if (card.getRank() == Rank.ACE) {
          hasAce = true;
        }
      }
      softValue = hardValue + ((hasAce && hardValue < 12) ? 10 : 0);
    }
  }

}

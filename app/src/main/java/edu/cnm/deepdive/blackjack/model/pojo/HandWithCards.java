package edu.cnm.deepdive.blackjack.model.pojo;

import androidx.room.Ignore;
import androidx.room.Relation;
import edu.cnm.deepdive.blackjack.model.entity.Card;
import edu.cnm.deepdive.blackjack.model.entity.Card.Rank;
import edu.cnm.deepdive.blackjack.model.entity.Hand;
import java.util.EnumSet;
import java.util.List;

public class HandWithCards extends Hand {

  private static final EnumSet<Rank> BLACKJACK_UP_CARD_RANKS =
      EnumSet.of(Rank.ACE, Rank.TEN, Rank.JACK, Rank.QUEEN, Rank.KING);

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

  public boolean isBlackjack() {
    return (basis == 2 && getSoftValue() == 21);
  }

  public boolean isPossibleBlackjack() {
    return (basis == 2 && isDealer() && BLACKJACK_UP_CARD_RANKS.contains(cards.get(1).getRank()));
  }

  public boolean isBusted() {
    return getHardValue() > 21;
  }

  public int getHardValue() {
    computeValue();
    return hardValue;
  }

  public int getSoftValue() {
    computeValue();
    return softValue;
  }

  public boolean isSoft() {
    return getHardValue() < getSoftValue();
  }

  private synchronized void computeValue() {
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

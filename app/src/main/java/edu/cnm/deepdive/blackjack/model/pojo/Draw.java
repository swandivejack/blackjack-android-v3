package edu.cnm.deepdive.blackjack.model.pojo;

import com.google.gson.annotations.SerializedName;
import edu.cnm.deepdive.blackjack.model.entity.Card;
import java.util.List;

public class Draw {

  private List<Card> cards;

  private int remaining;

  @SerializedName("success")
  private boolean successful;

  public List<Card> getCards() {
    return cards;
  }

  public void setCards(List<Card> cards) {
    this.cards = cards;
  }

  public int getRemaining() {
    return remaining;
  }

  public void setRemaining(int remaining) {
    this.remaining = remaining;
  }

  public boolean isSuccessful() {
    return successful;
  }

  public void setSuccessful(boolean successful) {
    this.successful = successful;
  }

}

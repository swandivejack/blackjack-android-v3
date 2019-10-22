package edu.cnm.deepdive.blackjack.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;
import java.util.Date;

@Entity(
    foreignKeys = {
        @ForeignKey(
            entity = Hand.class,
            childColumns = "hand_id",
            parentColumns = "hand_id",
            onDelete = ForeignKey.CASCADE
        )
    }
)
public class Card {

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "card_id")
  private long id;

  @NonNull
  @ColumnInfo(index = true)
  private Date created = new Date();

  @ColumnInfo(name = "hand_id", index = true)
  private long handId;

  @NonNull
  @SerializedName("value")
  private Rank rank;

  @NonNull
  private Suit suit;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @NonNull
  public Date getCreated() {
    return created;
  }

  public void setCreated(@NonNull Date created) {
    this.created = created;
  }

  public long getHandId() {
    return handId;
  }

  public void setHandId(long handId) {
    this.handId = handId;
  }

  @NonNull
  public Rank getRank() {
    return rank;
  }

  public void setRank(@NonNull Rank rank) {
    this.rank = rank;
  }

  @NonNull
  public Suit getSuit() {
    return suit;
  }

  public void setSuit(@NonNull Suit suit) {
    this.suit = suit;
  }

  @Override
  public String toString() {
    return rank.getSymbol() + suit.getSymbol();
  }

  public String getAbbreviation() {
    return rank.getAbbreviation() + suit.getAbbreviation();
  }

  public enum Rank {

    ACE,
    @SerializedName("2") TWO,
    @SerializedName("3") THREE,
    @SerializedName("4") FOUR,
    @SerializedName("5") FIVE,
    @SerializedName("6") SIX,
    @SerializedName("7") SEVEN,
    @SerializedName("8") EIGHT,
    @SerializedName("9") NINE,
    @SerializedName("10") TEN,
    JACK,
    QUEEN,
    KING;

    private static final String[] SYMBOLS =
        {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    private static final String[] ABBREVIATIONS =
        {"A", "2", "3", "4", "5", "6", "7", "8", "9", "0", "J", "Q", "K"};

    public String getSymbol() {
      return SYMBOLS[ordinal()];
    }

    public String getAbbreviation() {
      return ABBREVIATIONS[ordinal()];
    }

  }

  public enum Suit {

    CLUBS,
    DIAMONDS,
    HEARTS,
    SPADES;

    private static final String[] SYMBOLS = {"\u2663", "\u2662", "\u2661", "\u2660"};

    private static final String[] ABBREVIATIONS = {"C", "D", "H", "S"};

    public String getSymbol() {
      return SYMBOLS[ordinal()];
    }

    public Color getColor() {
      return (ordinal() % 3 == 0) ? Color.BLACK : Color.RED;
    }

    public String getAbbreviation() {
      return ABBREVIATIONS[ordinal()];
    }

    public enum Color {
      BLACK, RED;
    }

  }

}

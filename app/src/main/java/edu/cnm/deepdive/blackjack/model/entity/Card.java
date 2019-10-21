package edu.cnm.deepdive.blackjack.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(
    foreignKeys = {
        @ForeignKey(
            entity = Shoe.class,
            childColumns = "shoe_id",
            parentColumns = "shoe_id",
            onDelete = ForeignKey.CASCADE
        ),
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

  @NonNull
  @ColumnInfo(index = true)
  private Date updated  = new Date();

  @ColumnInfo(name = "shoe_id", index = true)
  private Long shoeId;

  @ColumnInfo(name = "hand_id", index = true)
  private Long handId;

  @NonNull
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

  @NonNull
  public Date getUpdated() {
    return updated;
  }

  public void setUpdated(@NonNull Date updated) {
    this.updated = updated;
  }

  public Long getShoeId() {
    return shoeId;
  }

  public void setShoeId(Long shoeId) {
    this.shoeId = shoeId;
  }

  public Long getHandId() {
    return handId;
  }

  public void setHandId(Long handId) {
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
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    TEN,
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

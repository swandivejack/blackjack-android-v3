package edu.cnm.deepdive.blackjack.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.util.TableInfo.Column;
import com.google.gson.annotations.SerializedName;
import java.util.Date;

@Entity(indices = @Index(value = "shoe_key", unique = true))
public class Shoe {

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "shoe_id")
  private long id;

  @ColumnInfo(index = true)
  @NonNull
  private Date created = new Date();

  @ColumnInfo(index = true)
  @NonNull
  private Date updated = new Date();

  @ColumnInfo(name = "shuffle_point")
  private int shufflePoint;

  @SerializedName("deck_id")
  @NonNull
  @ColumnInfo(name = "shoe_key")
  private String shoeKey;

  @Ignore
  private int remaining;

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

  public int getShufflePoint() {
    return shufflePoint;
  }

  public void setShufflePoint(int shufflePoint) {
    this.shufflePoint = shufflePoint;
  }

  @NonNull
  public String getShoeKey() {
    return shoeKey;
  }

  public void setShoeKey(@NonNull String shoeKey) {
    this.shoeKey = shoeKey;
  }

  public int getRemaining() {
    return remaining;
  }

  public void setRemaining(int remaining) {
    this.remaining = remaining;
  }

}

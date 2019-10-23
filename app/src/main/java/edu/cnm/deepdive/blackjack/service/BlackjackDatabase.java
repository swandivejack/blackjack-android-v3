package edu.cnm.deepdive.blackjack.service;

import android.app.Application;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import edu.cnm.deepdive.blackjack.model.dao.CardDao;
import edu.cnm.deepdive.blackjack.model.dao.HandDao;
import edu.cnm.deepdive.blackjack.model.dao.RoundDao;
import edu.cnm.deepdive.blackjack.model.dao.ShoeDao;
import edu.cnm.deepdive.blackjack.model.entity.Card;
import edu.cnm.deepdive.blackjack.model.entity.Card.Rank;
import edu.cnm.deepdive.blackjack.model.entity.Card.Suit;
import edu.cnm.deepdive.blackjack.model.entity.Hand;
import edu.cnm.deepdive.blackjack.model.entity.Hand.Outcome;
import edu.cnm.deepdive.blackjack.model.entity.Round;
import edu.cnm.deepdive.blackjack.model.entity.Shoe;
import java.util.Date;

@Database(
    entities = {Card.class, Hand.class, Round.class, Shoe.class},
    version = 1, exportSchema = true
)
@TypeConverters(BlackjackDatabase.Converters.class)
public abstract class BlackjackDatabase extends RoomDatabase {

  private static Application applicationContext;

  public static void setApplicationContext(Application applicationContext) {
    BlackjackDatabase.applicationContext = applicationContext;
  }

  public static BlackjackDatabase getInstance() {
    return InstanceHolder.INSTANCE;
  }

  public abstract ShoeDao getShoeDao();

  public abstract CardDao getCardDao();

  public abstract RoundDao getRoundDao();

  public abstract HandDao getHandDao();

  private static class InstanceHolder {

    private static final BlackjackDatabase INSTANCE;

    static {
      INSTANCE =
          Room.databaseBuilder(applicationContext, BlackjackDatabase.class, "blackjack_db").build();
    }

  }

  public static class Converters {

    @TypeConverter
    public Long dateToLong(Date date) {
      return (date != null) ? date.getTime() : null;
    }

    @TypeConverter
    public Date longToDate(Long milliseconds) {
      return (milliseconds != null) ? new Date(milliseconds) : null;
    }

    @TypeConverter
    public String enumToString(Enum value) {
      return (value != null) ? value.toString() : null;
    }

    @TypeConverter
    public Outcome stringToOutcome(String name) {
      return (name != null) ? Outcome.valueOf(name) : null;
    }

    @TypeConverter
    public Rank stringToRank(String name) {
      return (name != null) ? Rank.valueOf(name) : null;
    }

    @TypeConverter
    public Suit stringToSuit(String name) {
      return (name != null) ? Suit.valueOf(name) : null;
    }

  }

}

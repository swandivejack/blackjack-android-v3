package edu.cnm.deepdive.blackjack.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import edu.cnm.deepdive.blackjack.model.entity.Shoe;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Dao
public interface ShoeDao {

  @Insert
  long insert(Shoe shoe);

  @Insert
  long[] insert(Shoe... shoes);

  @Insert
  List<Long> insert(Collection<Shoe> shoes);

  @Query("SELECT * FROM Shoe ORDER BY shoe_id ASC")
  LiveData<List<Shoe>> getAll();

  @Query("SELECT * FROM Shoe WHERE shoe_id = :shoeId")
  LiveData<Shoe> getById(long shoeId);

  @Query("UPDATE Shoe SET updated = :updated, shuffle_point = :shufflePoint WHERE shoe_id = :shoeId")
  int update(long shoeId, Date updated, int shufflePoint);

  @Update
  int update(Shoe... shoes);

  @Delete
  int delete(Shoe... shoes);

}

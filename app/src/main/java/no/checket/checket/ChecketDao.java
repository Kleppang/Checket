package no.checket.checket;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ChecketDao {
    @Query("Select * from AchievementsTable")
    List<Achievement> loadAllAchievements();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAchievement(Achievement achievement);
}

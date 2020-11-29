package no.checket.checket;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ChecketDao {
    // Achievements
    @Query("Select * from AchievementsTable")
    List<Achievement> loadAllAchievements();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAchievement(Achievement achievement);

    @Query("Select * from AchievementsTable where name = :thisName")
    Achievement getAchievement(String thisName);

    // Tasks
    @Query("Select * from TasksTable")
    List<no.checket.checket.Task> loadAllTasks();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(Task task);
}

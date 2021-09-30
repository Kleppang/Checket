package no.checket.checket;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "AchievementsTable")
public class Achievement implements Comparable<Achievement> {
    @ColumnInfo
    @PrimaryKey()
    @NonNull
    private String name;
    @ColumnInfo
    private String desc;
    @ColumnInfo
    private String category;

    public Achievement(String name, String desc, String category) {
        this.name = name;
        this.desc = desc;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public int compareTo(Achievement achievement) {
        return this.getName().compareTo(achievement.getName());
    }
}

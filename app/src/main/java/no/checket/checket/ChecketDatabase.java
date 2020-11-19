package no.checket.checket;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Achievement.class}, version = 1)
public abstract class ChecketDatabase extends RoomDatabase {

    public static final String DB_NAME = "ChecketDB";
    private static ChecketDatabase INSTANCE;
    public abstract ChecketDao checketDao();

    static ChecketDatabase getDatabase(final Context context) {
        if(INSTANCE == null) {
            synchronized (ChecketDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ChecketDatabase.class, DB_NAME).fallbackToDestructiveMigration().build();
            }
        }
        return INSTANCE;
    }
}

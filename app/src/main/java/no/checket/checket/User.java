package no.checket.checket;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Blob;

@Entity(tableName = "UserTable")
public class User {
    @ColumnInfo
    @PrimaryKey()
    @NonNull
    private String username;
    @ColumnInfo
    private Blob profilepic;

    public User(String username, Blob profilepic) {
        this.username = username;
        this.profilepic = profilepic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Blob getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(Blob profilepic) {
        this.profilepic = profilepic;
    }
}

package no.checket.checket;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity(tableName = "TasksTable")
public class Task {
    @ColumnInfo
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;
    @ColumnInfo
    private String header;
    @ColumnInfo
    private String details;
    @ColumnInfo
    private long date;
    @ColumnInfo
    private String icon;

    public Task(String header, String details, long date, String icon) {
        this.header = header;
        this.details = details;
        this.date = date;
        this.icon = icon;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}

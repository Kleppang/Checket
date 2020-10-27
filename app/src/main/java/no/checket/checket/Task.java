package no.checket.checket;

import java.util.Date;

public class Task {
    private String header;
    private String details;
    private Date date;
    private String icon;

    public Task(String header, String details, Date date, String icon) {
        this.header = header;
        this.details = details;
        this.date = date;
        this.icon = icon;

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}

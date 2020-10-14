package no.checket.checket;

public class Task {
    private String header;
    private String details;
    private String date;
    private String icon;

    public Task(String header, String details, String date, String icon) {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}

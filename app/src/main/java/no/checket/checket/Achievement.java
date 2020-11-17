package no.checket.checket;

public class Achievement {
    private String name;
    private String desc;

    public Achievement(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}

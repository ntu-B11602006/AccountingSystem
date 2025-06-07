import java.io.Serializable;

public class Reminder implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private int dayOfMonth; // 每月幾號

    public Reminder(String name, int dayOfMonth) {
        this.name = name;
        this.dayOfMonth = dayOfMonth;
    }

    public String getName() { return name; }
    public int getDayOfMonth() { return dayOfMonth; }
    public void setDayOfMonth(int dayOfMonth) { this.dayOfMonth = dayOfMonth; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return name + "（每月" + dayOfMonth + "號）";
    }
}
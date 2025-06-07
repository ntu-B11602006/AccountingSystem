import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ReminderManager {
    private static final String REMINDER_FILE = "reminders.ser";
    private final List<Reminder> reminders = new ArrayList<>();

    // 預設提醒
    private static final String[] DEFAULT_NAMES = {"房租", "通話費", "水電費"};
    private static final int DEFAULT_DAY = 15;

    public ReminderManager() {
        loadReminders();
        if (reminders.isEmpty()) {
            for (String name : DEFAULT_NAMES) {
                reminders.add(new Reminder(name, DEFAULT_DAY));
            }
            saveReminders();
        }
    }

    public List<Reminder> getReminders() {
        return reminders;
    }

    public void addReminder(Reminder reminder) {
        reminders.add(reminder);
        saveReminders();
    }

    public void removeReminder(int idx) {
        if (idx >= 0 && idx < reminders.size()) {
            reminders.remove(idx);
            saveReminders();
        }
    }

    public void updateReminderDate(int idx, int newDay) {
        if (idx >= 0 && idx < reminders.size()) {
            reminders.get(idx).setDayOfMonth(newDay);
            saveReminders();
        }
    }

    public void saveReminders() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(REMINDER_FILE))) {
            oos.writeObject(new ArrayList<>(reminders));
        } catch (IOException e) {
            System.err.println("提醒資料儲存失敗: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadReminders() {
        File file = new File(REMINDER_FILE);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(REMINDER_FILE))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                reminders.clear();
                reminders.addAll((List<Reminder>) obj);
            }
        } catch (Exception e) {
            System.err.println("提醒資料載入失敗: " + e.getMessage());
        }
    }

    // 計算距離下次繳費的天數
    public int calculateDaysRemaining(int targetDay) {
        LocalDate today = LocalDate.now();
        
        // 取得當前月份的有效目標日
        YearMonth currentMonth = YearMonth.from(today);
        int validDay = Math.min(targetDay, currentMonth.lengthOfMonth());
        LocalDate targetDate = today.withDayOfMonth(validDay);
        
        // 判斷是否需計算下個月
        if (today.isAfter(targetDate)) {
            YearMonth nextMonth = currentMonth.plusMonths(1);
            int nextValidDay = Math.min(targetDay, nextMonth.lengthOfMonth());
            targetDate = nextMonth.atDay(nextValidDay);
        }
        
        // 使用 ChronoUnit 計算天數差
        return (int) ChronoUnit.DAYS.between(today, targetDate);
    }
}
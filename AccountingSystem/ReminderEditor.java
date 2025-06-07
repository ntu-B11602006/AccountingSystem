import java.util.List;
import java.util.Scanner;

public class ReminderEditor {
    private final ReminderManager reminderManager;
    private final Scanner scanner;

    public ReminderEditor(ReminderManager reminderManager, Scanner scanner) {
        this.reminderManager = reminderManager;
        this.scanner = scanner;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== 編輯提醒 ===");
            System.out.println("1. 新增提醒");
            System.out.println("2. 刪除提醒");
            System.out.println("3. 更改日期");
            System.out.println("0. 返回主選單");
            System.out.print("請選擇操作：");
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> addReminder();
                case "2" -> deleteReminder();
                case "3" -> changeDate();
                case "0" -> { return; }
                default -> System.out.println("無效輸入");
            }
        }
    }

    private void addReminder() {
        System.out.print("請輸入提醒名稱：");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("名稱不能為空");
            return;
        }
        for (Reminder r : reminderManager.getReminders()) {
            if (r.getName().equals(name)) {
                System.out.println("提醒名稱已存在");
                return;
            }
        }
        int day = inputDayOfMonth();
        if (day == -1) return;
        reminderManager.addReminder(new Reminder(name, day));
        System.out.println("新增成功！");
    }

    private void deleteReminder() {
        List<Reminder> list = reminderManager.getReminders();
        if (list.isEmpty()) {
            System.out.println("沒有提醒可刪除");
            return;
        }
        showReminders();
        System.out.print("請輸入要刪除的提醒編號（0返回）：");
        try {
            int idx = Integer.parseInt(scanner.nextLine());
            if (idx == 0) return;
            if (idx < 1 || idx > list.size()) {
                System.out.println("無效編號");
                return;
            }
            reminderManager.removeReminder(idx - 1);
            System.out.println("刪除成功！");
        } catch (NumberFormatException e) {
            System.out.println("請輸入數字");
        }
    }

    private void changeDate() {
        List<Reminder> list = reminderManager.getReminders();
        if (list.isEmpty()) {
            System.out.println("沒有提醒可修改");
            return;
        }
        showReminders();
        System.out.print("請輸入要更改日期的提醒編號（0返回）：");
        try {
            int idx = Integer.parseInt(scanner.nextLine());
            if (idx == 0) return;
            if (idx < 1 || idx > list.size()) {
                System.out.println("無效編號");
                return;
            }
            int newDay = inputDayOfMonth();
            if (newDay == -1) return;
            reminderManager.updateReminderDate(idx - 1, newDay);
            System.out.println("修改成功！");
        } catch (NumberFormatException e) {
            System.out.println("請輸入數字");
        }
    }

    private int inputDayOfMonth() {
        while (true) {
            System.out.print("請輸入每月幾號（1-31）：");
            String input = scanner.nextLine();
            try {
                int day = Integer.parseInt(input);
                if (day >= 1 && day <= 31) return day;
                System.out.println("請輸入1~31的數字");
            } catch (NumberFormatException e) {
                System.out.println("請輸入數字");
            }
        }
    }

    private void showReminders() {
        List<Reminder> list = reminderManager.getReminders();
        for (int i = 0; i < list.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, list.get(i));
        }
    }
}
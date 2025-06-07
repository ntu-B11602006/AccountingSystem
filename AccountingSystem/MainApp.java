// MainApp.java - 主程式入口（加入資料持久化功能）

import java.io.File; // <--- 雖然沒直接用，但相關操作會產生檔案
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
/**
 * 記帳系統主應用程式
 * ----------------
 * 系統入口點，提供互動式命令行界面
 * 管理用戶輸入和菜單顯示
 */
public class MainApp {
	
	// --- 定義檔案名稱常數 ---
    private static final String EXPENSES_FILE = "expenses.ser";
    private static final String CATEGORIES_FILE = "categories.ser";
    // 記帳管理器
    private static final ExpenseManager manager = new ExpenseManager();
    // 輸入掃描器
    private static final Scanner scanner = new Scanner(System.in);
    // 分類管理器
    private static final CategoryManager categoryManager = new CategoryManager();
    private static final ReminderManager reminderManager = new ReminderManager();
    /**
     * 程式入口點
     * 載入資料或初始化，然後啟動主選單
     *
     * @param args 命令行參數（未使用）
     */
    public static void main(String[] args) {
        // --- 載入資料 ---
        System.out.println("正在載入分類資料...");
        CategoryManager.loadCategories(CATEGORIES_FILE); // 載入靜態分類

        System.out.println("正在載入記帳記錄...");
        manager.loadExpenses(EXPENSES_FILE); // 載入 manager 實例的記錄
        System.out.println("正在載入提醒記錄...");
        System.out.println("提醒記錄已從 reminders.ser 載入。");
        // --- 移除範例資料載入 ---
        /*
        // 載入範例資料 (移除或註解掉)
        for (Expense e : SampleDataProvider.getSampleExpenses()) {
            manager.addExpense(e);
        }
        */

        System.out.println("\n--- 資料載入完成 ---\n");

        // 顯示主選單
        
        showMainMenu();
    }

    private static void showReminders() {
        List<Reminder> reminders = reminderManager.getReminders();
        System.out.println("\n==== 繳費提醒 ====");
        for (Reminder reminder : reminders) {
            String item = reminder.getName();
            int targetDay = reminder.getDayOfMonth();
            int daysLeft = reminderManager.calculateDaysRemaining(targetDay);
            
            if (daysLeft == 0) {
                System.out.println("今天要繳" + item + "！");
            } else {
                System.out.println(item + "還有 " + daysLeft + " 天要繳費");
            }
        }
        System.out.println("==================");
    }
    
    /**
     * 年份選擇輔助方法
     * 提示用戶輸入有效的年份
     *
     * @return 用戶選擇的年份
     */
    private static int selectYear() {
        while (true) {
            System.out.print("請輸入年份（YYYY）：");
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("年份格式錯誤！");
            }
        }
    }

    /**
     * 月份選擇輔助方法
     * 提示用戶輸入有效的月份（1-12）
     *
     * @return 用戶選擇的月份
     */
    private static int selectMonth() {
        while (true) {
            System.out.print("請輸入月份（1-12）：");
            String input = scanner.nextLine().trim();
            try {
                int month = Integer.parseInt(input);
                if (month >= 1 && month <= 12) return month;
                System.out.println("月份範圍錯誤！");
            } catch (NumberFormatException e) {
                System.out.println("月份格式錯誤！");
            }
        }
    }

    /**
     * 顯示主選單
     * 系統的主要互動界面
     */
    private static void showMainMenu() {
        while (true) {
        	showReminders();
            System.out.println("\n=== 記帳系統主選單 ===");
            System.out.println("1. 新增記錄");
            System.out.println("2. 查看記錄");
            System.out.println("3. 編輯分類");
            System.out.println("4. 離開系統");
            System.out.println("5. 刪除記錄");
            System.out.println("6. 編輯提醒");
            System.out.print("請選擇操作：");
            switch (scanner.nextLine().trim()) {
                case "1" -> addExpense();
                case "2" -> viewExpenses();
                // 將靜態的 categoryManager 傳給 CategoryEditor
                case "3" -> new CategoryEditor(categoryManager, scanner).showCategoryMenu();
                case "4" -> exitSystem();
                case "5" -> {
                	List<Expense> allExpenses = manager.getExpenses();
                    if (allExpenses.isEmpty()) {
                        System.out.println("目前沒有記帳記錄可刪除。");
                        break;
                    }

                    System.out.println("\n=== 所有記帳記錄 ===");
                    for (int i = 0; i < allExpenses.size(); i++) {
                        System.out.println((i + 1) + ". " + allExpenses.get(i));
                    }

                    System.out.print("請輸入要刪除的記錄編號（輸入 0 取消）：");
                    int indexToDelete;
                    try {
                        indexToDelete = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("輸入格式錯誤，請輸入數字。");
                        break;
                    }

                    if (indexToDelete == 0) {
                        System.out.println("取消刪除操作。");
                        break;
                    }

                    if (indexToDelete < 1 || indexToDelete > allExpenses.size()) {
                        System.out.println("錯誤：無效的編號。");
                        break;
                    }

                    System.out.print("確定要刪除這筆記錄嗎？(y/n)：");
                    String confirm = scanner.nextLine().trim().toLowerCase();
                    if (confirm.equals("y")) {
                    	manager.deleteExpense(indexToDelete - 1); // 減 1 是因為列表是從 0 開始
                    } else {
                        System.out.println("取消刪除。");
                    }
                }
                case "6" -> new ReminderEditor(reminderManager, scanner).showMenu();
                default -> System.out.println("無效輸入");
            }   
        }
    }

    /**
     * 新增記帳記錄流程
     * 引導用戶輸入記帳記錄的各項資訊
     */
    private static void addExpense() {
        try {
            // 選擇類型
            TransactionType type = selectTransactionType();
            if (type == null) return; // 用戶取消

            // 多層分類選擇
            String category = selectCategory(type);
            if (category == null) return; // 用戶取消

            // --- 分類驗證 ---
            if (!categoryManager.isValidCategory(type, category)) {
                 System.out.println("錯誤：選擇的分類 '" + category + "' 對於類型 '" + type + "' 無效！請重新輸入。");
                 return; // 不繼續執行
            }

            // 輸入其他資訊
            LocalDate date = inputDate();
            BigDecimal amount = inputAmount();
            String remark = inputRemark();

            // 建立 Expense 物件並新增 (addExpense 內部會自動儲存)
            manager.addExpense(new Expense(date, amount, category, remark, type));
            System.out.println("記錄新增成功！");

        } catch (IllegalArgumentException e) { // 捕捉可能的驗證錯誤
             System.out.println("輸入錯誤：" + e.getMessage());
        } catch (Exception e) {
            System.out.println("發生未預期錯誤：" + e.getMessage());
             e.printStackTrace();
        }
    }

    /**
     * 選擇交易類型（收入/支出）
     *
     * @return 選擇的交易類型，若取消則返回 null
     */
    private static TransactionType selectTransactionType() {
        while (true) {
            System.out.println("\n=== 選擇類型 ===");
            System.out.println("1. 收入");
            System.out.println("2. 支出");
            System.out.println("0. 返回主選單");
            System.out.print("請輸入選擇（數字/中文）: ");
            String input = scanner.nextLine().trim();
            if (input.equals("0")) return null;
            switch (input) {
                case "1", "收入" -> { return TransactionType.收入; }
                case "2", "支出" -> { return TransactionType.支出; }
                default -> System.out.println("無效選擇！");
            }
        }
    }

    /**
     * 分類選擇主方法
     *
     * @param type 交易類型（收入/支出）
     * @return 選擇的分類，若取消則返回 null
     */
    private static String selectCategory(TransactionType type) {
        Deque<String> breadcrumbs = new ArrayDeque<>();
        return selectCategoryRecursive(type, "main", breadcrumbs);
    }

    /**
     * 遞迴方式選擇分類
     * 支援多層分類結構，包括主分類和子分類
     * 記錄路徑以支援返回上一層
     *
     * @param type 交易類型（收入/支出）
     * @param currentLevel 當前分類層級
     * @param breadcrumbs 瀏覽路徑記錄
     * @return 選擇的分類，若取消則返回 null
     */
    private static String selectCategoryRecursive(TransactionType type, String currentLevel, Deque<String> breadcrumbs) {
        Map<Integer, String> categories = categoryManager.getCategories(type, currentLevel);
        while (true) {
            System.out.println("\n=== 選擇分類 ===");
            categories.keySet().stream()
                .sorted()
                .forEach(k -> System.out.printf("%d. %s%n", k, categories.get(k)));
            System.out.println("0. 返回上一步");
            System.out.print("請輸入選擇（數字/中文）: ");
            String input = scanner.nextLine().trim();
            if (input.equals("0")) {
                if (breadcrumbs.isEmpty()) return null;
                return selectCategoryRecursive(type, breadcrumbs.pop(), breadcrumbs);
            }

            try {
                // 處理數字輸入
                int choice = Integer.parseInt(input);
                if (categories.containsKey(choice)) {
                    String selected = categories.get(choice);
                    if (categoryManager.hasSubcategories(type, selected)) {
                        breadcrumbs.push(currentLevel);
                        return selectCategoryRecursive(type, selected, breadcrumbs);
                    }
                    return selected;
                }
            } catch (NumberFormatException e) {
                // 處理中文輸入
                for (Map.Entry<Integer, String> entry : categories.entrySet()) {
                    if (entry.getValue().equals(input)) {
                        String selected = entry.getValue();
                        if (categoryManager.hasSubcategories(type, selected)) {
                            breadcrumbs.push(currentLevel);
                            return selectCategoryRecursive(type, selected, breadcrumbs);
                        }
                        return selected;
                    }
                }
            }
            System.out.println("無效分類選擇！");
        }
    }

    /**
     * 日期輸入處理
     *
     * @return 用戶輸入的日期
     */
    private static LocalDate inputDate() {
        while (true) {
            System.out.print("輸入日期（YYYY-MM-DD）: ");
            String input = scanner.nextLine();
            try {
                return LocalDate.parse(input);
            } catch (Exception e) {
                System.out.println("日期格式錯誤！");
            }
        }
    }

    /**
     * 金額輸入處理
     * 支援直接輸入數字或數學算式
     *
     * @return 用戶輸入的金額
     */
    private static BigDecimal inputAmount() {
        while (true) {
            System.out.print("輸入金額或算式: ");
            String input = scanner.nextLine();
            try {
                return input.matches(".*[+\\-*/].*") ?
                    ExpressionEvaluator.evaluate(input) :
                    new BigDecimal(input);
            } catch (Exception e) {
                System.out.println("金額格式錯誤！");
            }
        }
    }

    /**
     * 備註輸入處理
     *
     * @return 用戶輸入的備註
     */
    private static String inputRemark() {
        System.out.print("輸入備註: ");
        return scanner.nextLine();
    }

    /**
     * 查看年份記錄
     * 顯示特定年份的所有記帳記錄並提供分頁、排序功能
     */
    private static void viewYearlyExpenses() {
        int year = selectYear();
        while (true) {
            System.out.printf("\n=== 當前查看：%d年 ===\n", year);
            List<Expense> yearlyExpenses = manager.getExpensesByYear(year);
            // 直接在這裡處理分頁，整合所有操作在同一層選單
            manager.sort(yearlyExpenses, "dateDesc"); // 預設排序
            PaginationHelper<Expense> ph = new PaginationHelper<>(yearlyExpenses, 5, "dateDesc");
            ph.resetPage();
            while (true) {
                // 顯示當前頁資料
                System.out.printf("\n=== 年份記錄（第%d頁/共%d頁） ===\n",
                    ph.getCurrentPage()+1, ph.getTotalPages());
                displayExpenses(ph.getCurrentPageData());
                System.out.println("\n=== 排序方式 ===");
                System.out.println("1. 金額由大到小");
                System.out.println("2. 金額由小到大");
                System.out.println("3. 日期由遠到近");
                System.out.println("4. 日期由近到遠");
                System.out.println("0. 直接使用預設排序");
                String sortType = "dateDesc"; // 默認排序方式
                System.out.print("請選擇排序方式：");
                String sortInput = scanner.nextLine().trim();
                switch (sortInput) {
                    case "1" -> sortType = "amountDesc";
                    case "2" -> sortType = "amountAsc";
                    case "3" -> sortType = "dateAsc";
                    case "4" -> sortType = "dateDesc";
                    case "0" -> {} // 使用預設值
                    default -> System.out.println("使用預設排序");
                }

                // 整合所有選項在同一層
                System.out.println("\n1. 下一頁");
                System.out.println("2. 下一年");
                System.out.println("3. 上一年");
                System.out.println("4. 切換年份");
                if (ph.getCurrentPage() > 0) {
                    System.out.println("5. 上一頁");
                    System.out.println("6. 回到第一頁");
                }
                System.out.println("0. 返回上一步");
                String input = scanner.nextLine().trim();
                switch (input) {
                    case "1" -> { // 下一頁
                        if (!ph.hasNextPage()) {
                            System.out.println("已無更多資料！");
                        } else {
                            ph.nextPage();
                        }
                    }
                    case "2" -> { // 下一年
                        year++;
                        break; // 跳出內層循環，重新獲取新年份資料
                    }
                    case "3" -> { // 上一年
                        year--;
                        break; // 跳出內層循環，重新獲取新年份資料
                    }
                    case "4" -> { // 切換年份
                        year = selectYear();
                        break; // 跳出內層循環，重新獲取新年份資料
                    }
                    case "5" -> { // 上一頁
                        if (!ph.hasPreviousPage()) {
                            System.out.println("已經是第一頁！");
                        } else {
                            ph.previousPage();
                        }
                    }
                    case "6" -> ph.resetPage(); // 回到第一頁
                    case "0" -> { return; } // 返回上一步
                    default -> System.out.println("無效輸入");
                }

                // 如果是年份變更，跳出內層循環
                if (input.equals("2") || input.equals("3") || input.equals("4")) {
                    break;
                }
            }
        }
    }

    /**
     * 根據備註關鍵字搜尋記帳記錄
     */
    private static void searchByRemark() {
        while (true) {
            System.out.print("請輸入備註關鍵字（輸入0返回）：");
            String keyword = scanner.nextLine().trim();
            if (keyword.equals("0")) return;
            List<Expense> results = manager.searchByRemark(keyword);
            if (results.isEmpty()) {
                System.out.println("查無相關記錄！");
            } else {
                handlePagination(results, "關鍵字搜尋結果");
            }
        }
    }

    /**
     * 查看記錄選單
     * 提供不同方式查看記帳記錄
     */
    private static void viewExpenses() {
        while (true) {
            System.out.println("\n=== 查看記錄 ===");
            System.out.println("1. 查看所有記錄");
            System.out.println("2. 查看單月份記錄");
            System.out.println("3. 查看年份記錄");
            System.out.println("4. 透過備註搜尋");
            System.out.println("0. 返回主選單");
            System.out.print("請選擇操作：");
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> viewAllExpenses();
                case "2" -> viewMonthlyExpenses();
                case "3" -> viewYearlyExpenses();
                case "4" -> searchByRemark();
                case "0" -> { return; }
                default -> System.out.println("無效輸入");
            }
        }
    }

    /**
     * 查看所有記錄
     */
    private static void viewAllExpenses() {
        List<Expense> allExpenses = new ArrayList<>(manager.getExpenses());
        handlePagination(allExpenses, "全部記錄");
    }

    /**
     * 查看月份記錄
     * 顯示特定月份的記帳記錄並提供分頁、排序功能
     */
    private static void viewMonthlyExpenses() {
        int year = selectYear();
        int month = selectMonth();
        while (true) {
            System.out.printf("\n=== 當前查看：%d年%02d月 ===\n", year, month);
            List<Expense> monthlyExpenses = manager.getExpensesByMonth(year, month);
            System.out.println("\n=== 排序方式 ===");
            System.out.println("1. 金額由大到小");
            System.out.println("2. 金額由小到大");
            System.out.println("3. 日期由遠到近");
            System.out.println("4. 日期由近到遠");
            System.out.println("0. 直接使用預設排序");
            String sortType = "dateDesc"; // 默認排序方式
            System.out.print("請選擇排序方式：");
            String sortInput = scanner.nextLine().trim();
            switch (sortInput) {
                case "1" -> sortType = "amountDesc";
                case "2" -> sortType = "amountAsc";
                case "3" -> sortType = "dateAsc";
                case "4" -> sortType = "dateDesc";
                case "0" -> {} // 使用預設值
                default -> System.out.println("使用預設排序");
            }

            manager.sort(monthlyExpenses, sortType);
            PaginationHelper<Expense> ph = new PaginationHelper<>(monthlyExpenses, 5, sortType);
            ph.resetPage();
            while (true) {
                // 顯示當前頁數據
                System.out.printf("\n=== 月份記錄（第%d頁/共%d頁） ===\n",
                    ph.getCurrentPage()+1, ph.getTotalPages());
                displayExpenses(ph.getCurrentPageData());
                // 整合所有選項在同一層
                System.out.println("\n1. 下一頁");
                System.out.println("2. 下個月");
                System.out.println("3. 上個月");
                System.out.println("4. 切換年份");
                if (ph.getCurrentPage() > 0) {
                    System.out.println("5. 上一頁");
                    System.out.println("6. 回到第一頁");
                }
                System.out.println("0. 返回上一步");
                String input = scanner.nextLine().trim();
                switch (input) {
                    case "1" -> { // 下一頁
                        if (!ph.hasNextPage()) {
                            System.out.println("已無更多資料！");
                        } else {
                            ph.nextPage();
                        }
                    }
                    case "2" -> { // 下個月
                        if (month == 12) { year++; month = 1; }
                        else { month++; }
                        break; // 跳出內層循環，重新獲取新月份資料
                    }
                    case "3" -> { // 上個月
                        if (month == 1) { year--; month = 12; }
                        else { month--; }
                        break; // 跳出內層循環，重新獲取新月份資料
                    }
                    case "4" -> { // 切換年份
                        year = selectYear();
                        month = selectMonth();
                        break; // 跳出內層循環，重新獲取新月份資料
                    }
                    case "5" -> { // 上一頁
                        if (!ph.hasPreviousPage()) {
                            System.out.println("已經是第一頁！");
                        } else {
                            ph.previousPage();
                        }
                    }
                    case "6" -> ph.resetPage(); // 回到第一頁
                    case "0" -> { return; } // 返回上一步
                    default -> System.out.println("無效輸入");
                }

                // 如果是月份或年份變更，跳出內層循環
                if (input.equals("2") || input.equals("3") || input.equals("4")) {
                    break;
                }
            }
        }
    }

    /**
     * 處理分頁顯示記帳記錄
     *
     * @param data 要分頁顯示的資料
     * @param context 上下文描述（顯示在頁面標題）
     * @return 分頁輔助器實例
     */
    private static PaginationHelper<Expense> handlePagination(List<Expense> data, String context) {
        System.out.println("\n=== 排序方式 ===");
        System.out.println("1. 金額由大到小");
        System.out.println("2. 金額由小到大");
        System.out.println("3. 日期由遠到近");
        System.out.println("4. 日期由近到遠");
        System.out.println("0. 返回上一步");
        String sortType = "dateDesc";
        boolean isValidChoice = false;
        while (!isValidChoice) {
            System.out.print("請選擇排序方式：");
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> { sortType = "amountDesc"; isValidChoice = true; }
                case "2" -> { sortType = "amountAsc"; isValidChoice = true; }
                case "3" -> { sortType = "dateAsc"; isValidChoice = true; }
                case "4" -> { sortType = "dateDesc"; isValidChoice = true; }
                case "0" -> { return null; }
                default -> System.out.println("無效選擇");
            }
        }

        manager.sort(data, sortType); // 排序資料
        PaginationHelper<Expense> ph = new PaginationHelper<>(data, 5, sortType);
        ph.resetPage();
        while (true) {
            System.out.printf("\n=== %s（第%d頁/共%d頁） ===\n", context, ph.getCurrentPage()+1, ph.getTotalPages());
            displayExpenses(ph.getCurrentPageData());
            System.out.println("\n1. 下一頁");
            if (ph.getCurrentPage() > 0) {
                System.out.println("2. 上一頁");
                System.out.println("3. 回到第一頁");
            }
            System.out.println("0. 返回上一步");
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> handlePageNavigation(ph, 1);
                case "2" -> handlePageNavigation(ph, -1);
                case "3" -> ph.resetPage();
                case "0" -> { return ph; }
                default -> System.out.println("無效輸入");
            }
        }
    }

    /**
     * 處理分頁導航
     *
     * @param ph 分頁輔助器實例
     * @param direction 導航方向（1表示下一頁，-1表示上一頁）
     */
    private static void handlePageNavigation(PaginationHelper<Expense> ph, int direction) {
        try {
            if (direction > 0) {
                ph.nextPage();
            } else {
                ph.previousPage();
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("已無更多資料！");
        }
    }

    /**
     * 顯示記帳記錄列表
     *
     * @param expenses 要顯示的記帳記錄列表
     */
    private static void displayExpenses(List<Expense> expenses) {
        if (expenses.isEmpty()) {
            System.out.println("沒有找到相關記錄");
            return;
        }
        System.out.println("日期 | 金額 | 類型 | 分類 | 備註");
        System.out.println("----------------------------------------------------------");
        expenses.forEach(exp ->
            System.out.printf("%s | %-8s | %-4s | %-14s | %s\n",
                exp.getDate(),
                exp.getAmount() + "元",
                exp.getType(),
                exp.getCategory(),
                exp.getRemark())
        );
    }

    
    
    
    
    /**
     * 離開系統
     * 儲存資料後結束程式，並匯出CSV檔案
     */
    private static void exitSystem() {
        System.out.println("正在儲存資料 (二進位)...");
        manager.saveExpenses(EXPENSES_FILE); //
        CategoryManager.saveCategories(CATEGORIES_FILE); //
        reminderManager.saveReminders(); // 明確呼叫儲存提醒，確保資料一致性
        System.out.println("二進位資料儲存完畢。");

        // --- 新增：匯出 CSV 檔案 ---
        System.out.println("正在匯出記帳記錄為 CSV 檔案...");
        // 產生一個帶有日期的CSV檔案名稱，例如："記帳紀錄_2024-05-26.csv"
        String csvFilename = "記帳紀錄_" + LocalDate.now().toString() + ".csv";
        manager.exportExpensesToCSV(csvFilename);
        // 訊息已在 exportExpensesToCSV 方法中打印，這裡可以不再重複或只打印簡短提示
        // System.out.println("CSV 檔案匯出完成。");

        System.out.println("感謝使用記帳系統！");
        scanner.close();
        System.exit(0);
    }
}
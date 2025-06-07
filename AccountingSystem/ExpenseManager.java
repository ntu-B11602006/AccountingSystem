import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets; // 保持這個匯入
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ExpenseManager {
    // 定義檔案名稱常數
    private static final String EXPENSES_FILE = "expenses.ser"; // <--- 檔案名稱

    // 所有記帳記錄的列表 (維持 non-static)
    private List<Expense> expenses = new ArrayList<>();
    // 分類管理器 (維持 final)
    private final CategoryManager categoryManager = new CategoryManager();
    /**
     * 建構子：初始化記帳管理器
     * (現在不載入資料，由 MainApp 控制)
     */
    public ExpenseManager() {
        // 初始化空的記帳列表
        expenses = new ArrayList<>();
    }

    /**
     * 新增消費記錄（強化分類驗證）
     *
     * @param expense 要新增的記錄
     * @throws IllegalArgumentException 若分類不符合類型要求
     */
    public void addExpense(Expense expense) {
        // validateCategory(expense.getType(), expense.getCategory()); // 分類驗證移到 MainApp 輸入階段更佳
        expenses.add(expense);
        sortByDate(); // 按日期排序（可選）
        saveExpenses(EXPENSES_FILE); // <--- 新增後儲存
    }

    // 驗證分類的方法可以保留，但建議在 MainApp 輸入時就做
    private void validateCategory(TransactionType type, String category) {
        // 注意: CategoryManager 的 isValidCategory 需要是 static 或透過實例調用
        // 如果 CategoryManager 的方法改為 static，這裡也要改
        // if (!categoryManager.isValidCategory(type, category)) { // 如果 CategoryManager 方法是 static
        // 或保持現在這樣通過 categoryManager 實例調用
        // if (!this.categoryManager.isValidCategory(type, category)) { // 假設 isValidCategory 非 static
        //  throw new IllegalArgumentException("無效分類: " + category + " 對於類型 " + type);
        // }
        // --- 目前 CategoryManager 中的 isValidCategory 還是 non-static，所以維持現狀 ---
         if (!categoryManager.isValidCategory(type, category)) {
            throw new IllegalArgumentException("無效分類: " + category + " 對於類型 " + type);
         }
    }


    /**
     * 編輯記帳記錄金額
     * (注意：編輯後也應該儲存)
     *
     * @param index 要編輯的記錄索引
     * @param newAmount 新金額
     */
    public void editExpense(int index, BigDecimal newAmount) {
        if (index >= 0 && index < expenses.size()) {
            expenses.get(index).setAmount(newAmount);
            saveExpenses(EXPENSES_FILE); // <--- 編輯後儲存
        } else {
             System.out.println("錯誤：無效的記錄索引。");
        }
    }

    // --- getExpenses, searchByKeyword, sortByDate, getExpensesByMonth, getExpensesByYear, sort, searchByRemark 維持不變 ---
    public List<Expense> getExpenses() {
        return new ArrayList<>(expenses);
    }

    /**
     * 根據關鍵字搜尋記帳記錄
     *
     * @param keyword 要搜尋的關鍵字
     * @return 符合的記帳記錄列表
     */
    public List<Expense> searchByKeyword(String keyword) {
        return expenses.stream()
            .filter(e -> e.getRemark().contains(keyword))
            .collect(Collectors.toList());
    }

    /**
     * 按日期排序所有記帳記錄
     */
    public void sortByDate() {
        expenses.sort(Comparator.comparing(Expense::getDate));
    }

    /**
     * 獲取特定月份的記帳記錄
     *
     * @param year 年份
     * @param month 月份
     * @return 該月份的記帳記錄列表
     */
    public List<Expense> getExpensesByMonth(int year, int month) {
        return expenses.stream()
            .filter(e -> e.getDate().getYear() == year && e.getDate().getMonthValue() == month)
            .collect(Collectors.toList());
    }

    /**
     * 獲取特定年份的記帳記錄
     *
     * @param year 年份
     * @return 該年份的記帳記錄列表
     */
    public List<Expense> getExpensesByYear(int year) {
        return expenses.stream()
            .filter(e -> e.getDate().getYear() == year)
            .collect(Collectors.toList());
    }

    /**
     * 根據排序類型對記帳記錄列表進行排序
     *
     * @param list 要排序的記帳記錄列表
     * @param sortType 排序類型（amountDesc、amountAsc、dateAsc、dateDesc）
     */
    public void sort(List<Expense> list, String sortType) {
        switch (sortType) {
            case "amountDesc" -> list.sort(Comparator.comparing(Expense::getAmount).reversed());
            case "amountAsc" -> list.sort(Comparator.comparing(Expense::getAmount));
            case "dateAsc" -> list.sort(Comparator.comparing(Expense::getDate));
            default -> list.sort(Comparator.comparing(Expense::getDate).reversed());
        }
    }

    /**
     * 根據備註關鍵字搜尋記帳記錄
     * 完全匹配的結果會排在前面
     *
     * @param keyword 要搜尋的備註關鍵字
     * @return 排序後的搜尋結果列表
     */
    public List<Expense> searchByRemark(String keyword) {
        return expenses.stream()
            .filter(e -> e.getRemark().contains(keyword))
            .sorted((a,b) -> {
                boolean exactMatchA = a.getRemark().equalsIgnoreCase(keyword);
                boolean exactMatchB = b.getRemark().equalsIgnoreCase(keyword);
                if (exactMatchA == exactMatchB) return 0;
                return exactMatchA ? -1 : 1;
            })
            .collect(Collectors.toList());
    }


    /**
     * 將目前的 expenses 列表儲存到檔案
     *
     * @param filename 檔案路徑
     */
    public void saveExpenses(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(new ArrayList<>(this.expenses)); // 儲存 expenses 列表的副本
            // System.out.println("記帳記錄已儲存到 " + filename); // 可選：儲存成功提示
        } catch (IOException e) {
            System.err.println("儲存記帳記錄時發生錯誤: " + e.getMessage());
             e.printStackTrace(); // 顯示詳細錯誤
        }
    }

    /**
     * 從檔案載入記帳記錄到 expenses 列表
     * 如果檔案不存在或載入失敗，則保持列表為空
     *
     * @param filename 檔案路徑
     */
    @SuppressWarnings("unchecked") // 抑制讀取 Object 時的類型轉換警告
    public void loadExpenses(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("找不到記帳記錄存檔，將從空記錄開始...");
            this.expenses = new ArrayList<>(); // 確保是空列表
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Object loadedObject = ois.readObject();
             if (loadedObject instanceof List<?>) {
                 // 清空目前列表，確保不重複載入
                this.expenses.clear();
                // 載入資料，需要類型轉換
                this.expenses.addAll((List<Expense>) loadedObject);
                System.out.println("記帳記錄已從 " + filename + " 載入。");
                sortByDate(); // 載入後排序一次
            } else {
                 System.err.println("記帳記錄檔案格式錯誤，將從空記錄開始...");
                 this.expenses = new ArrayList<>();
            }

        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.err.println("讀取記帳記錄時發生錯誤: " + e.getMessage() + "，將從空記錄開始...");
             e.printStackTrace(); // 顯示詳細錯誤
            this.expenses = new ArrayList<>(); // 出錯時確保是空列表
        }
    }
    /**
     * 根據索引刪除記帳記錄
     *
     * @param index 要刪除的記錄索引
     */
    public void deleteExpense(int index) {
        if (index >= 0 && index < expenses.size()) {
            expenses.remove(index);
            saveExpenses(EXPENSES_FILE); // 刪除後儲存更新
            System.out.println("記錄已刪除。");
        } else {
            System.out.println("錯誤：無效的記錄索引。");
        }
    }
    
    
    
    
    // ========== 新增：匯出記帳記錄到 CSV 檔案 ==========
    /**
     * 將所有記帳記錄匯出到指定的 CSV 檔案。
     *
     * @param filename 要匯出的 CSV 檔案名稱
     */
    public void exportExpensesToCSV(String filename) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        String[] headers = {"日期", "金額", "類型", "分類", "備註"};
        File file = new File(filename);

        // 使用 try-with-resources 確保資源正確關閉
        try (OutputStream fos = new FileOutputStream(file); // <--- 改用 FileOutputStream
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8); // <--- 使用 UTF_8
             BufferedWriter writer = new BufferedWriter(osw)) {

            // 手動寫入 BOM (EF BB BF)
            fos.write(0xEF); // <--- 手動寫入BOM的第一個位元組
            fos.write(0xBB); // <--- 手動寫入BOM的第二個位元組
            fos.write(0xBF); // <--- 手動寫入BOM的第三個位元組
            // 注意：寫入BOM後，後續都應該透過 BufferedWriter/OutputStreamWriter 來寫入字元資料

            // 寫入表頭
            writer.write(String.join(",", headers));
            writer.newLine();

            // 逐筆寫入資料
            for (Expense expense : this.expenses) {
                List<String> rowData = new ArrayList<>();
                rowData.add(escapeCsvField(expense.getDate().format(dateFormatter)));
                rowData.add(escapeCsvField(expense.getAmount().toPlainString()));
                rowData.add(escapeCsvField(expense.getType().toString()));
                rowData.add(escapeCsvField(expense.getCategory()));
                rowData.add(escapeCsvField(expense.getRemark()));

                writer.write(String.join(",", rowData));
                writer.newLine();
            }
            System.out.println("CSV 檔案已成功匯出至: " + file.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("匯出 CSV 檔案時發生錯誤: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 輔助方法：處理 CSV 欄位中的特殊字元。
     */
    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        String escapedField = field.replace("\"", "\"\"");
        if (field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains("\r")) {
            escapedField = "\"" + escapedField + "\"";
        }
        return escapedField;
    }

    // ... (其他現有方法)
}




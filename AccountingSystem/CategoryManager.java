// CategoryManager.java - 分類管理類（加入資料持久化功能）

import java.io.*; // <--- 匯入 IO 相關類別
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collection;
import java.util.List; // <--- 匯入 List

/**
 * 分類管理類
 * ---------
 * 負責管理記帳系統中的收入與支出分類
 * 支援分類的新增、刪除、重新排序等功能
 * 支出分類採用兩層結構（主分類和子分類）
 */
public class CategoryManager {
    // 定義檔案名稱常數
    private static final String CATEGORIES_FILE = "categories.ser"; // <--- 檔案名稱

    // --- 分類結構維持 static ---
    private static final LinkedHashMap<Integer, String> INCOME_CATEGORIES = new LinkedHashMap<>();
    private static final LinkedHashMap<Integer, String> EXPENSE_MAIN_CATEGORIES = new LinkedHashMap<>();
    private static final LinkedHashMap<String, LinkedHashMap<Integer, String>> EXPENSE_SUB_CATEGORIES = new LinkedHashMap<>();
    private Map<Integer, String> incomeCategories = new LinkedHashMap<>();



    /**
     * 建構子：初始化分類管理器
     * (現在不在這裡初始化預設分類，改由 loadCategories 控制)
     */
    public CategoryManager() {
        // 這裡不再呼叫 initDefaultCategories()
    }

    /**
     * 初始化預設分類
     * (設為 static 方便 loadCategories 呼叫)
     */
    private static void initDefaultCategories() { // <--- 改為 static
        // 清空現有分類，確保是從預設狀態開始
        INCOME_CATEGORIES.clear();
        EXPENSE_MAIN_CATEGORIES.clear();
        EXPENSE_SUB_CATEGORIES.clear();

        // 初始化收入分類
        INCOME_CATEGORIES.put(1, "薪資");
        INCOME_CATEGORIES.put(2, "獎金");
        INCOME_CATEGORIES.put(3, "投資收入");
        INCOME_CATEGORIES.put(4, "其他收入");

        // 初始化支出主分類
        EXPENSE_MAIN_CATEGORIES.put(1, "生活日常");
        EXPENSE_MAIN_CATEGORIES.put(2, "居住相關");
        EXPENSE_MAIN_CATEGORIES.put(3, "醫療保險");
        EXPENSE_MAIN_CATEGORIES.put(4, "娛樂休閒");
        EXPENSE_MAIN_CATEGORIES.put(5, "人際社交");
        EXPENSE_MAIN_CATEGORIES.put(6, "投資");
        EXPENSE_MAIN_CATEGORIES.put(7, "其他");

        // 初始化支出子分類
        initDefaultSubCategories(); // <--- 呼叫 static 的方法
        System.out.println("已初始化預設分類。"); // <--- 提示訊息
    }

    /**
     * 初始化預設的支出子分類
     * (設為 static 方便 loadCategories 呼叫)
     */
    private static void initDefaultSubCategories() { // <--- 改為 static
        initSubcategory("生活日常", Map.of(1, "飲食", 2, "交通", 3, "衣服&配件", 4, "通訊(手機&網路)", 5, "日用品"));
        initSubcategory("居住相關", Map.of(1, "房租", 2, "水電瓦斯", 3, "居家用品"));
        initSubcategory("醫療保險", Map.of(1, "醫療", 2, "保險"));
        initSubcategory("娛樂休閒", Map.of(1, "旅遊", 2, "電影",3, "訂閱服務"));
        initSubcategory("人際社交", Map.of(1, "禮物", 2, "節慶開銷"));
        initSubcategory("投資", Map.of(1, "股票買進", 2, "基金投資",3, "虛擬貨幣"));
        initSubcategory("其他", Map.of(1, "其他")); // 預設子分類
    }

    /**
     * 初始化特定主分類的子分類
     * (維持 static)
     *
     * @param mainCategory 主分類名稱
     * @param items 子分類 Map（ID 對應名稱）
     */
    private static void initSubcategory(String mainCategory, Map<Integer, String> items) {
        EXPENSE_SUB_CATEGORIES.put(mainCategory, new LinkedHashMap<>(items));
    }

    /**
     * 新增收入分類
     *
     * @param newCategory 新的收入分類名稱
     */
    public void addIncomeCategory(String newCategory) {
        int newKey = INCOME_CATEGORIES.isEmpty() ? 1 : Collections.max(INCOME_CATEGORIES.keySet()) + 1;
        INCOME_CATEGORIES.put(newKey, newCategory);
        saveCategories(CATEGORIES_FILE); // <--- 新增後儲存
    }

    /**
     * 新增支出主分類
     *
     * @param newCategory 新的支出主分類名稱
     */
    public void addExpenseMainCategory(String newCategory) {
        int newKey = EXPENSE_MAIN_CATEGORIES.isEmpty() ? 1 : Collections.max(EXPENSE_MAIN_CATEGORIES.keySet()) + 1;
        EXPENSE_MAIN_CATEGORIES.put(newKey, newCategory);
        // 同時建立一個預設的同名子分類
        LinkedHashMap<Integer, String> defaultSub = new LinkedHashMap<>();
        defaultSub.put(1, newCategory); // 使用 newCategory 作為預設子分類名稱
        EXPENSE_SUB_CATEGORIES.put(newCategory, defaultSub);
        saveCategories(CATEGORIES_FILE); // <--- 新增後儲存
    }


    /**
     * 新增支出子分類
     *
     * @param mainCategory 所屬主分類名稱
     * @param newSubCategory 新的子分類名稱
     */
    public void addExpenseSubCategory(String mainCategory, String newSubCategory) {
        LinkedHashMap<Integer, String> subMap = EXPENSE_SUB_CATEGORIES.get(mainCategory);
        if (subMap != null) { // 確保主分類存在
            int newKey = subMap.isEmpty() ? 1 : Collections.max(subMap.keySet()) + 1;
            subMap.put(newKey, newSubCategory);
            saveCategories(CATEGORIES_FILE); // <--- 新增後儲存
        } else {
            System.out.println("錯誤：找不到主分類 " + mainCategory);
        }
    }

    /**
     * 刪除收入分類
     *
     * @param key 要刪除的收入分類 ID
     */
    public void removeIncomeCategory(int key) {
        INCOME_CATEGORIES.remove(key);
        reindexCategories(INCOME_CATEGORIES);
        saveCategories(CATEGORIES_FILE); // <--- 刪除後儲存
    }

    /**
     * 刪除支出子分類
     *
     * @param mainCategory 所屬主分類名稱
     * @param subKey 要刪除的子分類 ID
     */
    public void removeExpenseSubCategory(String mainCategory, int subKey) {
        LinkedHashMap<Integer, String> subMap = EXPENSE_SUB_CATEGORIES.get(mainCategory);
        if (subMap != null) { // 確保主分類存在
            subMap.remove(subKey);
            reindexCategories(subMap);
            // 如果主分類下沒有子分類了，可以考慮是否要移除主分類（這裡暫不處理）
            saveCategories(CATEGORIES_FILE); // <--- 刪除後儲存
        } else {
             System.out.println("錯誤：找不到主分類 " + mainCategory);
        }
    }

    /**
     * 重新排序分類 ID，保持 ID 連續 (改為 static)
     *
     * @param map 要重新排序的分類 Map
     */
    private static void reindexCategories(LinkedHashMap<Integer, String> map) { // <--- 改為 static
        LinkedHashMap<Integer, String> temp = new LinkedHashMap<>();
        int newKey = 1;
        for (String value : map.values()) {
            temp.put(newKey++, value);
        }
        map.clear();
        map.putAll(temp);
    }

    // --- 以下 Getters 維持不變，但確保返回副本 ---

    public Map<Integer, String> getIncomeCategories() {
        return new LinkedHashMap<>(INCOME_CATEGORIES); // 返回副本
    }

    public Map<Integer, String> getExpenseMainCategories() {
        return new LinkedHashMap<>(EXPENSE_MAIN_CATEGORIES); // 返回副本
    }

    public Map<Integer, String> getExpenseSubCategories(String mainCategory) {
        // 返回副本或空 Map
        LinkedHashMap<Integer, String> subMap = EXPENSE_SUB_CATEGORIES.get(mainCategory);
        return (subMap != null) ? new LinkedHashMap<>(subMap) : new LinkedHashMap<>();
    }

    public Collection<LinkedHashMap<Integer, String>> getAllExpenseSubCategories() {
        // 返回深層副本比較複雜，這裡暫時返回淺層副本的集合
        // 如果需要修改裡面的 Map，需要注意對原 static 數據的影響
        // 為了安全，外部最好只讀取
         return new LinkedHashMap<>(EXPENSE_SUB_CATEGORIES).values();
    }

    /**
     * 檢查指定類型和分類名稱是否有效
     *
     * @param type 交易類型（收入/支出）
     * @param category 分類名稱
     * @return 如果有效返回 true，否則返回 false
     */
    public boolean isValidCategory(TransactionType type, String category) {
        switch (type) {
            case 收入:
                return INCOME_CATEGORIES.containsValue(category);
            case 支出:
                // 先檢查主分類
                if (EXPENSE_MAIN_CATEGORIES.containsValue(category)) return true;
                // 再檢查所有子分類
                return EXPENSE_SUB_CATEGORIES.values().stream()
                    .anyMatch(subMap -> subMap.containsValue(category));
            default:
                return false;
        }
    }

    /**
     * 根據交易類型和當前層級獲取相應的分類
     *
     * @param type 交易類型（收入/支出）
     * @param currentLevel 當前分類層級（"main" 或主分類名稱）
     * @return 分類 Map（ID 對應名稱）
     */
    public Map<Integer, String> getCategories(TransactionType type, String currentLevel) {
        return switch (type) {
            case 收入 -> getIncomeCategories();
            case 支出 -> {
                if ("main".equals(currentLevel)) {
                    yield getExpenseMainCategories();
                } else {
                    yield getExpenseSubCategories(currentLevel);
                }
            }
        };
    }

    /**
     * 檢查分類是否為支出主分類
     *
     * @param category 分類名稱
     * @return 如果是主分類返回 true，否則返回 false
     */
    public boolean isExpenseMainCategory(String category) {
        return EXPENSE_MAIN_CATEGORIES.containsValue(category);
    }

    /**
     * 檢查指定類型和分類是否有子分類
     *
     * @param type 交易類型（收入/支出）
     * @param category 分類名稱
     * @return 如果有子分類返回true，否則返回false
     */
    public boolean hasSubcategories(TransactionType type, String category) {
        if (type == TransactionType.收入) {
            // 收入分類沒有子分類
            return false;
        } else {
            // 支出分類：主分類有子分類，子分類沒有子分類
            return isExpenseMainCategory(category);
        }
    }


    

    /**
     * 將目前的靜態分類資料儲存到檔案
     *
     * @param filename 檔案路徑
     */
    public static void saveCategories(String filename) {
        // 將三個 Map 打包到一個 List 中以便序列化
        List<Object> categoryData = List.of(
                new LinkedHashMap<>(INCOME_CATEGORIES),
                new LinkedHashMap<>(EXPENSE_MAIN_CATEGORIES),
                new LinkedHashMap<>(EXPENSE_SUB_CATEGORIES)
        );

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(categoryData);
            // System.out.println("分類資料已儲存到 " + filename); // 可選：儲存成功提示
        } catch (IOException e) {
            System.err.println("儲存分類資料時發生錯誤: " + e.getMessage());
            e.printStackTrace(); // 顯示詳細錯誤
        }
    }

    /**
     * 從檔案載入分類資料到靜態變數中
     * 如果檔案不存在或載入失敗，則初始化預設分類
     *
     * @param filename 檔案路徑
     */
    @SuppressWarnings("unchecked") // 抑制讀取 Object 時的類型轉換警告
    public static void loadCategories(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("找不到分類存檔，將初始化預設分類...");
            initDefaultCategories();
            saveCategories(filename); // 順便保存一次預設分類
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Object loadedObject = ois.readObject();
            if (loadedObject instanceof List<?> loadedList && loadedList.size() == 3) {
                // 清空目前的靜態 Map
                INCOME_CATEGORIES.clear();
                EXPENSE_MAIN_CATEGORIES.clear();
                EXPENSE_SUB_CATEGORIES.clear();

                // 載入資料，需要類型轉換
                INCOME_CATEGORIES.putAll((LinkedHashMap<Integer, String>) loadedList.get(0));
                EXPENSE_MAIN_CATEGORIES.putAll((LinkedHashMap<Integer, String>) loadedList.get(1));
                EXPENSE_SUB_CATEGORIES.putAll((LinkedHashMap<String, LinkedHashMap<Integer, String>>) loadedList.get(2));

                System.out.println("分類資料已從 " + filename + " 載入。");
            } else {
                 System.err.println("分類檔案格式錯誤，將初始化預設分類...");
                 initDefaultCategories();
                 saveCategories(filename); // 覆蓋錯誤的檔案
            }
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.err.println("讀取分類資料時發生錯誤: " + e.getMessage() + "，將初始化預設分類...");
            e.printStackTrace(); // 顯示詳細錯誤
            initDefaultCategories();
            saveCategories(filename); // 覆蓋可能有問題的檔案
        }
    }
    public void editIncomeCategory(int index, String newName) {
    	INCOME_CATEGORIES.put(index, newName);
    	saveCategories(CATEGORIES_FILE);
    }

    public void editExpenseSubCategory(String mainCategory, int index, String newName) {
        Map<Integer, String> subCategories = EXPENSE_SUB_CATEGORIES.get(mainCategory);
        if (subCategories != null) {
            subCategories.put(index, newName);
            saveCategories(CATEGORIES_FILE); // 加上儲存功能
        } else {
            System.out.println("錯誤：找不到主分類 " + mainCategory);
        }
    }


}
// CategoryEditor.java - 分類編輯器類

import java.util.Map;
import java.util.Scanner;

/**
 * 分類編輯器類
 * ----------
 * 提供互動式界面讓用戶管理記帳分類
 * 支援新增和刪除收入與支出分類
 */
public class CategoryEditor {
    // 分類管理器引用
    private final CategoryManager categoryManager;
    // 輸入掃描器引用
    private final Scanner scanner;


    /**
     * 建構子：初始化分類編輯器
     *
     * @param categoryManager 分類管理器實例
     * @param scanner 輸入掃描器實例
     */
    public CategoryEditor(CategoryManager categoryManager, Scanner scanner) {
        this.categoryManager = categoryManager;
        this.scanner = scanner;
    }

    /**
     * 顯示分類管理主選單
     * 提供新增、刪除分類等功能選項
     */
    public void showCategoryMenu() {
        while (true) {
            System.out.println("\n=== 分類管理 ===");
            System.out.println("1. 新增分類");
            System.out.println("2. 刪除分類");
            System.out.println("3. 修改分類");
            System.out.println("0. 返回主選單");
            System.out.print("請選擇操作：");
            switch (scanner.nextLine().trim()) {
                case "1" -> addCategoryProcess();
                case "2" -> deleteCategoryProcess();
                case "3" -> editCategoryProcess();
                case "0" -> { return; }
                default -> System.out.println("無效輸入");
            }
        }
    }
    private void editCategoryProcess() {
        System.out.println("\n=== 編輯分類 ===");
        System.out.println("1. 修改收入分類");
        System.out.println("2. 修改支出子分類");
        System.out.print("請選擇類型：");
        String input = scanner.nextLine().trim();
        switch (input) {
            case "1" -> editIncomeCategory();
            case "2" -> editExpenseSubCategory();
            default -> System.out.println("無效選擇");
        }
    }

    /**
     * 新增分類選單處理
     * 引導用戶選擇要新增收入分類還是支出分類
     */
    private void addCategoryProcess() {
        System.out.println("\n=== 新增分類 ===");
        System.out.println("1. 收入分類");
        System.out.println("2. 支出分類");
        System.out.print("請選擇類型：");
        String input = scanner.nextLine().trim();
        switch (input) {
            case "1" -> addIncomeCategory();
            case "2" -> addExpenseCategory();
            default -> System.out.println("無效選擇");
        }
    }

    /**
     * 新增收入分類處理
     * 引導用戶輸入新收入分類名稱
     */
    private void addIncomeCategory() {
        System.out.print("輸入新收入分類名稱：");
        String newCategory = scanner.nextLine().trim();
        if (!newCategory.isEmpty()) {
            categoryManager.addIncomeCategory(newCategory);
            System.out.println("收入分類新增成功！");
        } else {
            System.out.println("分類名稱不能為空！");
        }
    }

    /**
     * 新增支出分類處理
     * 引導用戶選擇主分類並輸入新子分類名稱
     * 或新增主分類（當選擇"其他"時）
     */
    private void addExpenseCategory() {
        Map<Integer, String> mainCategories = categoryManager.getExpenseMainCategories();
        System.out.println("\n=== 選擇主分類 ===");
        mainCategories.forEach((k, v) -> System.out.printf("%d. %s%n", k, v));
        System.out.print("請選擇主分類編號：");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            String mainCategory = mainCategories.get(choice);
            if (mainCategory != null) {
                if (choice == 7) { // "其他"主分類特殊處理
                    System.out.print("輸入新主分類名稱：");
                    String newMain = scanner.nextLine().trim();
                    if (!newMain.isEmpty()) {
                        categoryManager.addExpenseMainCategory(newMain);
                        System.out.println("新增主分類成功！");
                        // 詢問是否要為新主分類添加子分類
                        System.out.print("是否要新增子分類？(y/n)：");
                        String addSubInput = scanner.nextLine().trim().toLowerCase();
                        if (addSubInput.equals("y") || addSubInput.equals("yes")) {
                            System.out.print("輸入新子分類名稱：");
                            String newSub = scanner.nextLine().trim();
                            if (!newSub.isEmpty()) {
                                categoryManager.addExpenseSubCategory(newMain, newSub);
                                System.out.println("子分類新增成功！");
                            } else {
                                System.out.println("分類名稱不能為空！");
                            }
                        }
                    } else {
                        System.out.println("分類名稱不能為空！");
                    }
                } else {
                    System.out.print("輸入新子分類名稱：");
                    String newSub = scanner.nextLine().trim();
                    if (!newSub.isEmpty()) {
                        categoryManager.addExpenseSubCategory(mainCategory, newSub);
                        System.out.println("子分類新增成功！");
                    } else {
                        System.out.println("分類名稱不能為空！");
                    }
                }
            } else {
                System.out.println("無效選擇");
            }
        } catch (NumberFormatException e) {
            System.out.println("請輸入數字編號");
        }
    }

    /**
     * 刪除分類選單處理
     * 引導用戶選擇要刪除收入分類還是支出分類
     */
    private void deleteCategoryProcess() {
        System.out.println("\n=== 刪除分類 ===");
        System.out.println("1. 刪除收入分類");
        System.out.println("2. 刪除支出子分類");
        System.out.print("請選擇類型：");
        String input = scanner.nextLine().trim();
        switch (input) {
            case "1" -> deleteIncomeCategory();
            case "2" -> deleteExpenseSubCategory();
            default -> System.out.println("無效選擇");
        }
    }

    /**
     * 刪除收入分類處理
     * 顯示所有收入分類並引導用戶選擇要刪除的分類
     */
    private void deleteIncomeCategory() {
        Map<Integer, String> categories = categoryManager.getIncomeCategories();
        System.out.println("\n=== 收入分類列表 ===");
        categories.forEach((k, v) -> System.out.printf("%d. %s%n", k, v));
        System.out.print("輸入要刪除的分類編號：");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (categories.containsKey(choice)) {
                if (categories.size() <= 1) {
                    System.out.println("至少需保留一個收入分類！");
                } else {
                    categoryManager.removeIncomeCategory(choice);
                    System.out.println("分類刪除成功！");
                }
            } else {
                System.out.println("無效編號");
            }
        } catch (NumberFormatException e) {
            System.out.println("請輸入數字編號");
        }
    }

    /**
     * 刪除支出子分類處理
     * 引導用戶選擇主分類和要刪除的子分類
     */
    private void deleteExpenseSubCategory() {
        Map<Integer, String> mainCategories = categoryManager.getExpenseMainCategories();
        System.out.println("\n=== 選擇主分類 ===");
        mainCategories.forEach((k, v) -> System.out.printf("%d. %s%n", k, v));
        System.out.print("請選擇主分類編號：");
        try {
            int mainChoice = Integer.parseInt(scanner.nextLine().trim());
            String mainCategory = mainCategories.get(mainChoice);
            if (mainCategory != null) {
                Map<Integer, String> subCategories = categoryManager.getExpenseSubCategories(mainCategory);
                System.out.println("\n=== 子分類列表 ===");
                subCategories.forEach((k, v) -> System.out.printf("%d. %s%n", k, v));
                System.out.print("輸入要刪除的子分類編號：");
                int subChoice = Integer.parseInt(scanner.nextLine().trim());
                if (subCategories.containsKey(subChoice)) {
                    if (subCategories.size() <= 1) {
                        System.out.println("至少需保留一個子分類！");
                    } else {
                        categoryManager.removeExpenseSubCategory(mainCategory, subChoice);
                        System.out.println("子分類刪除成功！");
                    }
                } else {
                    System.out.println("無效編號");
                }
            } else {
                System.out.println("無效選擇");
            }
        } catch (NumberFormatException e) {
            System.out.println("請輸入數字編號");
        }
    }
    private void editIncomeCategory() {
        Map<Integer, String> categories = categoryManager.getIncomeCategories();
        System.out.println("\n=== 收入分類列表 ===");
        categories.forEach((k, v) -> System.out.printf("%d. %s%n", k, v));
        System.out.print("輸入要修改的分類編號：");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (categories.containsKey(choice)) {
                System.out.print("輸入新的分類名稱：");
                String newName = scanner.nextLine().trim();
                if (newName.isEmpty()) {
                    System.out.println("分類名稱不能為空！");
                    return;
                }
                categoryManager.editIncomeCategory(choice, newName);
                System.out.println("收入分類修改成功！");
            } else {
                System.out.println("無效編號");
            }
        } catch (NumberFormatException e) {
            System.out.println("請輸入數字編號");
        }
    }
    private void editExpenseSubCategory() {
        Map<Integer, String> mainCategories = categoryManager.getExpenseMainCategories();
        System.out.println("\n=== 選擇主分類 ===");
        mainCategories.forEach((k, v) -> System.out.printf("%d. %s%n", k, v));
        System.out.print("請選擇主分類編號：");
        try {
            int mainChoice = Integer.parseInt(scanner.nextLine().trim());
            String mainCategory = mainCategories.get(mainChoice);
            if (mainCategory != null) {
                Map<Integer, String> subCategories = categoryManager.getExpenseSubCategories(mainCategory);
                System.out.println("\n=== 子分類列表 ===");
                subCategories.forEach((k, v) -> System.out.printf("%d. %s%n", k, v));
                System.out.print("輸入要修改的子分類編號：");
                int subChoice = Integer.parseInt(scanner.nextLine().trim());
                if (subCategories.containsKey(subChoice)) {
                    System.out.print("輸入新的子分類名稱：");
                    String newName = scanner.nextLine().trim();
                    if (newName.isEmpty()) {
                        System.out.println("分類名稱不能為空！");
                        return;
                    }
                    categoryManager.editExpenseSubCategory(mainCategory, subChoice, newName);
                    System.out.println("子分類修改成功！");
                } else {
                    System.out.println("無效編號");
                }
            } else {
                System.out.println("無效選擇");
            }
        } catch (NumberFormatException e) {
            System.out.println("請輸入數字編號");
        }
    }

}

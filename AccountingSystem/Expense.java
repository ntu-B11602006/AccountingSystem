// Expense.java - 記帳記錄類別

import java.io.Serializable; // <--- 匯入 Serializable
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Expense 類別 - 記帳記錄
 * ---------------
 * 此類別代表一筆記帳系統中的交易紀錄，包含日期、金額、分類、備註與收入/支出類型
 * 使用 BigDecimal 確保金額計算精確度，避免浮點數計算誤差
 * 使用 LocalDate 處理日期資訊，提供更好的日期操作功能
 */
public class Expense implements Serializable {
    // 加入 serialVersionUID
    private static final long serialVersionUID = 1L; // <--- 加入版本 ID

    // 記錄日期
    private LocalDate date;
    // 交易金額，使用 BigDecimal 確保精確度
    private BigDecimal amount;
    // 交易分類（如飲食、交通等）
    private String category;
    // 備註說明
    private String remark;
    // 交易類型（收入/支出）
    private TransactionType type;

    /**
     * 建構子：建立一筆新的記帳紀錄
     *
     * @param date 記錄日期（LocalDate）
     * @param amount 金額（BigDecimal）
     * @param category 分類（字串）
     * @param remark 備註（字串）
     * @param type 交易類型（TransactionType）
     */
    public Expense(LocalDate date, BigDecimal amount, String category, String remark, TransactionType type) {
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.remark = remark;
        this.type = type;
    }

    /**
     * 取得記錄日期
     *
     * @return 記錄的日期（LocalDate）
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * 設定記錄日期
     *
     * @param date 要設定的日期（LocalDate）
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * 取得交易金額
     *
     * @return 交易金額（BigDecimal）
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * 設定交易金額
     *
     * @param amount 要設定的金額（BigDecimal）
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * 取得交易分類
     *
     * @return 交易分類名稱（String）
     */
    public String getCategory() {
        return category;
    }

    /**
     * 設定交易分類
     *
     * @param category 要設定的分類（String）
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * 取得備註
     *
     * @return 交易備註（String）
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 設定備註
     *
     * @param remark 要設定的備註（String）
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 取得交易類型（收入/支出）
     *
     * @return 交易類型（TransactionType）
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * 設定交易類型
     *
     * @param type 要設定的交易類型（TransactionType）
     */
    public void setType(TransactionType type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return String.format(
            "%-10s | %8s | %-4s | %-8s | %s",
            date.toString(),                          // 日期 (yyyy-MM-dd)
            amount.toString(), // 金額
            type == null ? "未知" : type.toString(),   // 收入/支出
            category == null ? "未分類" : category,    // 分類
            remark == null ? "" : remark              // 備註
        );
    }

}

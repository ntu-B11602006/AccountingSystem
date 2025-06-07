// PaginationHelper.java - 分頁輔助類

import java.util.List;

/**
 * 分頁輔助類
 * --------
 * 處理資料分頁顯示的邏輯
 * 提供頁面導航、排序類型管理等功能
 *
 * @param <T> 分頁數據的類型
 */
public class PaginationHelper<T> {
    // 原始資料列表
    private final List<T> data;
    // 每頁顯示項目數
    private final int pageSize;
    // 當前頁碼（從0開始）
    private int currentPage;
    // 排序類型
    private String sortType;
    // 查看的年份（用於月份和年份視圖）
    private int currentYear;
    // 查看的月份（用於月份視圖）
    private int currentMonth;

    /**
     * 建構子：初始化基本分頁設定
     *
     * @param data 原始資料列表
     * @param pageSize 每頁顯示項目數
     * @param sortType 排序類型
     */
    public PaginationHelper(List<T> data, int pageSize, String sortType) {
        this.data = data;
        this.pageSize = pageSize;
        this.sortType = sortType;
        this.currentPage = 0;
    }

    /**
     * 建構子：初始化分頁設定，包含年份和月份資訊
     *
     * @param data 原始資料列表
     * @param pageSize 每頁顯示項目數
     * @param sortType 排序類型
     * @param year 年份
     * @param month 月份
     */
    public PaginationHelper(List<T> data, int pageSize, String sortType, int year, int month) {
        this(data, pageSize, sortType);
        this.currentYear = year;
        this.currentMonth = month;
    }

    /**
     * 獲取當前頁的資料
     *
     * @return 當前頁的資料子列表
     */
    public List<T> getCurrentPageData() {
        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, data.size());
        return data.subList(start, end);
    }

    /**
     * 檢查是否有下一頁
     *
     * @return 如果有下一頁返回 true，否則返回 false
     */
    public boolean hasNextPage() {
        return (currentPage + 1) * pageSize < data.size();
    }

    /**
     * 檢查是否有上一頁
     *
     * @return 如果有上一頁返回 true，否則返回 false
     */
    public boolean hasPreviousPage() {
        return currentPage > 0;
    }

    /**
     * 前往下一頁
     * 如果已經是最後一頁，則不進行操作
     */
    public void nextPage() {
        if (hasNextPage()) currentPage++;
    }

    /**
     * 前往上一頁
     * 如果已經是第一頁，則不進行操作
     */
    public void previousPage() {
        if (hasPreviousPage()) currentPage--;
    }

    /**
     * 重設為第一頁
     */
    public void resetPage() {
        currentPage = 0;
    }

    // Getters and Setters
    /**
     * 獲取當前頁碼
     *
     * @return 當前頁碼
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * 獲取排序類型
     *
     * @return 排序類型
     */
    public String getSortType() {
        return sortType;
    }

    /**
     * 獲取總頁數
     *
     * @return 總頁數
     */
    public int getTotalPages() {
        return (int) Math.ceil((double)data.size() / pageSize);
    }

    /**
     * 獲取當前年份
     *
     * @return 當前年份
     */
    public int getCurrentYear() {
        return currentYear;
    }

    /**
     * 獲取當前月份
     *
     * @return 當前月份
     */
    public int getCurrentMonth() {
        return currentMonth;
    }

    /**
     * 設定當前年份
     *
     * @param year 要設定的年份
     */
    public void setCurrentYear(int year) {
        this.currentYear = year;
    }

    /**
     * 設定當前月份
     *
     * @param month 要設定的月份
     */
    public void setCurrentMonth(int month) {
        this.currentMonth = month;
    }
}

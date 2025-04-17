package com.banking.transactionservice.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * 交易实体类
 * 表示系统中的一笔交易记录
 */
public class Transaction {
    private String id;                // 交易ID
    private BigDecimal amount;        // 交易金额
    private String description;       // 交易描述
    private LocalDateTime timestamp;  // 交易时间戳
    private TransactionType type;     // 交易类型
    private String category;          // 交易类别
    private TransactionStatus status; // 交易状态
    
    /**
     * 默认构造函数
     * 生成随机ID并设置默认时间和状态
     */
    public Transaction() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.status = TransactionStatus.COMPLETED;
    }
    
    /**
     * 带参数的构造函数
     * @param amount 交易金额
     * @param description 交易描述
     * @param type 交易类型
     * @param category 交易类别
     */
    public Transaction(BigDecimal amount, String description, TransactionType type, String category) {
        this();
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.category = category;
    }
    
    // Getter 和 Setter 方法
    /**
     * 获取交易ID
     * @return 交易ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置交易ID
     * @param id 交易ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取交易金额
     * @return 交易金额
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * 设置交易金额
     * @param amount 交易金额
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * 获取交易描述
     * @return 交易描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置交易描述
     * @param description 交易描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取交易时间戳
     * @return 交易时间戳
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * 设置交易时间戳
     * @param timestamp 交易时间戳
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * 获取交易类型
     * @return 交易类型
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * 设置交易类型
     * @param type 交易类型
     */
    public void setType(TransactionType type) {
        this.type = type;
    }

    /**
     * 获取交易类别
     * @return 交易类别
     */
    public String getCategory() {
        return category;
    }

    /**
     * 设置交易类别
     * @param category 交易类别
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * 获取交易状态
     * @return 交易状态
     */
    public TransactionStatus getStatus() {
        return status;
    }

    /**
     * 设置交易状态
     * @param status 交易状态
     */
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    /**
     * 判断对象是否相等
     * 仅通过ID判断交易是否相同
     * @param o 比较对象
     * @return 是否相等
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    /**
     * 获取对象哈希码
     * @return 哈希码
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
} 
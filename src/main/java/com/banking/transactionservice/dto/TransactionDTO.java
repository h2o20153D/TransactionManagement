package com.banking.transactionservice.dto;

import com.banking.transactionservice.model.TransactionStatus;
import com.banking.transactionservice.model.TransactionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易数据传输对象
 * 用于在API层与客户端交互的数据模型
 */
public class TransactionDTO {
    private String id;                // 交易ID
    
    @NotNull(message = "金额不能为空")
    @Positive(message = "金额必须为正数")
    private BigDecimal amount;        // 交易金额
    
    @NotBlank(message = "描述不能为空")
    private String description;       // 交易描述
    
    private LocalDateTime timestamp;  // 交易时间戳
    
    @NotNull(message = "交易类型不能为空")
    private TransactionType type;     // 交易类型
    
    @NotBlank(message = "类别不能为空")
    private String category;          // 交易类别
    
    private TransactionStatus status; // 交易状态

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
} 
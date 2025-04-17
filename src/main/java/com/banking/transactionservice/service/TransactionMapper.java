package com.banking.transactionservice.service;

import com.banking.transactionservice.dto.TransactionDTO;
import com.banking.transactionservice.model.Transaction;
import org.springframework.stereotype.Component;

/**
 * 交易对象映射器
 * 负责在DTO和实体对象之间进行转换
 */
@Component
public class TransactionMapper {

    /**
     * 将实体对象转换为DTO
     * @param transaction 交易实体
     * @return 交易DTO
     */
    public TransactionDTO toDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setDescription(transaction.getDescription());
        dto.setTimestamp(transaction.getTimestamp());
        dto.setType(transaction.getType());
        dto.setCategory(transaction.getCategory());
        dto.setStatus(transaction.getStatus());
        
        return dto;
    }

    /**
     * 将DTO转换为实体对象
     * @param dto 交易DTO
     * @return 交易实体
     */
    public Transaction toEntity(TransactionDTO dto) {
        if (dto == null) {
            return null;
        }

        Transaction transaction = new Transaction();
        if (dto.getId() != null) {
            transaction.setId(dto.getId());
        }
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setType(dto.getType());
        transaction.setCategory(dto.getCategory());
        
        if (dto.getTimestamp() != null) {
            transaction.setTimestamp(dto.getTimestamp());
        }
        
        if (dto.getStatus() != null) {
            transaction.setStatus(dto.getStatus());
        }
        
        return transaction;
    }
} 
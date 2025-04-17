package com.banking.transactionservice.service;

import com.banking.transactionservice.dto.TransactionDTO;
import com.banking.transactionservice.model.Transaction;
import com.banking.transactionservice.model.TransactionStatus;
import com.banking.transactionservice.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionMapperTest {

    private TransactionMapper mapper;
    private Transaction transaction;
    private TransactionDTO dto;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        mapper = new TransactionMapper();
        now = LocalDateTime.now();
        
        transaction = new Transaction();
        transaction.setId("1");
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setDescription("Test transaction");
        transaction.setType(TransactionType.CREDIT);
        transaction.setCategory("Test");
        transaction.setTimestamp(now);
        transaction.setStatus(TransactionStatus.COMPLETED);
        
        dto = new TransactionDTO();
        dto.setId("1");
        dto.setAmount(new BigDecimal("100.00"));
        dto.setDescription("Test transaction");
        dto.setType(TransactionType.CREDIT);
        dto.setCategory("Test");
        dto.setTimestamp(now);
        dto.setStatus(TransactionStatus.COMPLETED);
    }

    @Test
    void toDTO_ShouldMapAllFields() {
        TransactionDTO result = mapper.toDTO(transaction);
        
        assertThat(result.getId()).isEqualTo(transaction.getId());
        assertThat(result.getAmount()).isEqualTo(transaction.getAmount());
        assertThat(result.getDescription()).isEqualTo(transaction.getDescription());
        assertThat(result.getType()).isEqualTo(transaction.getType());
        assertThat(result.getCategory()).isEqualTo(transaction.getCategory());
        assertThat(result.getTimestamp()).isEqualTo(transaction.getTimestamp());
        assertThat(result.getStatus()).isEqualTo(transaction.getStatus());
    }
    
    @Test
    void toEntity_ShouldMapAllFields() {
        Transaction result = mapper.toEntity(dto);
        
        assertThat(result.getId()).isEqualTo(dto.getId());
        assertThat(result.getAmount()).isEqualTo(dto.getAmount());
        assertThat(result.getDescription()).isEqualTo(dto.getDescription());
        assertThat(result.getType()).isEqualTo(dto.getType());
        assertThat(result.getCategory()).isEqualTo(dto.getCategory());
        assertThat(result.getTimestamp()).isEqualTo(dto.getTimestamp());
        assertThat(result.getStatus()).isEqualTo(dto.getStatus());
    }
    
    @Test
    void toEntity_WithNullValues_ShouldHandleGracefully() {
        TransactionDTO nullDto = new TransactionDTO();
        nullDto.setId("2");
        
        Transaction result = mapper.toEntity(nullDto);
        
        assertThat(result.getId()).isEqualTo("2");
        assertThat(result.getAmount()).isNull();
        assertThat(result.getDescription()).isNull();
        assertThat(result.getType()).isNull();
        assertThat(result.getCategory()).isNull();
        assertThat(result.getTimestamp()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
    }
    
    @Test
    void toDTO_WithNullValues_ShouldHandleGracefully() {
        Transaction nullTransaction = new Transaction();
        nullTransaction.setId("2");
        
        TransactionDTO result = mapper.toDTO(nullTransaction);
        
        assertThat(result.getId()).isEqualTo("2");
        assertThat(result.getAmount()).isNull();
        assertThat(result.getDescription()).isNull();
        assertThat(result.getType()).isNull();
        assertThat(result.getCategory()).isNull();
        assertThat(result.getTimestamp()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
    }
} 
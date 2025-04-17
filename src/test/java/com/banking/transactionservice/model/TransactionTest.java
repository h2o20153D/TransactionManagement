package com.banking.transactionservice.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionTest {

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        
        Transaction tx1 = new Transaction();
        tx1.setId("1");
        tx1.setAmount(new BigDecimal("100.00"));
        tx1.setDescription("Test");
        tx1.setType(TransactionType.CREDIT);
        tx1.setCategory("Test");
        tx1.setTimestamp(now);
        tx1.setStatus(TransactionStatus.COMPLETED);
        
        Transaction tx2 = new Transaction();
        tx2.setId("1");
        tx2.setAmount(new BigDecimal("100.00"));
        tx2.setDescription("Test");
        tx2.setType(TransactionType.CREDIT);
        tx2.setCategory("Test");
        tx2.setTimestamp(now);
        tx2.setStatus(TransactionStatus.COMPLETED);
        
        Transaction tx3 = new Transaction();
        tx3.setId("2");
        tx3.setAmount(new BigDecimal("200.00"));
        tx3.setDescription("Different");
        tx3.setType(TransactionType.DEBIT);
        tx3.setCategory("Other");
        tx3.setTimestamp(now);
        tx3.setStatus(TransactionStatus.PENDING);
        
        assertThat(tx1).isEqualTo(tx2);
        assertThat(tx1).isNotEqualTo(tx3);
        
        assertThat(tx1.hashCode()).isEqualTo(tx2.hashCode());
        assertThat(tx1.hashCode()).isNotEqualTo(tx3.hashCode());
    }
    
    @Test
    void testGettersAndSetters() {
        LocalDateTime now = LocalDateTime.now();
        BigDecimal amount = new BigDecimal("100.00");
        
        Transaction tx = new Transaction();
        tx.setId("1");
        tx.setAmount(amount);
        tx.setDescription("Test");
        tx.setType(TransactionType.CREDIT);
        tx.setCategory("Test");
        tx.setTimestamp(now);
        tx.setStatus(TransactionStatus.COMPLETED);
        
        assertThat(tx.getId()).isEqualTo("1");
        assertThat(tx.getAmount()).isEqualTo(amount);
        assertThat(tx.getDescription()).isEqualTo("Test");
        assertThat(tx.getType()).isEqualTo(TransactionType.CREDIT);
        assertThat(tx.getCategory()).isEqualTo("Test");
        assertThat(tx.getTimestamp()).isEqualTo(now);
        assertThat(tx.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
    }
    
    @Test
    void testTransactionStatus() {
        assertThat(TransactionStatus.PENDING.name()).isEqualTo("PENDING");
        assertThat(TransactionStatus.COMPLETED.name()).isEqualTo("COMPLETED");
        assertThat(TransactionStatus.FAILED.name()).isEqualTo("FAILED");
        
        assertThat(TransactionStatus.values()).hasSize(3);
    }
    
    @Test
    void testTransactionType() {
        assertThat(TransactionType.CREDIT.name()).isEqualTo("CREDIT");
        assertThat(TransactionType.DEBIT.name()).isEqualTo("DEBIT");
        
        assertThat(TransactionType.values()).hasSize(2);
    }
} 
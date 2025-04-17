package com.banking.transactionservice.repository;

import com.banking.transactionservice.model.Transaction;
import com.banking.transactionservice.model.TransactionStatus;
import com.banking.transactionservice.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionRepositoryTest {

    private TransactionRepository repository;
    private Transaction transaction1;
    private Transaction transaction2;

    @BeforeEach
    void setUp() {
        repository = new TransactionRepository();
        
        transaction1 = new Transaction();
        transaction1.setId("1");
        transaction1.setAmount(new BigDecimal("100.00"));
        transaction1.setDescription("Test transaction 1");
        transaction1.setType(TransactionType.CREDIT);
        transaction1.setCategory("Test");
        transaction1.setTimestamp(LocalDateTime.now());
        transaction1.setStatus(TransactionStatus.COMPLETED);
        
        transaction2 = new Transaction();
        transaction2.setId("2");
        transaction2.setAmount(new BigDecimal("200.00"));
        transaction2.setDescription("Test transaction 2");
        transaction2.setType(TransactionType.DEBIT);
        transaction2.setCategory("Test");
        transaction2.setTimestamp(LocalDateTime.now());
        transaction2.setStatus(TransactionStatus.PENDING);
    }

    @Test
    void save_ShouldStoreTransaction() {
        Transaction saved = repository.save(transaction1);
        
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo("1");
        assertThat(repository.findById("1")).isPresent();
    }
    
    @Test
    void save_ShouldUpdateExistingTransaction() {
        repository.save(transaction1);
        
        transaction1.setAmount(new BigDecimal("150.00"));
        Transaction updated = repository.save(transaction1);
        
        assertThat(updated.getAmount()).isEqualTo(new BigDecimal("150.00"));
        assertThat(repository.findById("1").get().getAmount()).isEqualTo(new BigDecimal("150.00"));
    }

    @Test
    void findById_ShouldReturnTransaction_WhenExists() {
        repository.save(transaction1);
        
        Optional<Transaction> found = repository.findById("1");
        
        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("Test transaction 1");
    }
    
    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        Optional<Transaction> found = repository.findById("999");
        
        assertThat(found).isEmpty();
    }
    
    @Test
    void deleteById_ShouldRemoveTransaction() {
        repository.save(transaction1);
        assertThat(repository.findById("1")).isPresent();
        
        repository.deleteById("1");
        
        assertThat(repository.findById("1")).isEmpty();
    }
    
    @Test
    void existsById_ShouldReturnTrue_WhenExists() {
        repository.save(transaction1);
        
        boolean exists = repository.existsById("1");
        
        assertThat(exists).isTrue();
    }
    
    @Test
    void existsById_ShouldReturnFalse_WhenNotExists() {
        boolean exists = repository.existsById("999");
        
        assertThat(exists).isFalse();
    }
    
    @Test
    void findAll_ShouldReturnAllTransactions() {
        repository.save(transaction1);
        repository.save(transaction2);
        
        List<Transaction> transactions = repository.findAll();
        
        assertThat(transactions).hasSize(2);
        assertThat(transactions).extracting(Transaction::getId).containsExactlyInAnyOrder("1", "2");
    }
    
    @Test
    void findAll_WithPagination_ShouldReturnPagedTransactions() {
        repository.save(transaction1);
        repository.save(transaction2);
        
        List<Transaction> page1 = repository.findAll(0, 1);
        assertThat(page1).hasSize(1);
        
        List<Transaction> page2 = repository.findAll(1, 1);
        assertThat(page2).hasSize(1);
        
        assertThat(page1.get(0).getId()).isNotEqualTo(page2.get(0).getId());
    }
    
    @Test
    void count_ShouldReturnNumberOfTransactions() {
        assertThat(repository.count()).isEqualTo(0);
        
        repository.save(transaction1);
        assertThat(repository.count()).isEqualTo(1);
        
        repository.save(transaction2);
        assertThat(repository.count()).isEqualTo(2);
        
        repository.deleteById("1");
        assertThat(repository.count()).isEqualTo(1);
    }
} 
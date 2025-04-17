package com.banking.transactionservice.service;

import com.banking.transactionservice.dto.TransactionDTO;
import com.banking.transactionservice.exception.ResourceNotFoundException;
import com.banking.transactionservice.model.Transaction;
import com.banking.transactionservice.model.TransactionStatus;
import com.banking.transactionservice.model.TransactionType;
import com.banking.transactionservice.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction transaction;
    private TransactionDTO transactionDTO;
    private Transaction updatedTransaction;
    private TransactionDTO updatedTransactionDTO;

    @BeforeEach
    void setUp() {
        transaction = new Transaction();
        transaction.setId("1");
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setDescription("Test transaction");
        transaction.setType(TransactionType.CREDIT);
        transaction.setCategory("Test");
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.COMPLETED);

        transactionDTO = new TransactionDTO();
        transactionDTO.setId("1");
        transactionDTO.setAmount(new BigDecimal("100.00"));
        transactionDTO.setDescription("Test transaction");
        transactionDTO.setType(TransactionType.CREDIT);
        transactionDTO.setCategory("Test");
        transactionDTO.setTimestamp(LocalDateTime.now());
        transactionDTO.setStatus(TransactionStatus.COMPLETED);
        
        updatedTransaction = new Transaction();
        updatedTransaction.setId("1");
        updatedTransaction.setAmount(new BigDecimal("200.00"));
        updatedTransaction.setDescription("Updated transaction");
        updatedTransaction.setType(TransactionType.DEBIT);
        updatedTransaction.setCategory("Updated");
        updatedTransaction.setTimestamp(LocalDateTime.now());
        updatedTransaction.setStatus(TransactionStatus.COMPLETED);
        
        updatedTransactionDTO = new TransactionDTO();
        updatedTransactionDTO.setId("1");
        updatedTransactionDTO.setAmount(new BigDecimal("200.00"));
        updatedTransactionDTO.setDescription("Updated transaction");
        updatedTransactionDTO.setType(TransactionType.DEBIT);
        updatedTransactionDTO.setCategory("Updated");
        updatedTransactionDTO.setTimestamp(LocalDateTime.now());
        updatedTransactionDTO.setStatus(TransactionStatus.COMPLETED);
    }

    @Test
    void createTransaction_ShouldReturnCreatedTransaction() {
        when(transactionMapper.toEntity(any(TransactionDTO.class))).thenReturn(transaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(transactionMapper.toDTO(any(Transaction.class))).thenReturn(transactionDTO);

        TransactionDTO result = transactionService.createTransaction(transactionDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("1");
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void getTransaction_WithValidId_ShouldReturnTransaction() {
        when(transactionRepository.findById("1")).thenReturn(Optional.of(transaction));
        when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);

        TransactionDTO result = transactionService.getTransaction("1");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("1");
    }

    @Test
    void getTransaction_WithInvalidId_ShouldThrowException() {
        when(transactionRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.getTransaction("999"));
    }

    @Test
    void updateTransaction_WithValidId_ShouldReturnUpdatedTransaction() {
        when(transactionRepository.existsById("1")).thenReturn(true);
        when(transactionMapper.toEntity(any(TransactionDTO.class))).thenReturn(transaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(transactionMapper.toDTO(any(Transaction.class))).thenReturn(transactionDTO);

        TransactionDTO result = transactionService.updateTransaction("1", transactionDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("1");
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void updateTransaction_WithInvalidId_ShouldThrowException() {
        when(transactionRepository.existsById("999")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> transactionService.updateTransaction("999", transactionDTO));
    }

    @Test
    void deleteTransaction_WithValidId_ShouldDeleteTransaction() {
        when(transactionRepository.existsById("1")).thenReturn(true);
        doNothing().when(transactionRepository).deleteById("1");

        transactionService.deleteTransaction("1");

        verify(transactionRepository, times(1)).deleteById("1");
    }

    @Test
    void deleteTransaction_WithInvalidId_ShouldThrowException() {
        when(transactionRepository.existsById("999")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> transactionService.deleteTransaction("999"));
    }

    @Test
    void getAllTransactions_ShouldReturnListOfTransactions() {
        when(transactionRepository.findAll(0, 10)).thenReturn(List.of(transaction));
        when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);

        List<TransactionDTO> result = transactionService.getAllTransactions(0, 10);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("1");
    }

    @Test
    void endToEndTransactionLifecycle_ShouldWorkCorrectly() {
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        
        // 1. 创建交易
        when(transactionMapper.toEntity(any(TransactionDTO.class))).thenReturn(transaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);
        
        TransactionDTO createdDTO = transactionService.createTransaction(transactionDTO);
        assertThat(createdDTO).isNotNull();
        assertThat(createdDTO.getId()).isEqualTo("1");
        
        // 2. 获取交易
        when(transactionRepository.findById("1")).thenReturn(Optional.of(transaction));
        
        TransactionDTO retrievedDTO = transactionService.getTransaction("1");
        assertThat(retrievedDTO).isNotNull();
        assertThat(retrievedDTO.getAmount()).isEqualTo(new BigDecimal("100.00"));
        
        // 3. 更新交易
        when(transactionRepository.existsById("1")).thenReturn(true);
        when(transactionMapper.toEntity(updatedTransactionDTO)).thenReturn(updatedTransaction);
        when(transactionRepository.save(updatedTransaction)).thenReturn(updatedTransaction);
        when(transactionMapper.toDTO(updatedTransaction)).thenReturn(updatedTransactionDTO);
        
        TransactionDTO result = transactionService.updateTransaction("1", updatedTransactionDTO);
        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("200.00"));
        assertThat(result.getDescription()).isEqualTo("Updated transaction");
        
        // 4. 删除交易
        when(transactionRepository.existsById("1")).thenReturn(true);
        doNothing().when(transactionRepository).deleteById("1");
        
        transactionService.deleteTransaction("1");
        
        // 5. 验证删除后找不到交易
        when(transactionRepository.findById("1")).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.getTransaction("1");
        });
        
        // 验证save方法被调用了两次
        verify(transactionRepository, times(2)).save(transactionCaptor.capture());
        // 验证其他调用
        verify(transactionRepository, times(2)).existsById("1");
        verify(transactionRepository).deleteById("1");
        verify(transactionRepository, times(2)).findById("1");
        
        // 检查保存的事务顺序
        List<Transaction> savedTransactions = transactionCaptor.getAllValues();
        assertThat(savedTransactions).hasSize(2);
        // 第一次保存的是创建时的事务
        assertThat(savedTransactions.get(0)).isEqualTo(transaction);
        // 第二次保存的是更新后的事务
        assertThat(savedTransactions.get(1)).isEqualTo(updatedTransaction);
    }
    
    @Test
    void getTransactions_WithPagination_ShouldReturnCorrectPage() {
        Transaction transaction1 = new Transaction();
        transaction1.setId("1");
        Transaction transaction2 = new Transaction();
        transaction2.setId("2");
        Transaction transaction3 = new Transaction();
        transaction3.setId("3");
        
        TransactionDTO dto1 = new TransactionDTO();
        dto1.setId("1");
        TransactionDTO dto2 = new TransactionDTO();
        dto2.setId("2");
        TransactionDTO dto3 = new TransactionDTO();
        dto3.setId("3");
        
        when(transactionRepository.findAll(0, 2)).thenReturn(Arrays.asList(transaction1, transaction2));
        when(transactionMapper.toDTO(transaction1)).thenReturn(dto1);
        when(transactionMapper.toDTO(transaction2)).thenReturn(dto2);
        
        List<TransactionDTO> page1 = transactionService.getAllTransactions(0, 2);
        assertThat(page1).hasSize(2);
        assertThat(page1.get(0).getId()).isEqualTo("1");
        assertThat(page1.get(1).getId()).isEqualTo("2");
        
        when(transactionRepository.findAll(1, 2)).thenReturn(List.of(transaction3));
        when(transactionMapper.toDTO(transaction3)).thenReturn(dto3);
        
        List<TransactionDTO> page2 = transactionService.getAllTransactions(1, 2);
        assertThat(page2).hasSize(1);
        assertThat(page2.get(0).getId()).isEqualTo("3");
    }

    @Test
    void countTransactions_ShouldReturnTotalCount() {
        when(transactionRepository.count()).thenReturn(5L);
        
        long count = transactionService.countTransactions();
        
        assertThat(count).isEqualTo(5L);
        verify(transactionRepository).count();
    }

    @Test
    void updateTransaction_WhenIdDoesNotExist_ShouldThrowException() {
        when(transactionRepository.existsById("999")).thenReturn(false);
        
        assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.updateTransaction("999", transactionDTO);
        });
        
        verify(transactionRepository).existsById("999");
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void deleteTransaction_WhenIdDoesNotExist_ShouldThrowException() {
        when(transactionRepository.existsById("999")).thenReturn(false);
        
        assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.deleteTransaction("999");
        });
        
        verify(transactionRepository).existsById("999");
        verify(transactionRepository, never()).deleteById(anyString());
    }
} 
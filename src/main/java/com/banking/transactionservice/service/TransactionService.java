package com.banking.transactionservice.service;

import com.banking.transactionservice.dto.TransactionDTO;
import com.banking.transactionservice.exception.ResourceNotFoundException;
import com.banking.transactionservice.model.Transaction;
import com.banking.transactionservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = transactionMapper.toEntity(transactionDTO);
        Transaction saved = transactionRepository.save(transaction);
        return transactionMapper.toDTO(saved);
    }

    /**
     * 获取单个交易
     * @param id 交易ID
     * @return 交易DTO
     * @throws ResourceNotFoundException 当交易不存在时抛出
     */
    @Cacheable(value = "transactions", key = "#id", unless = "#result == null")
    public TransactionDTO getTransaction(String id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
        return transactionMapper.toDTO(transaction);
    }

    @Caching(evict = {
            @CacheEvict(value = "transactions", key = "#id"),
            @CacheEvict(value = "allTransactions", allEntries = true)
    })
    public TransactionDTO updateTransaction(String id, TransactionDTO transactionDTO) {
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction not found with ID: " + id);
        }
        
        Transaction transaction = transactionMapper.toEntity(transactionDTO);
        transaction.setId(id);
        Transaction updated = transactionRepository.save(transaction);
        return transactionMapper.toDTO(updated);
    }

    @Caching(evict = {
            @CacheEvict(value = "transactions", key = "#id"),
            @CacheEvict(value = "allTransactions", allEntries = true)
    })
    public void deleteTransaction(String id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction not found with ID: " + id);
        }
        transactionRepository.deleteById(id);
    }

    @Cacheable(value = "allTransactions")
    public List<TransactionDTO> getAllTransactions(int page, int size) {
        List<Transaction> transactions = transactionRepository.findAll(page, size);
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .toList();
    }
    
    public long countTransactions() {
        return transactionRepository.count();
    }
} 
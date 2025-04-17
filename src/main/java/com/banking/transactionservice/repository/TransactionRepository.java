package com.banking.transactionservice.repository;

import com.banking.transactionservice.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 交易数据访问仓库
 * 使用内存中的ConcurrentHashMap存储交易数据
 */
@Repository
public class TransactionRepository {
    private final Map<String, Transaction> transactions = new ConcurrentHashMap<>();

    /**
     * 保存交易记录
     * @param transaction 待保存的交易对象
     * @return 保存后的交易对象
     */
    public Transaction save(Transaction transaction) {
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }

    /**
     * 根据ID查找交易
     * @param id 交易ID
     * @return 包装在Optional中的交易对象
     */
    public Optional<Transaction> findById(String id) {
        return Optional.ofNullable(transactions.get(id));
    }

    /**
     * 根据ID删除交易
     * @param id 要删除的交易ID
     */
    public void deleteById(String id) {
        transactions.remove(id);
    }

    /**
     * 获取所有交易记录
     * @return 所有交易记录的列表
     */
    public List<Transaction> findAll() {
        return new ArrayList<>(transactions.values());
    }

    /**
     * 分页获取交易记录
     * @param page 页码
     * @param size 每页记录数
     * @return 指定页的交易记录列表
     */
    public List<Transaction> findAll(int page, int size) {
        return transactions.values().stream()
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    /**
     * 检查指定ID的交易是否存在
     * @param id 交易ID
     * @return 是否存在
     */
    public boolean existsById(String id) {
        return transactions.containsKey(id);
    }
    
    /**
     * 获取交易总数
     * @return 交易记录总数
     */
    public long count() {
        return transactions.size();
    }
} 
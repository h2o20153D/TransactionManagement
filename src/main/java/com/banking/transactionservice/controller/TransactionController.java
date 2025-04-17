package com.banking.transactionservice.controller;

import com.banking.transactionservice.dto.TransactionDTO;
import com.banking.transactionservice.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 交易控制器类
 * 提供交易相关的RESTful API接口
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * 构造函数，注入交易服务
     * @param transactionService 交易服务实例
     */
    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * 创建新交易
     * @param transactionDTO 交易数据传输对象
     * @return 创建成功的交易信息及201状态码
     */
    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@Valid @RequestBody TransactionDTO transactionDTO) {
        TransactionDTO created = transactionService.createTransaction(transactionDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * 根据ID获取交易信息
     * @param id 交易ID
     * @return 交易信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransaction(@PathVariable String id) {
        TransactionDTO transaction = transactionService.getTransaction(id);
        return ResponseEntity.ok(transaction);
    }

    /**
     * 更新交易信息
     * @param id 要更新的交易ID
     * @param transactionDTO 更新的交易数据
     * @return 更新后的交易信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(
            @PathVariable String id, 
            @Valid @RequestBody TransactionDTO transactionDTO) {
        TransactionDTO updated = transactionService.updateTransaction(id, transactionDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除交易记录
     * @param id 要删除的交易ID
     * @return 204状态码表示成功但无内容返回
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable String id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 分页获取所有交易记录
     * @param page 页码，默认为0
     * @param size 每页记录数，默认为10
     * @return 交易记录列表
     */
    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<TransactionDTO> transactions = transactionService.getAllTransactions(page, size);
        return ResponseEntity.ok(transactions);
    }
    
    /**
     * 获取交易总数
     * @return 包含交易总数的Map
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> countTransactions() {
        long count = transactionService.countTransactions();
        return ResponseEntity.ok(Map.of("count", count));
    }
} 
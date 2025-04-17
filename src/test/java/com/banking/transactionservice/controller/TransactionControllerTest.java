package com.banking.transactionservice.controller;

import com.banking.transactionservice.dto.TransactionDTO;
import com.banking.transactionservice.exception.ResourceNotFoundException;
import com.banking.transactionservice.model.TransactionType;
import com.banking.transactionservice.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private TransactionDTO transactionDTO;

    @BeforeEach
    void setUp() {
        transactionDTO = new TransactionDTO();
        transactionDTO.setId("1");
        transactionDTO.setAmount(new BigDecimal("100.00"));
        transactionDTO.setDescription("Test transaction");
        transactionDTO.setType(TransactionType.CREDIT);
        transactionDTO.setCategory("Test");
    }

    @Test
    void createTransaction_ShouldReturnCreatedStatus() throws Exception {
        given(transactionService.createTransaction(any(TransactionDTO.class))).willReturn(transactionDTO);

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.amount", is(100.00)))
                .andExpect(jsonPath("$.description", is("Test transaction")));
    }

    @Test
    void getTransaction_WithValidId_ShouldReturnTransaction() throws Exception {
        given(transactionService.getTransaction("1")).willReturn(transactionDTO);

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.amount", is(100.00)))
                .andExpect(jsonPath("$.description", is("Test transaction")));
    }

    @Test
    void getTransaction_WithInvalidId_ShouldReturnNotFound() throws Exception {
        given(transactionService.getTransaction("999")).willThrow(new ResourceNotFoundException("Transaction not found"));

        mockMvc.perform(get("/api/transactions/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTransaction_WithValidId_ShouldReturnUpdatedTransaction() throws Exception {
        given(transactionService.updateTransaction(eq("1"), any(TransactionDTO.class))).willReturn(transactionDTO);

        mockMvc.perform(put("/api/transactions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.amount", is(100.00)))
                .andExpect(jsonPath("$.description", is("Test transaction")));
    }

    @Test
    void updateTransaction_WithInvalidId_ShouldReturnNotFound() throws Exception {
        given(transactionService.updateTransaction(eq("999"), any(TransactionDTO.class)))
                .willThrow(new ResourceNotFoundException("Transaction not found"));

        mockMvc.perform(put("/api/transactions/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTransaction_WithValidId_ShouldReturnNoContent() throws Exception {
        doNothing().when(transactionService).deleteTransaction("1");

        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTransaction_WithInvalidId_ShouldReturnNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Transaction not found")).when(transactionService).deleteTransaction("999");

        mockMvc.perform(delete("/api/transactions/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllTransactions_ShouldReturnListOfTransactions() throws Exception {
        List<TransactionDTO> transactions = List.of(transactionDTO);
        given(transactionService.getAllTransactions(0, 10)).willReturn(transactions);

        mockMvc.perform(get("/api/transactions?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].amount", is(100.00)))
                .andExpect(jsonPath("$[0].description", is("Test transaction")));
    }

    @Test
    void updateTransaction_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // 创建一个无效的DTO（缺少必要字段）
        TransactionDTO invalidDTO = new TransactionDTO();
        invalidDTO.setId("1");

        mockMvc.perform(put("/api/transactions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTransaction_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // 创建一个无效的DTO（金额为负）
        TransactionDTO invalidDTO = new TransactionDTO();
        invalidDTO.setAmount(new BigDecimal("-100.00")); // 负金额，应该被验证拦截
        invalidDTO.setDescription("Invalid transaction");
        invalidDTO.setType(TransactionType.CREDIT);
        invalidDTO.setCategory("Test");

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void endToEndTest_TransactionLifecycle() throws Exception {
        // 1. 创建交易
        given(transactionService.createTransaction(any(TransactionDTO.class))).willReturn(transactionDTO);
        
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("1")));
        
        // 2. 获取交易
        given(transactionService.getTransaction("1")).willReturn(transactionDTO);
        
        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")));
                
        // 3. 更新交易
        TransactionDTO updatedDTO = new TransactionDTO();
        updatedDTO.setId("1");
        updatedDTO.setAmount(new BigDecimal("200.00")); // 更新金额
        updatedDTO.setDescription("Updated transaction");
        updatedDTO.setType(TransactionType.CREDIT);
        updatedDTO.setCategory("Updated");
        
        given(transactionService.updateTransaction(eq("1"), any(TransactionDTO.class))).willReturn(updatedDTO);
        
        mockMvc.perform(put("/api/transactions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(200.00)))
                .andExpect(jsonPath("$.description", is("Updated transaction")));
                
        // 4. 删除交易
        doNothing().when(transactionService).deleteTransaction("1");
        
        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isNoContent());
                
        // 5. 验证删除后获取会失败
        doThrow(new ResourceNotFoundException("Transaction not found")).when(transactionService).getTransaction("1");
        
        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isNotFound());
    }
} 
# 银行交易管理系统

这是一个用于管理银行交易的RESTful API服务。该应用程序允许用户创建、更新、删除和检索金融交易信息。

## 功能特点

- 实现业务 API
  - 创建新交易
  - 更新现有交易
  - 删除交易
  - 分页列出所有交易
  - 按ID获取交易
  - 统计交易总数
- 健壮的错误处理机制
- 基于内存的线程安全数据存储
- 缓存机制提升性能

## 技术栈

- Java 21
- Spring Boot 3.2.4
- Maven
- Caffeine 缓存
- Gatling 性能测试
- Docker
- Kubernetes

## 构建和运行应用

### 前提条件

- JDK 21或更高版本
- Maven 3.6或更高版本
- Docker（用于容器化）
- Kubernetes（用于容器编排）

### 使用Maven构建

```
mvn clean package
```

### 本地运行

```
mvn spring-boot:run
```

应用将在 http://localhost:8080 启动

### 构建Docker镜像

```
docker build -t transaction-service:latest .
```

### 运行Docker容器

```
docker run -p 8080:8080 transaction-service:latest
```

### 部署到Kubernetes

```
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

## API 接口

### 创建交易

```
POST /api/transactions
Content-Type: application/json

{
  "amount": 100.00,
  "description": "Grocery shopping",
  "type": "DEBIT",
  "category": "Food"
}
```

### 获取交易

```
GET /api/transactions/{id}
```

### 更新交易

```
PUT /api/transactions/{id}
Content-Type: application/json

{
  "amount": 150.00,
  "description": "Updated grocery shopping",
  "type": "DEBIT",
  "category": "Food"
}
```

### 删除交易

```
DELETE /api/transactions/{id}
```

### 交易列表（分页）

```
GET /api/transactions?page=0&size=10
```

### 统计交易数量

```
GET /api/transactions/count
```


## 单元测试

项目使用JUnit和Mockito进行单元测试，确保各个组件功能正确。测试用例覆盖了正常业务流程和异常情况处理。

### 运行单元测试

运行所有单元测试：

```bash
mvn test
```

运行特定测试类：

```bash
mvn test -Dtest=TransactionServiceTest
```

运行特定测试方法：

```bash
mvn test -Dtest=TransactionServiceTest#createTransaction_ShouldReturnCreatedTransaction
```

### 测试覆盖率报告

项目集成了JaCoCo进行测试覆盖率分析：

```bash
mvn clean test jacoco:report
```

生成的报告位于 `target/site/jacoco/index.html`，可在浏览器中查看。

### 测试覆盖范围

项目的单元测试全面覆盖了各个层次的功能和异常处理：

1. **控制器层测试** (`TransactionControllerTest`)
   - API正常响应测试：验证各接口在正常情况下的返回值和状态码
     - 例如：`createTransaction_ShouldReturnCreatedStatus` 测试创建交易API返回201状态码
   - 参数验证测试：检查API对无效输入的处理
     - 例如：`createTransaction_WithInvalidData_ShouldReturnBadRequest` 测试当金额为负值时API返回400错误
   - 交易不存在测试：验证请求不存在的交易时的错误处理
     - 例如：`getTransaction_WithInvalidId_ShouldReturnNotFound` 测试获取不存在ID时返回404错误
   - 模拟完整的API交互流程（创建→获取→更新→删除→验证删除）

2. **服务层测试** (`TransactionServiceTest`)
   - 业务功能测试：验证所有业务方法是否正确执行
     - 例如：`createTransaction_ShouldReturnCreatedTransaction` 测试创建交易逻辑
   - 异常处理测试：验证异常情况是否正确处理
     - 例如：`updateTransaction_WhenIdDoesNotExist_ShouldThrowException` 测试更新不存在交易时抛出异常
   - 分页功能测试：验证交易列表分页功能
     - 例如：`getTransactions_WithPagination_ShouldReturnCorrectPage` 测试不同页码的交易数据正确性
   - 完整生命周期测试：测试从创建到删除的完整流程
     - 例如：`endToEndTransactionLifecycle_ShouldWorkCorrectly` 验证交易完整CRUD流程

3. **映射器测试** (`TransactionMapperTest`)
   - DTO转实体测试：验证DTO到实体对象是否正确转换
     - 例如：`toEntity_ShouldMapAllFields` 测试所有字段的正确映射
   - 实体转DTO测试：验证实体到DTO对象是否正确转换
     - 例如：`toDTO_ShouldMapAllFields` 测试映射过程中是否没有数据丢失
   - 空值处理测试：验证对象映射时是否正确处理空值
     - 例如：`toEntity_WithNullValues_ShouldHandleGracefully` 测试将有空字段的DTO转为实体

4. **仓库层测试** (`TransactionRepositoryTest`)
   - 数据存储测试：验证交易数据正确保存
     - 例如：`save_ShouldStoreTransaction` 测试交易保存后能被检索
   - 数据更新测试：验证现有交易数据更新功能
     - 例如：`save_ShouldUpdateExistingTransaction` 测试更新已存在交易的字段
   - 数据查询测试：验证按ID查询交易功能
     - 例如：`findById_ShouldReturnTransaction_WhenExists` 测试能正确找到已存在交易
   - 数据删除测试：验证交易删除功能
     - 例如：`deleteById_ShouldRemoveTransaction` 测试删除后交易不再可检索
   - 分页查询测试：验证分页获取交易列表功能
     - 例如：`findAll_WithPagination_ShouldReturnPagedTransactions` 测试分页参数的正确应用

5. **模型层测试** (`TransactionTest`)
   - 实体属性测试：验证实体对象的getter和setter方法
     - 例如：`testGettersAndSetters` 测试设置属性后能正确获取
   - 实体相等性测试：验证equals和hashCode方法的正确实现
     - 例如：`testEqualsAndHashCode` 测试ID相同的交易被视为相等
   - 枚举值测试：验证状态和类型枚举的正确定义
     - 例如：`testTransactionStatus` 测试状态枚举包含正确的值

## 性能测试

项目使用Gatling进行性能测试，模拟高并发场景下系统的表现。

### 执行性能测试

```bash
mvn gatling:test
```

### 性能测试内容

1. **吞吐量测试**
   - 逐步增加用户负载，从每秒10个用户增加到50个用户
   - 维持峰值负载一分钟，测量系统吞吐量
   - 监控系统是否能够持续处理高并发请求

2. **延迟测试**
   - 测试多用户同时读取同一交易的响应时间
   - 通过100个用户同时读取测试缓存效率
   - 分析在不同并发级别下的延迟变化

3. **并发测试场景**
   - 并发创建交易：50-100个并发用户同时创建交易
   - 并发读取相同交易：100个并发用户读取同一交易
   - 监控数据一致性和系统稳定性

4. **完整工作流测试**
   - 模拟用户执行完整业务流程：创建→读取→更新→删除
   - 分析每个步骤的性能瓶颈
   - 测试系统在真实业务场景下的表现

### 性能测试断言

- 平均响应时间 < 1000ms
- 90%请求响应时间 < 2000ms
- 请求成功率 > 99%（错误率 < 1%）

## 外部库

- **Spring Boot Starter Web**: 提供RESTful API功能
- **Spring Boot Starter Validation**: 提供请求载荷验证功能
- **Spring Boot Starter Cache**: 提供缓存抽象
- **Caffeine Cache**: 高性能、接近最优的缓存库
- **Gatling**: 用于性能和负载测试的高性能工具
  
## 项目结构

```
src
├── main
│   ├── java
│   │   └── com.banking.transactionservice
│   │       ├── config                                  // 应用配置
│   │       │   └── CachingConfig.java
│   │       ├── controller                              // 控制器
│   │       │   └── TransactionController.java
│   │       ├── dto                                     // 数据传输对象
│   │       │   └── TransactionDTO.java
│   │       ├── exception                               // 异常处理
│   │       │   ├── ErrorResponse.java
│   │       │   ├── GlobalExceptionHandler.java
│   │       │   └── ResourceNotFoundException.java
│   │       ├── model                                   // 实体模型
│   │       │   ├── Transaction.java
│   │       │   ├── TransactionStatus.java
│   │       │   └── TransactionType.java
│   │       ├── repository                              // 数据访问层
│   │       │   └── TransactionRepository.java
│   │       ├── service                                 // 业务逻辑层
│   │       │   ├── TransactionMapper.java
│   │       │   └── TransactionService.java
│   │       └── TransactionServiceApplication.java      // 应用启动类
│   └── resources
│       └── application.yml                             // 应用配置文件
└── test
    ├── java
    │   └── com.banking.transactionservice
    │       ├── controller
    │       │   └── TransactionControllerTest.java      // 控制器测试
    │       └── service
    │           └── TransactionServiceTest.java         // 业务逻辑测试
    └── scala
        └── com.banking.transactionservice
            └── simulation
                └── TransactionSimulation.scala         // 压力测试模拟
```

此外，项目还包含以下文件：
- `pom.xml` - Maven 项目配置文件
- `Dockerfile` - 用于构建Docker镜像
- `k8s/` - Kubernetes部署配置文件
  - `deployment.yaml` - 部署配置
  - `service.yaml` - 服务配置
  - `configmap.yaml` - 配置映射

## 缓存实现

系统采用了Spring Cache和Caffeine实现高效的缓存机制，提升API性能。

### 缓存配置

在`CachingConfig`类中，我们配置了Caffeine缓存：

```java
@Configuration
public class CachingConfig {
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("transactions", "allTransactions");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)     // 初始容量为100
                .maximumSize(1000)        // 最大容量为1000
                .expireAfterWrite(10, TimeUnit.MINUTES)  // 写入10分钟后过期
                .recordStats());          // 记录缓存状态
        return cacheManager;
    }
}
```

### 缓存应用

在服务层使用多种缓存策略：

1. **单个交易缓存**：通过ID缓存单个交易，避免重复查询
   ```java
   @Cacheable(value = "transactions", key = "#id", unless = "#result == null")
   public TransactionDTO getTransaction(String id) {
       // 实现代码
   }
   ```

2. **列表缓存**：缓存分页查询结果，提高列表查询性能
   ```java
   @Cacheable(value = "allTransactions")
   public List<TransactionDTO> getAllTransactions(int page, int size) {
       // 实现代码
   }
   ```

3. **缓存失效策略**：更新或删除交易时自动更新缓存
   ```java
   @Caching(evict = {
       @CacheEvict(value = "transactions", key = "#id"),
       @CacheEvict(value = "allTransactions", allEntries = true)
   })
   public void deleteTransaction(String id) {
       // 实现代码
   }
   ```

### 缓存优势

- 减少数据库访问，提高响应速度
- 自动过期机制，确保数据最终一致性
- 线程安全的实现，适用于高并发场景

## 分页实现

系统实现了高效的内存分页机制，优化大数据集的访问性能。

### 控制层分页

在控制器中接收分页参数（页码和页大小）：

```java
@GetMapping
public ResponseEntity<List<TransactionDTO>> getAllTransactions(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    List<TransactionDTO> transactions = transactionService.getAllTransactions(page, size);
    return ResponseEntity.ok(transactions);
}
```

### 数据访问层分页

在`TransactionRepository`中实现分页：

```java
public List<Transaction> findAll(int page, int size) {
    return transactions.values().stream()
            .skip((long) page * size)  // 跳过前面的记录
            .limit(size)               // 限制返回数量
            .collect(Collectors.toList());
}
```

### 分页优势

- 减少内存使用，只返回所需数据
- 提高API响应速度，特别是对大数据集
- 支持客户端灵活控制数据加载量
- 配合缓存使用，进一步提升性能


## 完整运行示例

### 安装依赖环境
1. 下载安装 JDK 21 ： https://www.oracle.com/cn/java/technologies/downloads/ 
2. 下载安装最新版 Maven ： https://maven.apache.org/download.cgi

### 运行项目
1. 本地运行
`./run/deploy.sh local`
2. docker 运行
`./run/deploy.sh docker`
3. k8s 部署
`./run/deploy.sh k8s`

### 测试项目
1. 单元测试
`./run/unittest.sh`
2. 性能测试
`./run/gatling.sh`


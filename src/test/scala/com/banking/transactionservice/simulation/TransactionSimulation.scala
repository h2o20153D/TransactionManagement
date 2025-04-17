package com.banking.transactionservice.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import java.util.UUID

class TransactionSimulation extends Simulation {
  
  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .check(status.not(500))
    .shareConnections
    .disableWarmUp
    .connectionHeader("keep-alive")
  
  // 随机交易数据生成器
  val transactionFeeder = Iterator.continually(Map(
    "amount" -> (100 + scala.util.Random.nextInt(900)),
    "description" -> s"Test transaction ${UUID.randomUUID().toString.substring(0, 8)}",
    "type" -> (if (scala.util.Random.nextBoolean()) "CREDIT" else "DEBIT"),
    "category" -> s"Category-${scala.util.Random.nextInt(5) + 1}"
  ))
  
  // TPS测试 - 创建交易并计算吞吐量
  val createTransactionScenario = scenario("Create Transaction TPS Test")
    .feed(transactionFeeder)
    .exec(http("Create Transaction")
      .post("/api/transactions")
      .body(StringBody("""{"amount": ${amount}, "description": "${description}", "type": "${type}", "category": "${category}"}"""))
      .check(status.is(201))
      .check(jsonPath("$.id").exists.saveAs("createdTransactionId")))
    .exitHereIfFailed // 如果创建失败，退出场景
    .exec { session => session }
  
  // 延迟测试 - 获取同一交易
  val getTransactionLatencyScenario = scenario("Get Transaction Latency Test")
    // 先创建一个共享交易
    .feed(transactionFeeder)
    .exec(http("Create Shared Transaction")
      .post("/api/transactions")
      .body(StringBody("""{"amount": 500, "description": "Shared test transaction", "type": "${type}", "category": "Test"}"""))
      .check(status.is(201))
      .check(jsonPath("$.id").exists.saveAs("sharedId")))
    .exitHereIfFailed // 如果创建失败，退出场景
    // 然后反复读取该交易
    .pause(100.milliseconds)
    .repeat(3) {
      exec(http("Get Shared Transaction")
        .get("/api/transactions/${sharedId}")
        .check(status.is(200))
        .check(jsonPath("$.id").is("${sharedId}")))
    }
  
  // 并发创建交易测试
  val concurrentCreateScenario = scenario("Concurrent Create Test")
    .feed(transactionFeeder)
    .exec(http("Concurrent Create")
      .post("/api/transactions")
      .body(StringBody("""{"amount": ${amount}, "description": "Concurrent ${description}", "type": "${type}", "category": "${category}"}"""))
      .check(status.is(201))
      .check(jsonPath("$.id").exists.saveAs("concurrentId")))
  
  // 并发读取测试
  val concurrentReadScenario = scenario("Concurrent Read Test")
    .feed(transactionFeeder)
    .exec(http("Create Transaction For Read")
      .post("/api/transactions")
      .body(StringBody("""{"amount": 800, "description": "Transaction for read", "type": "DEBIT", "category": "Test"}"""))
      .check(status.is(201))
      .check(jsonPath("$.id").exists.saveAs("readId")))
    .exitHereIfFailed
    .pause(100.milliseconds)
    .exec(http("Read Created Transaction")
      .get("/api/transactions/${readId}")
      .check(status.is(200)))
  
  // 完整工作流测试
  val endToEndWorkflowScenario = scenario("End-to-End Workflow")
    .feed(transactionFeeder)
    .exec(http("E2E - Create Transaction")
      .post("/api/transactions")
      .body(StringBody("""{"amount": ${amount}, "description": "E2E ${description}", "type": "${type}", "category": "${category}"}"""))
      .check(status.is(201))
      .check(jsonPath("$.id").exists.saveAs("e2eId")))
    .exitHereIfFailed
    .pause(100.milliseconds)
    .exec(http("E2E - Get Transaction")
      .get("/api/transactions/${e2eId}")
      .check(status.is(200)))
    .pause(100.milliseconds)
    .exec(http("E2E - Update Transaction")
      .put("/api/transactions/${e2eId}")
      .body(StringBody("""{"amount": ${amount}, "description": "Updated ${description}", "type": "${type}", "category": "Updated-${category}"}"""))
      .check(status.is(200)))
    .pause(100.milliseconds)
    .exec(http("E2E - Delete Transaction")
      .delete("/api/transactions/${e2eId}")
      .check(status.is(204)))
  
  val numberOfUsers = 600

  setUp(
    createTransactionScenario.inject(
      rampUsersPerSec(5) to numberOfUsers during(30.seconds),
      constantUsersPerSec(numberOfUsers) during(30.seconds)
    ),
    
    getTransactionLatencyScenario.inject(
      rampUsers(numberOfUsers) during(20.seconds),
      constantUsersPerSec(numberOfUsers) during(20.seconds)
    ),
    
    concurrentCreateScenario.inject(
      constantConcurrentUsers(numberOfUsers) during(20.seconds),
      rampConcurrentUsers(30) to(numberOfUsers) during(15.seconds)
    ),
    
    concurrentReadScenario.inject(
      constantConcurrentUsers(numberOfUsers) during(20.seconds)
    ),
    
    endToEndWorkflowScenario.inject(
      rampUsers(numberOfUsers) during(15.seconds)
    )
  ).protocols(httpProtocol)
   .assertions(
      global.responseTime.mean.lt(200),            // 平均响应时间小于200ms
      global.responseTime.percentile3.lt(100),     // 90%的请求响应时间小于100ms
      global.successfulRequests.percent.gte(99),     // 成功率大于99%
      global.failedRequests.percent.lt(1)           // 失败率小于1%
   )
} 
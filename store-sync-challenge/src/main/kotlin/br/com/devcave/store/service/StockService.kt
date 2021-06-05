package br.com.devcave.store.service

import br.com.devcave.store.client.StockClient
import br.com.devcave.store.domain.response.StockResponse
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class StockService(
    private val stockClient: StockClient
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Retry(name = "retry-stock", fallbackMethod = "retrieveStockFallback")
    @CircuitBreaker(name = "circuitbreaker-stock")
    fun retrieveStock(sku: String): StockResponse? {
        return stockClient.retrieveStock(sku)
    }

    private fun retrieveStockFallback(throwable: Throwable): StockResponse? {
        logger.warn("retrieveStockFallback, problem=${throwable.message}")

        return null
    }
}
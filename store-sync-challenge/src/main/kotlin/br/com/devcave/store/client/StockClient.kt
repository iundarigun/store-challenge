package br.com.devcave.store.client

import br.com.devcave.store.domain.response.StockResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(value = "stockClient", url = "\${feign.client.config.stockClient.url}")
interface StockClient {
    @GetMapping("stock/{sku}")
    fun retrieveStock(@PathVariable sku: String): StockResponse
}
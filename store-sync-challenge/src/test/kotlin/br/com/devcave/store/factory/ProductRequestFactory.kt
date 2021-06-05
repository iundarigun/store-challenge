package br.com.devcave.store.factory

import br.com.devcave.store.configuration.FakerConfiguration.faker
import br.com.devcave.store.domain.request.ProductRequest

object ProductRequestFactory {
    fun build(
        categoryId: Long = faker.number().numberBetween(10_000L, 100_000L),
        minPrice: Long = 100_000L,
        maxPrice: Long = 990_000L
    ): ProductRequest {
        return ProductRequest(
                sku = faker.number().numberBetween(100_000, 999_000).toString(),
                name = faker.commerce().productName(),
                categoryId = categoryId,
                price = faker.number().numberBetween(minPrice, maxPrice)
            )
    }
}

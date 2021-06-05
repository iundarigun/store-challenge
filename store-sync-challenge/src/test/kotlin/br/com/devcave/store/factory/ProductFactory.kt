package br.com.devcave.store.factory

import br.com.devcave.store.configuration.FakerConfiguration.faker
import br.com.devcave.store.domain.entity.Category
import br.com.devcave.store.domain.entity.Product

object ProductFactory {
    fun build(
        categories: List<Category>,
        size: Int = faker.number().numberBetween(5, 15),
        minPrice: Long = 100_000L,
        maxPrice: Long = 990_000L
    ): List<Product> {
        return (1..size).map {
            Product(
                sku = faker.number().numberBetween(100_000, 999_000).toString(),
                name = faker.commerce().productName(),
                category = faker.options().nextElement(categories),
                price = faker.number().numberBetween(minPrice, maxPrice)
            )
        }
    }
}

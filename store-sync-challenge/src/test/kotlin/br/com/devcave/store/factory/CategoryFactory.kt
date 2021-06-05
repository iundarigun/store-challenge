package br.com.devcave.store.factory

import br.com.devcave.store.configuration.FakerConfiguration.faker
import br.com.devcave.store.domain.entity.Category

object CategoryFactory {
    fun build(size: Int = faker.number().numberBetween(5, 10)): List<Category> {
        return (1..size).map {
            Category(
                name = faker.lorem().characters(10, 100)
            )
        }
    }
}
package br.com.devcave.store.service

import br.com.devcave.store.domain.Price
import br.com.devcave.store.domain.ProductResponse
import br.com.devcave.store.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val categoryService: CategoryService,
    private val productRepository: ProductRepository
) {
    suspend fun findByParams(category: String?, priceLessThan: Long?): Flow<ProductResponse> {
        val categoryToFilter = category?.let { categoryService.getByName(it) }

        return productRepository.findByParams(categoryToFilter?.id, priceLessThan)
            .map {
                ProductResponse(
                    sku = it.sku,
                    name = it.name,
                    category = categoryToFilter?.name ?: categoryService.getById(it.categoryId).name,
                    price = Price(
                        original = it.price,
                        final = it.price
                    )
                )
            }
    }
}

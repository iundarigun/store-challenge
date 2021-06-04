package br.com.devcave.store.service

import br.com.devcave.store.domain.Price
import br.com.devcave.store.domain.Product
import br.com.devcave.store.domain.ProductResponse
import br.com.devcave.store.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val categoryService: CategoryService,
    private val discountService: DiscountService,
    private val productRepository: ProductRepository
) {
    @Transactional(readOnly = true)
    suspend fun findByParams(category: String?, priceLessThan: Long?): Flow<ProductResponse> {
        val categoryToFilter = category?.let { categoryService.getByName(it) }

        return productRepository.findByParams(categoryToFilter?.id, priceLessThan)
            .map { it.toResponse() }
    }

    @Suppress("MagicNumber")
    private suspend fun Product.toResponse(): ProductResponse {
        val categoryName = categoryService.getById(this.categoryId).name
        val discount = discountService.findTheBestDiscount(this.categoryId, this.id)
        return ProductResponse(
            sku = this.sku,
            name = this.name,
            category = categoryName,
            price = Price(
                original = this.price,
                final = discount?.let { this.price * (100 - it.value) / 100 } ?: this.price,
                discountPercentage = discount?.let { "${discount.value}%" }
            )
        )
    }
}

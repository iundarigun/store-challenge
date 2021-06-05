package br.com.devcave.store.service

import br.com.devcave.store.domain.response.PageResponse
import br.com.devcave.store.domain.response.ProductCustomerResponse
import br.com.devcave.store.domain.response.ProductResponse
import br.com.devcave.store.exception.ApplicationException
import br.com.devcave.store.repository.ProductRepository
import br.com.devcave.store.extension.toProductCustomerResponse
import br.com.devcave.store.extension.toProductResponse
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val categoryService: CategoryService,
    private val productRepository: ProductRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    @Cacheable("product", key = "'findByParams:'.concat(#categoryName?:'').concat(':').concat(#priceLessThan?:'')")
    fun findByParams(categoryName: String? = null, priceLessThan: Long? = null): List<ProductCustomerResponse> {
        logger.info("findByParams $categoryName, $priceLessThan not found on cache. Getting from database")

        val category = categoryName?.let { categoryService.getByName(it) }
        return productRepository.findByParams(category?.id, priceLessThan)
            .map { it.toProductCustomerResponse() }
    }

    @Transactional(readOnly = true)
    @Cacheable("product", key = "'findPageable:'.concat(#page).concat(':').concat(#size)")
    fun findPageable(page: Int, size: Int): PageResponse<ProductResponse> {
        logger.info("findPageable $page, $size not found on cache. Getting from database")

        val result = productRepository.findBy(PageRequest.of(page - 1, size))

        return PageResponse(
            currentPage = page,
            totalPages = result.totalPages,
            totalElements = result.totalElements,
            content = result.content.map { it.toProductResponse(false) }
        )
    }

    @Transactional(readOnly = true)
    @Cacheable("product", key = "'findById:'.concat(#id)")
    fun findById(id: Long): ProductResponse {
        logger.info("findById $id not found on cache. Getting from database")

        val result = productRepository.findById(id)
            .orElseThrow { ApplicationException(HttpStatus.NOT_FOUND, "Category not found") }
        return result.toProductResponse(true)
    }
}

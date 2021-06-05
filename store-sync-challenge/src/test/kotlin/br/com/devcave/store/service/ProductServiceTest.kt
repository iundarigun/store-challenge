package br.com.devcave.store.service

import br.com.devcave.store.exception.ApplicationException
import br.com.devcave.store.factory.CategoryFactory
import br.com.devcave.store.factory.ProductFactory
import br.com.devcave.store.factory.ProductRequestFactory
import br.com.devcave.store.repository.ProductRepository
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("unit")
class ProductServiceTest {
    private val stockService = mockk<StockService>()
    private val categoryService = mockk<CategoryService>()
    private val productRepository = mockk<ProductRepository>()

    private val productService = ProductService(stockService, categoryService, productRepository)

    @BeforeEach
    fun cleanMockk() {
        clearAllMocks()
    }

    @Test
    fun `create product successfully`() {
        val request = ProductRequestFactory.build()

        every {
            productRepository.existsBySku(any())
        } returns false
        every {
            productRepository.existsByNameAndCategoryId(any(), any())
        } returns false
        val category = CategoryFactory.build(1)[0]
        every {
            categoryService.getById(any())
        } returns category
        val product = ProductFactory.build(listOf(category), 1)[0]
        every {
            productRepository.save(any())
        } returns product

        val result = kotlin.runCatching { productService.create(request) }
        Assertions.assertTrue(result.isSuccess)

        verify(exactly = 1) {
            productRepository.existsBySku(requireNotNull(request.sku))
            productRepository.existsByNameAndCategoryId(request.name, any())
            categoryService.getById(any())
            productRepository.save(any())
        }
    }

    @Test
    fun `create product with sku exists`() {
        val request = ProductRequestFactory.build()

        every {
            productRepository.existsBySku(any())
        } returns true

        val result = kotlin.runCatching { productService.create(request) }
        Assertions.assertTrue(result.isFailure)
        Assertions.assertTrue(result.exceptionOrNull() is ApplicationException)

        verify(exactly = 1) {
            productRepository.existsBySku(requireNotNull(request.sku))
        }
        verify(exactly = 0) {
            productRepository.existsByNameAndCategoryId(request.name, any())
            categoryService.getById(any())
            productRepository.save(any())
        }
    }

    @Test
    fun `create product with name and category exists`() {
        val request = ProductRequestFactory.build()

        every {
            productRepository.existsBySku(any())
        } returns false
        every {
            productRepository.existsByNameAndCategoryId(any(), any())
        } returns true
        val result = kotlin.runCatching { productService.create(request) }
        Assertions.assertTrue(result.isFailure)
        Assertions.assertTrue(result.exceptionOrNull() is ApplicationException)

        verify(exactly = 1) {
            productRepository.existsBySku(requireNotNull(request.sku))
            productRepository.existsByNameAndCategoryId(request.name, any())
        }
        verify(exactly = 0) {
            categoryService.getById(any())
            productRepository.save(any())
        }
    }
}
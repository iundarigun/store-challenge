package br.com.devcave.store.service

import br.com.devcave.store.configuration.FakerConfiguration.faker
import br.com.devcave.store.domain.Discount
import br.com.devcave.store.domain.DiscountType
import br.com.devcave.store.domain.Product
import br.com.devcave.store.exception.ApplicationException
import br.com.devcave.store.factory.CategoryFactory
import br.com.devcave.store.factory.ProductFactory
import br.com.devcave.store.repository.ProductRepository
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.cache.CacheProperties
import org.springframework.boot.autoconfigure.cache.CacheType
import org.springframework.http.HttpStatus
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

@Tag("unit")
class ProductServiceTest {
    private val categoryService = mockk<CategoryService>()
    private val discountService = mockk<DiscountService>()
    private val productRepository = mockk<ProductRepository>()
    private val cacheProperties = mockk<CacheProperties>()
    private val cacheService = CacheService(mockk(relaxed = true), mockk(relaxed = true), cacheProperties)
    private val productService = ProductService(categoryService, discountService, cacheService, productRepository)

    @BeforeEach
    fun before() {
        clearAllMocks()
        every {
            cacheProperties.type
        } returns CacheType.NONE
    }

    @Test
    fun `product list successfully`() {
        val category = CategoryFactory.build(1)[0]
        val products = ProductFactory.build(listOf(category.id), 5)
        val discount = Discount(type = DiscountType.PRODUCT, referenceId = products[0].id, value = 10)

        coEvery {
            productRepository.findByParams(any(), any())
        } returns Flux.fromIterable(products).asFlow()

        coEvery {
            categoryService.getById(any())
        } returns category

        coEvery {
            discountService.findTheBestDiscount(any(), any())
        } returns null
        coEvery {
            discountService.findTheBestDiscount(category.id, products[0].id)
        } returns discount

        runBlocking {
            StepVerifier.create(productService.findByParams(null, null).asFlux())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete()
        }

        coVerify(exactly = 1) {
            productRepository.findByParams(null, null)
        }
        coVerify(exactly = 0) {
            categoryService.getByName(any())
        }
        coVerify(exactly = 5) {
            categoryService.getById(any())
            discountService.findTheBestDiscount(any(), any())
        }
    }

    @Test
    fun `product list filtering by category successfully`() {
        val category = CategoryFactory.build(1)[0]
        val products = ProductFactory.build(listOf(category.id), 5)

        coEvery {
            productRepository.findByParams(any(), any())
        } returns Flux.fromIterable(products).asFlow()

        coEvery {
            categoryService.getByName(any())
        } returns category

        coEvery {
            categoryService.getById(any())
        } returns category

        coEvery {
            discountService.findTheBestDiscount(any(), any())
        } returns null

        runBlocking {
            StepVerifier.create(productService.findByParams(category.name, null).asFlux())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete()
        }

        coVerify(exactly = 1) {
            productRepository.findByParams(category.id, null)
            categoryService.getByName(category.name)
        }
        coVerify(exactly = 5) {
            categoryService.getById(any())
            discountService.findTheBestDiscount(any(), any())
        }
    }

    @Test
    fun `empty product list`() {
        coEvery {
            productRepository.findByParams(any(), any())
        } returns Flux.fromIterable(emptyList<Product>()).asFlow()

        runBlocking {
            StepVerifier.create(productService.findByParams(null, null).asFlux())
                .expectSubscription()
                .verifyComplete()
        }

        coVerify(exactly = 1) {
            productRepository.findByParams(null, null)
        }
        coVerify(exactly = 0) {
            categoryService.getByName(any())
            categoryService.getById(any())
            discountService.findTheBestDiscount(any(), any())
        }
    }

    @Test
    fun `find products for wrong category`() {
        val category = faker.commerce().department()
        coEvery {
            categoryService.getByName(any())
        } throws ApplicationException(HttpStatus.NOT_FOUND, "Not found")

        runBlocking {
            val result = runCatching {
                productService.findByParams(category, null).asFlux()
            }
            Assertions.assertTrue(result.isFailure)
        }

        coVerify(exactly = 1) {
            categoryService.getByName(category)
        }
        coVerify(exactly = 0) {
            productRepository.findByParams(any(), any())
            categoryService.getById(any())
            discountService.findTheBestDiscount(any(), any())
        }
    }
}
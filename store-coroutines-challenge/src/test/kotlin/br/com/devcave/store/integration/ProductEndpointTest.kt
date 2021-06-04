package br.com.devcave.store.integration

import br.com.devcave.store.configuration.FakerConfiguration.faker
import br.com.devcave.store.domain.Discount
import br.com.devcave.store.domain.DiscountType
import br.com.devcave.store.domain.ProductResponse
import br.com.devcave.store.factory.CategoryFactory
import br.com.devcave.store.factory.ProductFactory
import br.com.devcave.store.repository.CategoryRepository
import br.com.devcave.store.repository.DiscountRepository
import br.com.devcave.store.repository.ProductRepository
import io.restassured.RestAssured
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductEndpointTest(
    private val discountRepository: DiscountRepository,
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) {
    @LocalServerPort
    private val port: Int? = null

    @BeforeEach
    fun before() {
        RestAssured.port = port ?: 8080
        runBlocking {
            discountRepository.deleteAll()
            productRepository.deleteAll()
            categoryRepository.deleteAll()
        }
    }

    @Test
    fun `get products successfully`() {
        val categories = CategoryFactory.build().map { runBlocking { categoryRepository.save(it).id } }
        val products = ProductFactory.build(categories)
            .map { runBlocking { productRepository.save(it) } }

        val responseList = RestAssured
            .given()
            .get("/products")
            .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath()
            .getList("", ProductResponse::class.java)

        Assertions.assertEquals(products.size, responseList.size)
        products.forEach { expected ->
            val response = responseList.firstOrNull { it.sku == expected.sku }
            assertAll(
                { Assertions.assertNotNull(response) },
                { Assertions.assertEquals(expected.name, response?.name) },
                { Assertions.assertEquals(expected.price, response?.price?.original) },
                { Assertions.assertEquals(expected.price, response?.price?.final) },
                { Assertions.assertNull(response?.price?.discountPercentage) }
            )
        }
    }

    @Test
    fun `get products filtering by category successfully`() {
        val categories = CategoryFactory.build().map { runBlocking { categoryRepository.save(it) } }
        val productsInCategory = ProductFactory.build(categories.subList(0, 1).map { it.id }, 5)
            .map { runBlocking { productRepository.save(it) } }
        val productsInOthersCategories = ProductFactory
            .build(categories.subList(1, categories.size).map { it.id }, 5)
            .map { runBlocking { productRepository.save(it) } }

        val responseList = RestAssured
            .given()
            .queryParam("category", categories[0].name)
            .get("/products")
            .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath()
            .getList("", ProductResponse::class.java)

        Assertions.assertEquals(productsInCategory.size, responseList.size)

        productsInCategory.forEach { expected ->
            val response = responseList.firstOrNull { it.sku == expected.sku }
            assertAll(
                { Assertions.assertNotNull(response) },
                { Assertions.assertEquals(expected.name, response?.name) },
                { Assertions.assertEquals(categories[0].name, response?.category) },
                { Assertions.assertEquals(expected.price, response?.price?.original) },
                { Assertions.assertEquals(expected.price, response?.price?.final) },
                { Assertions.assertNull(response?.price?.discountPercentage) }
            )
        }
        productsInOthersCategories.forEach { nonExpected ->
            val response = responseList.firstOrNull { it.sku == nonExpected.sku }
            Assertions.assertNull(response)
        }
    }

    @Test
    fun `get products filtering by category not found`() {
        val category = faker.commerce().department()

        RestAssured
            .given()
            .queryParam("category", category)
            .get("/products")
            .then()
            .log().all()
            .statusCode(HttpStatus.NOT_FOUND.value())
    }

    @Test
    fun `get products filtering by price less successfully`() {
        val categories = CategoryFactory.build().map { runBlocking { categoryRepository.save(it).id } }
        val cheapProducts = ProductFactory.build(
            categories = categories, maxPrice = 500_000L
        )
            .map { runBlocking { productRepository.save(it) } }
        val expensiveProducts = ProductFactory
            .build(categories, minPrice = 500_001L)
            .map { runBlocking { productRepository.save(it) } }

        val responseList = RestAssured
            .given()
            .queryParam("priceLessThan", "500000")
            .get("/products")
            .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath()
            .getList("", ProductResponse::class.java)

        Assertions.assertEquals(cheapProducts.size, responseList.size)

        cheapProducts.forEach { expected ->
            val response = responseList.firstOrNull { it.sku == expected.sku }
            assertAll(
                { Assertions.assertNotNull(response) },
                { Assertions.assertEquals(expected.name, response?.name) },
                { Assertions.assertEquals(expected.price, response?.price?.original) },
                { Assertions.assertEquals(expected.price, response?.price?.final) },
                { Assertions.assertNull(response?.price?.discountPercentage) }
            )
        }
        expensiveProducts.forEach { nonExpected ->
            val response = responseList.firstOrNull { it.sku == nonExpected.sku }
            Assertions.assertNull(response)
        }
    }

    @Test
    fun `get products with category discount`() {
        val categories = CategoryFactory.build().map { runBlocking { categoryRepository.save(it).id } }
        runBlocking {
            discountRepository.save(Discount(type = DiscountType.CATEGORY, referenceId = categories[0], value = 30))
        }
        val productsWithDiscount = ProductFactory.build(categories.subList(0, 1))
            .map { runBlocking { productRepository.save(it) } }
        val productsWithoutDiscount = ProductFactory
            .build(categories.subList(1, categories.size))
            .map { runBlocking { productRepository.save(it) } }

        val responseList = RestAssured
            .given()
            .get("/products")
            .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath()
            .getList("", ProductResponse::class.java)

        Assertions.assertEquals(productsWithDiscount.size + productsWithoutDiscount.size, responseList.size)

        productsWithDiscount.forEach { expected ->
            val response = responseList.firstOrNull { it.sku == expected.sku }
            assertAll(
                { Assertions.assertNotNull(response) },
                { Assertions.assertEquals(expected.name, response?.name) },
                { Assertions.assertEquals(expected.price, response?.price?.original) },
                { Assertions.assertTrue(expected.price > requireNotNull(response?.price?.final)) },
                { Assertions.assertEquals("30%", response?.price?.discountPercentage) }
            )
        }
        productsWithoutDiscount.forEach { expected ->
            val response = responseList.firstOrNull { it.sku == expected.sku }
            assertAll(
                { Assertions.assertNotNull(response) },
                { Assertions.assertEquals(expected.name, response?.name) },
                { Assertions.assertEquals(expected.price, response?.price?.original) },
                { Assertions.assertEquals(expected.price, response?.price?.final) },
                { Assertions.assertNull(response?.price?.discountPercentage) }
            )
        }
    }

    @Test
    fun `get products with product discount`() {
        val categories = CategoryFactory.build().map { runBlocking { categoryRepository.save(it).id } }
        val productsWithDiscount = ProductFactory.build(categories)
            .map { runBlocking { productRepository.save(it) } }
        productsWithDiscount.map {
            runBlocking {
                discountRepository.save(Discount(type = DiscountType.PRODUCT, referenceId = it.id, value = 15))
            }
        }
        val productsWithoutDiscount = ProductFactory.build(categories)
            .map { runBlocking { productRepository.save(it) } }

        val responseList = RestAssured
            .given()
            .get("/products")
            .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath()
            .getList("", ProductResponse::class.java)

        Assertions.assertEquals(productsWithDiscount.size + productsWithoutDiscount.size, responseList.size)

        productsWithDiscount.forEach { expected ->
            val response = responseList.firstOrNull { it.sku == expected.sku }
            assertAll(
                { Assertions.assertNotNull(response) },
                { Assertions.assertEquals(expected.name, response?.name) },
                { Assertions.assertEquals(expected.price, response?.price?.original) },
                { Assertions.assertTrue(expected.price > requireNotNull(response?.price?.final)) },
                { Assertions.assertEquals("15%", response?.price?.discountPercentage) }
            )
        }
        productsWithoutDiscount.forEach { expected ->
            val response = responseList.firstOrNull { it.sku == expected.sku }
            assertAll(
                { Assertions.assertNotNull(response) },
                { Assertions.assertEquals(expected.name, response?.name) },
                { Assertions.assertEquals(expected.price, response?.price?.original) },
                { Assertions.assertEquals(expected.price, response?.price?.final) },
                { Assertions.assertNull(response?.price?.discountPercentage) }
            )
        }
    }

    @Test
    fun `get products with product and category discount`() {
        val categories = CategoryFactory.build().map { runBlocking { categoryRepository.save(it).id } }
        val productsWithDiscount = ProductFactory.build(categories.subList(0, 1))
            .map { runBlocking { productRepository.save(it) } }
        productsWithDiscount.map {
            runBlocking {
                discountRepository.save(Discount(type = DiscountType.PRODUCT, referenceId = it.id, value = 15))
            }
        }
        runBlocking {
            discountRepository.save(Discount(type = DiscountType.CATEGORY, referenceId = categories[0], value = 30))
        }
        val productsWithoutDiscount = ProductFactory.build(categories.subList(1, categories.size))
            .map { runBlocking { productRepository.save(it) } }

        val responseList = RestAssured
            .given()
            .get("/products")
            .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath()
            .getList("", ProductResponse::class.java)

        Assertions.assertEquals(productsWithDiscount.size + productsWithoutDiscount.size, responseList.size)

        productsWithDiscount.forEach { expected ->
            val response = responseList.firstOrNull { it.sku == expected.sku }
            assertAll(
                { Assertions.assertNotNull(response) },
                { Assertions.assertEquals(expected.name, response?.name) },
                { Assertions.assertEquals(expected.price, response?.price?.original) },
                { Assertions.assertTrue(expected.price > requireNotNull(response?.price?.final)) },
                { Assertions.assertEquals("15%", response?.price?.discountPercentage) }
            )
        }
        productsWithoutDiscount.forEach { expected ->
            val response = responseList.firstOrNull { it.sku == expected.sku }
            assertAll(
                { Assertions.assertNotNull(response) },
                { Assertions.assertEquals(expected.name, response?.name) },
                { Assertions.assertEquals(expected.price, response?.price?.original) },
                { Assertions.assertEquals(expected.price, response?.price?.final) },
                { Assertions.assertNull(response?.price?.discountPercentage) }
            )
        }
    }
}

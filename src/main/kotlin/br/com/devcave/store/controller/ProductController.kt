package br.com.devcave.store.controller

import br.com.devcave.store.domain.ProductResponse
import br.com.devcave.store.service.ProductService
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("products")
class ProductController(
    private val productService: ProductService
) {

    @GetMapping
    suspend fun getAll(
        @RequestParam(required = false) category: String? = null,
        @RequestParam(required = false) priceLessThan: Long? = null
    ): Flow<ProductResponse> {
        return productService.findByParams(category, priceLessThan)
    }
}
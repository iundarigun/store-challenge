package br.com.devcave.store.controller

import br.com.devcave.store.domain.response.ProductCustomerResponse
import br.com.devcave.store.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("products")
class PublicProductController(
    private val productService: ProductService
) {

    @GetMapping
    fun findByParams(
        @RequestParam(required = false) category: String? = null,
        @RequestParam(required = false) priceLessThan: Long? = null
    ): ResponseEntity<List<ProductCustomerResponse>> {
        val response = productService.findByParams(category, priceLessThan)
        return ResponseEntity.ok(response)
    }
}
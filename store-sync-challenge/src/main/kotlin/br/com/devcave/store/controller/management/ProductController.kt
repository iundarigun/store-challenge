package br.com.devcave.store.controller.management

import br.com.devcave.store.domain.request.ProductRequest
import br.com.devcave.store.domain.response.PageResponse
import br.com.devcave.store.domain.response.ProductResponse
import br.com.devcave.store.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@Validated
@RestController
@RequestMapping("management/products")
class ProductController(
    private val productService: ProductService
) {

    @GetMapping
    fun findAll(
        @RequestParam(required = false, defaultValue = "1")
        @Min(value = 1)
        page: Int,
        @RequestParam(required = false, defaultValue = "20")
        @Min(value = 2)
        @Max(value = 50)
        size: Int
    ): ResponseEntity<PageResponse<ProductResponse>> {
        val response = productService.findPageable(page, size)
        return ResponseEntity.ok(response)
    }

    @GetMapping("{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<ProductResponse> {
        val response = productService.findById(id)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    fun create(@Valid @RequestBody request: ProductRequest): ResponseEntity<Long> {
        val id = productService.create(request)
        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .build(id)

        return ResponseEntity.created(location).body(id)
    }

    @PutMapping("{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: ProductRequest
    ): ResponseEntity<Long> {
        productService.update(id, request)

        return ResponseEntity.noContent().build()
    }
}
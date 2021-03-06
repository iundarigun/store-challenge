package br.com.devcave.store.extension

import br.com.devcave.store.domain.entity.Category
import br.com.devcave.store.domain.entity.Product
import br.com.devcave.store.domain.request.ProductRequest
import br.com.devcave.store.domain.response.PriceResponse
import br.com.devcave.store.domain.response.ProductCustomerResponse
import br.com.devcave.store.domain.response.ProductResponse

@Suppress("MagicNumber")
fun Product.toProductCustomerResponse(): ProductCustomerResponse {
    val discountValue = this.discount?.value ?: this.category.discount?.value
    return ProductCustomerResponse(
        sku = this.sku,
        name = this.name,
        category = category.name,
        price = PriceResponse(
            original = this.price,
            final = discountValue?.let { it * (100 - it) / 100 } ?: this.price,
            discountPercentage = discountValue?.let { "$it%" }
        )
    )
}

fun Product.toProductResponse(detailed: Boolean = false, stock: Long? = null): ProductResponse {
    return ProductResponse(
        id = this.id,
        sku = this.sku,
        name = this.name,
        category = if (detailed) category.toCategoryResponse() else null,
        categoryId = if (!detailed) category.id else null,
        price = this.price,
        discount = if (detailed) discount?.toDiscountResponse() else null,
        discountId = if (!detailed) discount?.id else null,
        stock = stock
    )
}

fun ProductRequest.toProduct(category: Category): Product =
    Product(
        sku = requireNotNull(this.sku),
        name = this.name,
        category = category,
        price = this.price
    )

fun ProductRequest.toUpdateProduct(product: Product, category: Category): Product =
    product.copy(
        name = this.name,
        category = category,
        price = this.price
    )

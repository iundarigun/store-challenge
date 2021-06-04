package br.com.devcave.store.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class ProductResponse(
    val sku: String,
    val name: String,
    val category: String,
    val price: Price
)

data class Price(
    val original: Long,
    val final: Long,
    @field:JsonProperty("discount_percentage")
    val discountPercentage: String? = null,
    val currency: String = "EUR"
)

package br.com.devcave.store.domain.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class ProductCustomerResponse(
    val sku: String,
    val name: String,
    val category: String,
    val price: PriceResponse
) : Serializable

data class PriceResponse(
    val original: Long,
    val final: Long,
    @field:JsonProperty("discount_percentage")
    val discountPercentage: String? = null,
    val currency: String = "EUR"
) : Serializable
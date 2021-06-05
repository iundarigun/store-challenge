package br.com.devcave.store.domain.response

import com.fasterxml.jackson.annotation.JsonInclude
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ProductResponse(
    val id: Long,
    val sku: String,
    val name: String,
    val categoryId: Long? = null,
    val category: CategoryResponse? = null,
    val price: Long,
    val discountId: Long? = null,
    val discount: DiscountResponse? = null,
    val stock: Long? = null
) : Serializable

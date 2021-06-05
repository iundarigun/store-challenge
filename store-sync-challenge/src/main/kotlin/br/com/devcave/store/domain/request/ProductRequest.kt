package br.com.devcave.store.domain.request

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Null

data class ProductRequest(
    @field:Null(groups = [ProductUpdate::class])
    @field:NotEmpty(groups = [ProductCreate::class])
    val sku: String?,

    val name: String,

    val categoryId: Long,

    val price: Long
)
interface ProductCreate
interface ProductUpdate

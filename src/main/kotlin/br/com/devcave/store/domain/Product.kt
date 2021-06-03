package br.com.devcave.store.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
data class Product(
    @Id
    val id: Long = 0,

    val sku: String,

    val name: String,

    val categoryId: Long,

    val price: Long
)
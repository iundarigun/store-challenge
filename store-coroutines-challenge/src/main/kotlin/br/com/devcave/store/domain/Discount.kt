package br.com.devcave.store.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
data class Discount(
    @Id
    val id: Long = 0,

    val type: DiscountType,

    val referenceId: Long,

    val value: Long
)
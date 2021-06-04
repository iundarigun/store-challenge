package br.com.devcave.store.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
data class Category(
    @Id
    val id: Long = 0,

    val name: String
)
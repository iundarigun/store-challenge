package br.com.devcave.store.domain.response

import java.io.Serializable

data class CategoryResponse(
    val id: Long,
    val name: String
) : Serializable

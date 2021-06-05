package br.com.devcave.store.domain.response

import java.io.Serializable

data class PageResponse<T>(
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val content: List<T>
) : Serializable

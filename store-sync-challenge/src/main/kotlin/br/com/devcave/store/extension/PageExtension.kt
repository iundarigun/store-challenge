package br.com.devcave.store.extension

import br.com.devcave.store.domain.response.PageResponse
import org.springframework.data.domain.Page

fun <T> Page<T>.toPageResponse() =
    PageResponse(
        currentPage = this.number + 1,
        totalPages = this.totalPages,
        totalElements = this.totalElements,
        content = this.content
    )
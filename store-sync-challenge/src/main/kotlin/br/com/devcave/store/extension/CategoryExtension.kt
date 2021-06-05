package br.com.devcave.store.extension

import br.com.devcave.store.domain.entity.Category
import br.com.devcave.store.domain.response.CategoryResponse

fun Category.toCategoryResponse(): CategoryResponse =
    CategoryResponse(this.id, this.name)
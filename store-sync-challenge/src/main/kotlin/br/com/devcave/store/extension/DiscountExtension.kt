package br.com.devcave.store.extension

import br.com.devcave.store.domain.entity.DiscountProduct
import br.com.devcave.store.domain.response.DiscountResponse

fun DiscountProduct.toDiscountResponse(): DiscountResponse =
    DiscountResponse(
        id = this.id,
        type = this.type,
        value = this.value)
package br.com.devcave.store.domain.response

import br.com.devcave.store.domain.entity.DiscountType
import java.io.Serializable

data class DiscountResponse(
    val id: Long,
    val type: DiscountType,
    val value: Long
) : Serializable

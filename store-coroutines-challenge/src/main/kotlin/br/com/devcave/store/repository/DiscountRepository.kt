package br.com.devcave.store.repository

import br.com.devcave.store.domain.Discount
import br.com.devcave.store.domain.DiscountType
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface DiscountRepository : CoroutineCrudRepository<Discount, Long> {
    suspend fun findByTypeAndReferenceId(type: DiscountType, referenceId: Long): Discount?
}
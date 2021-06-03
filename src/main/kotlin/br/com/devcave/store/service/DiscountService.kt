package br.com.devcave.store.service

import br.com.devcave.store.domain.DiscountType
import br.com.devcave.store.repository.DiscountRepository
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DiscountService(
    private val discountRepository: DiscountRepository
) {
    @Transactional(readOnly = true)
    suspend fun findTheBestDiscount(categoryId: Long, productId: Long): Long? {
        return listOf(
            DiscountType.PRODUCT to productId,
            DiscountType.CATEGORY to categoryId
        ).asFlow()
            .mapNotNull { discountRepository.findByTypeAndReferenceId(it.first, it.second) }
            .firstOrNull()?.value
    }
}

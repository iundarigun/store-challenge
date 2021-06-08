package br.com.devcave.store.service

import br.com.devcave.store.domain.Discount
import br.com.devcave.store.domain.DiscountType
import br.com.devcave.store.repository.DiscountRepository
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DiscountService(
    private val cacheService: CacheService,
    private val discountRepository: DiscountRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    suspend fun findTheBestDiscount(categoryId: Long, productId: Long): Discount? {
        return cacheService.verifyAndExecute("$categoryId:$productId", Discount::class.java) {
            logger.info(
                "findTheBestDiscount cat $categoryId, prod $productId. not found on cache. Getting from database"
            )
            listOf(
                DiscountType.PRODUCT to productId,
                DiscountType.CATEGORY to categoryId
            ).asFlow()
                .mapNotNull { discountRepository.findByTypeAndReferenceId(it.first, it.second) }
                .firstOrNull()
        }
    }
}

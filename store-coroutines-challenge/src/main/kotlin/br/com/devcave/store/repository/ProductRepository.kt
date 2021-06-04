package br.com.devcave.store.repository

import br.com.devcave.store.domain.Product
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ProductRepository : CoroutineCrudRepository<Product, Long> {
    @Query(
        """select * from Product p 
        where (p.category_id = :categoryId or :categoryId is null) and
        (p.price <= :priceLessThan or :priceLessThan is null)
    """
    )
    suspend fun findByParams(categoryId: Long? = null, priceLessThan: Long?): Flow<Product>
}
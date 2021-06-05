package br.com.devcave.store.repository

import br.com.devcave.store.domain.entity.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface ProductRepository : CrudRepository<Product, Long> {
    @Query(
        """from Product p where 
        (p.category.id = :categoryId or :categoryId is null) and
        (p.price <= :priceLessThan or :priceLessThan is null)
    """
    )
    @EntityGraph("findByParams", attributePaths = ["category", "discount", "category.discount"])
    fun findByParams(categoryId: Long?, priceLessThan: Long?): List<Product>

    @EntityGraph("findByParams", attributePaths = ["category", "discount", "category.discount"])
    fun findBy(pageable: Pageable): Page<Product>

    fun existsBySku(sku: String): Boolean

    fun existsByNameAndCategoryId(name: String, categoryId: Long): Boolean

    fun existsByNameAndCategoryIdAndIdNot(name: String, categoryId: Long, id: Long): Boolean
}
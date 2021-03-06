package br.com.devcave.store.service

import br.com.devcave.store.domain.Category
import br.com.devcave.store.exception.ApplicationException
import br.com.devcave.store.repository.CategoryRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val cacheService: CacheService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    suspend fun getById(id: Long): Category {
        return cacheService.verifyAndExecute(id, Category::class.java) {
            logger.info("getById $id not found on cache. Getting from database")
            categoryRepository.findById(id)
        } ?: throw ApplicationException(HttpStatus.NOT_FOUND, "Category not found")
    }

    @Transactional(readOnly = true)
    suspend fun getByName(name: String): Category {
        return cacheService.verifyAndExecute(name, Category::class.java) {
            logger.info("getByName $name not found on cache. Getting from database")
            categoryRepository.findByName(name)
        } ?: throw ApplicationException(HttpStatus.NOT_FOUND, "Category not found")
    }
}

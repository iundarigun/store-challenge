package br.com.devcave.store.service

import br.com.devcave.store.domain.entity.Category
import br.com.devcave.store.exception.ApplicationException
import br.com.devcave.store.repository.CategoryRepository
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Cacheable("category")
    @Transactional(readOnly = true)
    fun getByName(name: String): Category {
        logger.info("getByName $name not found on cache. Getting from database")
        return categoryRepository.findByName(name).orElseThrow {
            ApplicationException(HttpStatus.NOT_FOUND, "Category not found")
        }
    }
}

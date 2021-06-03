package br.com.devcave.store.service

import br.com.devcave.store.domain.Category
import br.com.devcave.store.exception.ApplicationException
import br.com.devcave.store.repository.CategoryRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository
) {

    suspend fun getById(id:Long): Category =
        categoryRepository.findById(id)?: throw ApplicationException(HttpStatus.NOT_FOUND, "Category not found")
}

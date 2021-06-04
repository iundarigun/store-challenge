package br.com.devcave.store.repository

import br.com.devcave.store.domain.Category
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CategoryRepository : CoroutineCrudRepository<Category, Long> {
    suspend fun findByName(name: String): Category?
}
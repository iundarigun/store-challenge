package br.com.devcave.store.repository

import br.com.devcave.store.domain.entity.Category
import org.springframework.data.repository.CrudRepository
import java.util.Optional

interface CategoryRepository : CrudRepository<Category, Long> {
    fun findByName(name: String): Optional<Category>
}
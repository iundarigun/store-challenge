package br.com.devcave.store.repository

import br.com.devcave.store.domain.Category
import br.com.devcave.store.domain.Product
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface CategoryRepository : CoroutineCrudRepository<Category, Long>
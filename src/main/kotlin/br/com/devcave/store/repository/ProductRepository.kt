package br.com.devcave.store.repository

import br.com.devcave.store.domain.Product
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ProductRepository : CoroutineCrudRepository<Product, Long>
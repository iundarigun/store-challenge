package br.com.devcave.store.repository

import br.com.devcave.store.domain.entity.DiscountCategory
import org.springframework.data.repository.CrudRepository

interface DiscountCategoryRepository : CrudRepository<DiscountCategory, Long>
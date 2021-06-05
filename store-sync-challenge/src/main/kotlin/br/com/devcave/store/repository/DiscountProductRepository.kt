package br.com.devcave.store.repository

import br.com.devcave.store.domain.entity.DiscountProduct
import org.springframework.data.repository.CrudRepository

interface DiscountProductRepository : CrudRepository<DiscountProduct, Long>
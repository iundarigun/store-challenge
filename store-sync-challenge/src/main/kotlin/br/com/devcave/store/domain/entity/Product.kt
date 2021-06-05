package br.com.devcave.store.domain.entity

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToOne

@Entity
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val sku: String,

    val name: String,

    @ManyToOne
    val category: Category,

    val price: Long,

    @OneToOne(mappedBy = "product")
    val discount: DiscountProduct? = null,

    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

package br.com.devcave.store.domain.entity

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToOne

@Entity
data class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val name: String,

    @OneToOne(mappedBy = "category")
    val discount: DiscountCategory? = null,

    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
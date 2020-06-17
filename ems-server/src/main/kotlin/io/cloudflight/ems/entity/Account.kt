package io.cloudflight.ems.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne


@Entity(name = "account")
data class Account (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val surname: String,

    @ManyToOne(optional = false)
    val accountRole: AccountRole,

    @Column(nullable = false)
    val password: String

)

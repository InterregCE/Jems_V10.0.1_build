package io.cloudflight.jems.server.user.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "account")
data class User (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true)
    @field:NotNull
    val email: String,

    @field:NotNull
    val name: String,

    @field:NotNull
    val surname: String,

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_role_id")
    @field:NotNull
    val userRole: UserRole,

    @field:NotNull
    val password: String

)

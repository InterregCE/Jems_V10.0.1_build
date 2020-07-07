package io.cloudflight.ems.entity

import javax.persistence.*


@Entity(name = "account")
data class User (

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
    @JoinColumn(name = "account_role_id")
    val userRole: UserRole,

    @Column(nullable = false)
    val password: String

)

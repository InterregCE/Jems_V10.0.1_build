package io.cloudflight.ems.user.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Column


@Entity(name = "account_profile")
data class UserProfile (
    @Id
    @Column(name = "account_id", nullable = false)
    val id: Long,

    @Column
    val language: String?

)

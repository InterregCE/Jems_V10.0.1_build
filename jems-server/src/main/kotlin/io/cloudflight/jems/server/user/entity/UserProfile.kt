package io.cloudflight.jems.server.user.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Column
import javax.validation.constraints.NotNull


@Entity(name = "account_profile")
data class UserProfile (

    @Id
    @Column(name = "account_id")
    @field:NotNull
    val id: Long,

    val language: String?

)

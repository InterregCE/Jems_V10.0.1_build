package io.cloudflight.jems.server.programme.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity(name = "programme_fund")
data class ProgrammeFund(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val abbreviation: String? = null,

    val description: String? = null,

    @field:NotNull
    val selected: Boolean = false

)

package io.cloudflight.jems.server.programme.entity

import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity(name = "programme_strategy")
data class ProgrammeStrategyEntity(

    @Id
    @Enumerated(EnumType.STRING)
    @field:NotNull
    val strategy: ProgrammeStrategy,

    @field:NotNull
    val active: Boolean
)

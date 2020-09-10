package io.cloudflight.ems.strategy.entity

import io.cloudflight.ems.api.strategy.ProgrammeStrategy
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Column
import javax.persistence.Enumerated
import javax.persistence.EnumType

@Entity(name = "programme_strategy")
data class Strategy (

    @Id
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val strategy: ProgrammeStrategy,

    @Column(nullable = false)
    val active: Boolean
)

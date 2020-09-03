package io.cloudflight.ems.strategy.entity

import io.cloudflight.ems.api.strategy.ProgrammeStrategy
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Column

@Entity(name = "programme_strategy")
data class Strategy (
    @Id
    @Column(nullable = false)
    val strategy: ProgrammeStrategy,

    @Column(nullable = false)
    val active: Boolean
)

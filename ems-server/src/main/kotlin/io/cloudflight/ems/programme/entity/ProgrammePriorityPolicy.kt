package io.cloudflight.ems.programme.entity

import io.cloudflight.ems.api.programme.dto.ProgrammeObjectivePolicy
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity(name = "programme_priority_policy")
data class ProgrammePriorityPolicy(

    @Id
    @Column(name = "programme_objective_policy_code", nullable = false)
    @Enumerated(EnumType.STRING)
    val programmeObjectivePolicy: ProgrammeObjectivePolicy,

    @Column(nullable = false, unique = true)
    val code: String

)

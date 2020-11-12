package io.cloudflight.jems.server.programme.entity

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "programme_priority")
data class ProgrammePriority(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true)
    @field:NotNull
    val code: String,

    @Column(unique = true)
    @field:NotNull
    val title: String,

    @Column(name = "objective_id")
    @Enumerated(EnumType.STRING)
    @field:NotNull
    val objective: ProgrammeObjective,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_priority_id", nullable = false, insertable = true)
    val programmePriorityPolicies: Set<ProgrammePriorityPolicy>

)

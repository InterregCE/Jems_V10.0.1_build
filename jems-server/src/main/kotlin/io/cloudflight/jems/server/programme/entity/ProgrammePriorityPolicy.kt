package io.cloudflight.jems.server.programme.entity

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import java.util.Objects
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "programme_priority_policy")
data class ProgrammePriorityPolicy(

    @Id
    @Column(name = "programme_objective_policy_code")
    @Enumerated(EnumType.STRING)
    @field:NotNull
    val programmeObjectivePolicy: ProgrammeObjectivePolicy,

    @Column(unique = true)
    @field:NotNull
    val code: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_priority_id", insertable = false, updatable = false)
    val programmePriority: ProgrammePriority? = null    // here null is because of hibernate, but when retrieved it cannot be null

) {
    override fun hashCode(): Int {
        return Objects.hashCode(programmeObjectivePolicy)
    }

    override fun equals(other: Any?): Boolean = (other is ProgrammePriorityPolicy)
        && programmeObjectivePolicy == other.programmeObjectivePolicy
        && code == other.code

    override fun toString(): String {
        return "${this.javaClass.simpleName}(programmeObjectivePolicy=$programmeObjectivePolicy, code=$code)"
    }

}

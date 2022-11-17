package io.cloudflight.jems.server.programme.entity

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import java.util.Objects
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity(name = "programme_priority_specific_objective")
data class ProgrammeSpecificObjectiveEntity(

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
    val programmePriority: ProgrammePriorityEntity? = null,    // here null is because of hibernate, but when retrieved it cannot be null

    @OneToMany(mappedBy = "dimensionCodeId.specificObjectiveEntity", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val dimensionCodes: MutableSet<ProgrammeObjectiveDimensionCodeEntity> = mutableSetOf()

) {
    override fun hashCode(): Int {
        return Objects.hashCode(programmeObjectivePolicy)
    }

    override fun equals(other: Any?): Boolean = (other is ProgrammeSpecificObjectiveEntity)
        && programmeObjectivePolicy == other.programmeObjectivePolicy
        && code == other.code

    override fun toString(): String {
        return "${this.javaClass.simpleName}(programmeObjectivePolicy=$programmeObjectivePolicy, code=$code)"
    }

}

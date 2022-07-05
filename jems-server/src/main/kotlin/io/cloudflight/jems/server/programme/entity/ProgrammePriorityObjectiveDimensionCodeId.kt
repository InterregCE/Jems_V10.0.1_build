package io.cloudflight.jems.server.programme.entity

import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeObjectiveDimension
import java.io.Serializable
import java.util.Objects
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.ManyToOne
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.validation.constraints.NotNull

@Embeddable
data class ProgrammePriorityObjectiveDimensionCodeId(

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val programmeObjectiveDimension: ProgrammeObjectiveDimension,

    @field:NotNull
    val code: String,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "objective_code")
    @field:NotNull
    val specificObjectiveEntity: ProgrammeSpecificObjectiveEntity

): Serializable {

    override fun equals(other: Any?): Boolean = this === other ||
        other is ProgrammePriorityObjectiveDimensionCodeId
        && programmeObjectiveDimension == other.programmeObjectiveDimension
        && code == other.code
        && specificObjectiveEntity.code == other.specificObjectiveEntity.code

    override fun hashCode(): Int = Objects.hash(programmeObjectiveDimension, code, specificObjectiveEntity)
}

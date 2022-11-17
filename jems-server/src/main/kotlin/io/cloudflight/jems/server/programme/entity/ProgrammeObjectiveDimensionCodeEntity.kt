package io.cloudflight.jems.server.programme.entity

import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "programme_objective_dimension_code")
class ProgrammeObjectiveDimensionCodeEntity(
    @EmbeddedId
    val dimensionCodeId: ProgrammePriorityObjectiveDimensionCodeId
)

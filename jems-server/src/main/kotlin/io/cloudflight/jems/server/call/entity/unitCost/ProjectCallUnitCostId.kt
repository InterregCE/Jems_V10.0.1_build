package io.cloudflight.jems.server.call.entity.unitCost

import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import java.io.Serializable
import java.util.Objects
import javax.persistence.Embeddable
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Embeddable
class ProjectCallUnitCostId(

    @ManyToOne
    @field:NotNull
    val projectCall: CallEntity,

    @ManyToOne
    @field:NotNull
    val programmeUnitCost: ProgrammeUnitCostEntity,

) : Serializable {

    override fun equals(other: Any?): Boolean = this === other ||
        other is ProjectCallUnitCostId && projectCall == other.projectCall && programmeUnitCost == other.programmeUnitCost

    override fun hashCode(): Int = Objects.hash(projectCall, programmeUnitCost)

}

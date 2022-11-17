package io.cloudflight.jems.server.call.entity.unitCost

import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_call_unit_cost")
class ProjectCallUnitCostEntity(

    @EmbeddedId
    val id: ProjectCallUnitCostId,

)

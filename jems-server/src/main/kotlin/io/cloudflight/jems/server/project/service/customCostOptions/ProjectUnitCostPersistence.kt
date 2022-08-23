package io.cloudflight.jems.server.project.service.customCostOptions

import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost

interface ProjectUnitCostPersistence {

    fun getAvailableUnitCostsForProjectId(projectId: Long, version: String? = null): List<ProgrammeUnitCost>

    fun getProjectUnitCost(projectId: Long, unitCostId: Long, version: String? = null): ProgrammeUnitCost

    fun existProjectUnitCost(projectId: Long, unitCostId: Long): Boolean

    fun getProjectUnitCostList(projectId: Long, version: String? = null): List<ProgrammeUnitCost>

    fun getCount(projectId: Long): Long

    fun deleteProjectUnitCost(projectId: Long, unitCostId: Long)

}

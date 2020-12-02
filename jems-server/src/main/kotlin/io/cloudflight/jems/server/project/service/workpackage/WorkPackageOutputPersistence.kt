package io.cloudflight.jems.server.project.service.workpackage

import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutputUpdate

interface WorkPackageOutputPersistence {

    fun updateWorkPackageOutputs(
        projectId: Long,
        workPackageOutputs: Set<WorkPackageOutputUpdate>,
        workPackageId: Long
    ): Set<WorkPackageOutput>

    fun getWorkPackageOutputsForWorkPackage(workPackageId: Long): Set<WorkPackageOutput>

}

package io.cloudflight.jems.server.project.service.workpackage.update_work_package_output

import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutputUpdate

interface UpdateWorkPackageOutputInteractor {
    fun updateWorkPackageOutputs(
        projectId: Long,
        workPackageOutputUpdate: Set<WorkPackageOutputUpdate>,
        workPackageId: Long
    ): Set<WorkPackageOutput>
}

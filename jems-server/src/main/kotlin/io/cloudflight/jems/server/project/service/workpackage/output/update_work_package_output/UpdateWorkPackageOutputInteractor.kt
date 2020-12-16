package io.cloudflight.jems.server.project.service.workpackage.output.update_work_package_output

import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutputUpdate

interface UpdateWorkPackageOutputInteractor {

    fun updateWorkPackageOutputs(
        workPackageId: Long,
        workPackageOutputUpdate: Set<WorkPackageOutputUpdate>,
    ): Set<WorkPackageOutput>

}

package io.cloudflight.jems.server.project.service.workpackage.output.update_work_package_output

import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput

interface UpdateWorkPackageOutputInteractor {

    fun updateWorkPackageOutputs(
        workPackageId: Long,
        workPackageOutputUpdate: List<WorkPackageOutput>,
    ): List<WorkPackageOutput>

}

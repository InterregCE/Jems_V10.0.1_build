package io.cloudflight.jems.server.project.service.workpackage.output.update_work_package_output

import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput

interface UpdateWorkPackageOutputInteractor {

    fun updateOutputsForWorkPackage(
        workPackageId: Long,
        outputs: List<WorkPackageOutput>,
    ): List<WorkPackageOutput>

}

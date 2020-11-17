package io.cloudflight.jems.server.project.service.workpackage.update_work_package_output

import io.cloudflight.jems.api.project.dto.workpackage.workpackageoutput.InputWorkPackageOutput

interface UpdateWorkPackageOutputInteractor {
    fun updateWorkPackageOutputs(projectId: Long, workPackageOutput: Set<InputWorkPackageOutput>, workPackageId: Long)
}
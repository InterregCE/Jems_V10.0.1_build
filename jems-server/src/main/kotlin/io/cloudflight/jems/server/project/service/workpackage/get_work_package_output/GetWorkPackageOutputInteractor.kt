package io.cloudflight.jems.server.project.service.workpackage.get_work_package_output

import io.cloudflight.jems.api.project.dto.workpackage.workpackageoutput.OutputWorkPackageOutput

interface GetWorkPackageOutputInteractor {
    fun getWorkPackageOutputsForWorkPackage(projectId: Long, workPackageId: Long): Set<OutputWorkPackageOutput>
}
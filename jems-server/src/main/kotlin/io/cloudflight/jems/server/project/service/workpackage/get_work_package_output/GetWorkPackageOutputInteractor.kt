package io.cloudflight.jems.server.project.service.workpackage.get_work_package_output

import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutput

interface GetWorkPackageOutputInteractor {
    fun getWorkPackageOutputsForWorkPackage(projectId: Long, workPackageId: Long): Set<WorkPackageOutput>
}
package io.cloudflight.jems.server.project.service.workpackage.get_work_package_output

import io.cloudflight.jems.server.project.authorization.CanReadProject
import io.cloudflight.jems.server.project.service.workpackage.WorkPackageOutputPersistence
import org.springframework.stereotype.Service

@Service
class GetWorkPackageOutput(
    private val workPackageOutputPersistence: WorkPackageOutputPersistence
) : GetWorkPackageOutputInteractor {

    @CanReadProject
    override fun getWorkPackageOutputsForWorkPackage(projectId: Long, workPackageId: Long) =
        workPackageOutputPersistence.getWorkPackageOutputsForWorkPackage(workPackageId)

}
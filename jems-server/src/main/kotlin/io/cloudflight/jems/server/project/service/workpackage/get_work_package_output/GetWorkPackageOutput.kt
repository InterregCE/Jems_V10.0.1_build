package io.cloudflight.jems.server.project.service.workpackage.get_work_package_output

import io.cloudflight.jems.server.project.authorization.CanReadProject
import io.cloudflight.jems.server.project.service.workpackage.WorkPackageOutputPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetWorkPackageOutput(
    private val workPackageOutputPersistence: WorkPackageOutputPersistence
) : GetWorkPackageOutputInteractor {

    @CanReadProject
    @Transactional(readOnly = true)
    override fun getWorkPackageOutputsForWorkPackage(projectId: Long, workPackageId: Long) =
        workPackageOutputPersistence.getWorkPackageOutputsForWorkPackage(workPackageId)
}
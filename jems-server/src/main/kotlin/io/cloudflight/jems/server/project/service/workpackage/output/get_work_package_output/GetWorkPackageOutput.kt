package io.cloudflight.jems.server.project.service.workpackage.output.get_work_package_output

import io.cloudflight.jems.server.project.authorization.CanRetrieveProject
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetWorkPackageOutput(
    private val workPackagePersistence: WorkPackagePersistence
) : GetWorkPackageOutputInteractor {

    @CanRetrieveProject
    @Transactional(readOnly = true)
    override fun getOutputsForWorkPackage(projectId: Long, workPackageId: Long, version: String?) =
        workPackagePersistence.getWorkPackageOutputsForWorkPackage(workPackageId, projectId, version)

}

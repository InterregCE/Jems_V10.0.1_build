package io.cloudflight.jems.server.project.service.workpackage.output.get_work_package_output

import io.cloudflight.jems.server.project.authorization.CanReadProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetWorkPackageOutput(
    private val workPackagePersistence: WorkPackagePersistence
) : GetWorkPackageOutputInteractor {

    @CanReadProjectWorkPackage
    @Transactional(readOnly = true)
    override fun getOutputsForWorkPackage(workPackageId: Long) =
        workPackagePersistence.getWorkPackageOutputsForWorkPackage(workPackageId)

}

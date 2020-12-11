package io.cloudflight.jems.server.project.service.workpackage.update_work_package_output

import io.cloudflight.jems.server.project.authorization.CanUpdateProject
import io.cloudflight.jems.server.project.service.workpackage.WorkPackageOutputPersistence
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutputUpdate
import io.cloudflight.jems.server.project.service.workpackage.validateWorkPackageOutputsLimit
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateWorkPackageOutput(
    private val workPackageOutputPersistence: WorkPackageOutputPersistence
) : UpdateWorkPackageOutputInteractor {

    @CanUpdateProject
    @Transactional
    override fun updateWorkPackageOutputs(
        projectId: Long,
        workPackageOutputUpdate: Set<WorkPackageOutputUpdate>,
        workPackageId: Long
    ): Set<WorkPackageOutput> {
        validateWorkPackageOutputsLimit(workPackageOutputUpdate)
        return workPackageOutputPersistence.updateWorkPackageOutputs(projectId, workPackageOutputUpdate, workPackageId)
    }
}
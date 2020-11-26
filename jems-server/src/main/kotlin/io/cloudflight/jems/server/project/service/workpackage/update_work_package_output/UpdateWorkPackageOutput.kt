package io.cloudflight.jems.server.project.service.workpackage.update_work_package_output

import io.cloudflight.jems.api.project.dto.workpackage.workpackageoutput.InputWorkPackageOutput
import io.cloudflight.jems.server.project.authorization.CanUpdateProject
import io.cloudflight.jems.server.project.service.workpackage.WorkPackageOutputPersistence
import org.springframework.stereotype.Service

@Service
class UpdateWorkPackageOutput(
    private val workPackageOutputPersistence: WorkPackageOutputPersistence
) : UpdateWorkPackageOutputInteractor {

    @CanUpdateProject
    override fun updateWorkPackageOutputs(projectId: Long, workPackageOutput: Set<InputWorkPackageOutput>, workPackageId: Long) =
        workPackageOutputPersistence.updateWorkPackageOutputs(projectId, workPackageOutput, workPackageId)
}
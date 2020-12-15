package io.cloudflight.jems.server.project.controller.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.output.WorkPackageOutputDTO
import io.cloudflight.jems.api.project.dto.workpackage.output.WorkPackageOutputUpdateDTO
import io.cloudflight.jems.api.project.workpackage.ProjectWorkPackageOutputApi
import io.cloudflight.jems.server.project.service.workpackage.output.get_work_package_output.GetWorkPackageOutputInteractor
import io.cloudflight.jems.server.project.service.workpackage.output.update_work_package_output.UpdateWorkPackageOutputInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectWorkPackageOutputController(
    private val getWorkPackageOutputInteractor: GetWorkPackageOutputInteractor,
    private val updateWorkPackageOutputInteractor: UpdateWorkPackageOutputInteractor,
) : ProjectWorkPackageOutputApi {

    override fun getWorkPackageOutputs(workPackageId: Long): Set<WorkPackageOutputDTO> {
        return getWorkPackageOutputInteractor.getWorkPackageOutputsForWorkPackage(workPackageId)
            .toWorkPackageOutputDTOSet()
    }

    override fun updateWorkPackageOutputs(
        workPackageId: Long,
        workPackageOutputUpdateDTO: Set<WorkPackageOutputUpdateDTO>
    ): Set<WorkPackageOutputDTO> =
        updateWorkPackageOutputInteractor.updateWorkPackageOutputs(
            workPackageId,
            workPackageOutputUpdateDTO.toWorkPackageOutputUpdateSet(),
        ).toWorkPackageOutputDTOSet()

}

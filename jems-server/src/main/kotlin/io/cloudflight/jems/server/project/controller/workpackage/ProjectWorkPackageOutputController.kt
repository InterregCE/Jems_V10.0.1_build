package io.cloudflight.jems.server.project.controller.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.output.WorkPackageOutputDTO
import io.cloudflight.jems.api.project.workpackage.ProjectWorkPackageOutputApi
import io.cloudflight.jems.server.project.service.workpackage.output.get_work_package_output.GetWorkPackageOutputInteractor
import io.cloudflight.jems.server.project.service.workpackage.output.update_work_package_output.UpdateWorkPackageOutputInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectWorkPackageOutputController(
    private val getOutputInteractor: GetWorkPackageOutputInteractor,
    private val updateOutputInteractor: UpdateWorkPackageOutputInteractor,
) : ProjectWorkPackageOutputApi {

    override fun getOutputs(workPackageId: Long, version: String?): List<WorkPackageOutputDTO> =
        getOutputInteractor.getOutputsForWorkPackage(workPackageId, version).toDto()

    override fun updateOutputs(
        workPackageId: Long,
        outputs: List<WorkPackageOutputDTO>
    ): List<WorkPackageOutputDTO> =
        updateOutputInteractor.updateOutputsForWorkPackage(workPackageId, outputs.toModel()).toDto()

}

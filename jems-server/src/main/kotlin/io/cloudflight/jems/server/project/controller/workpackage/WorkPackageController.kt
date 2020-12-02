package io.cloudflight.jems.server.project.controller.workpackage

import io.cloudflight.jems.api.project.WorkPackageApi
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageCreate
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageUpdate
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackageSimple
import io.cloudflight.jems.api.project.dto.workpackage.workpackageoutput.WorkPackageOutputDTO
import io.cloudflight.jems.api.project.dto.workpackage.workpackageoutput.WorkPackageOutputUpdateDTO
import io.cloudflight.jems.server.project.service.workpackage.WorkPackageService
import io.cloudflight.jems.server.project.service.workpackage.get_work_package_output.GetWorkPackageOutputInteractor
import io.cloudflight.jems.server.project.service.workpackage.update_work_package_output.UpdateWorkPackageOutputInteractor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class WorkPackageController(
    private val workPackageService: WorkPackageService,
    private val getWorkPackageOutputInteractor: GetWorkPackageOutputInteractor,
    private val updateWorkPackageOutputInteractor: UpdateWorkPackageOutputInteractor
) : WorkPackageApi {

    @PreAuthorize("@projectAuthorization.canReadProject(#projectId)")
    override fun getWorkPackageById(projectId: Long, id: Long): OutputWorkPackage {
        return workPackageService.getWorkPackageById(id)
    }

    @PreAuthorize("@projectAuthorization.canReadProject(#projectId)")
    override fun getWorkPackagesByProjectId(projectId: Long, pageable: Pageable): Page<OutputWorkPackageSimple> {
        return workPackageService.getWorkPackagesByProjectId(projectId, pageable)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#projectId)")
    override fun createWorkPackage(projectId: Long, inputWorkPackageCreate: InputWorkPackageCreate): OutputWorkPackage {
        return workPackageService.createWorkPackage(projectId, inputWorkPackageCreate)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#projectId)")
    override fun updateWorkPackage(projectId: Long, inputWorkPackageUpdate: InputWorkPackageUpdate): OutputWorkPackage {
        return workPackageService.updateWorkPackage(projectId, inputWorkPackageUpdate)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#projectId)")
    override fun deleteWorkPackage(projectId: Long, id: Long) {
        return workPackageService.deleteWorkPackage(projectId, id)
    }

    override fun getWorkPackageOutputs(projectId: Long, id: Long): Set<WorkPackageOutputDTO> {
        return getWorkPackageOutputInteractor.getWorkPackageOutputsForWorkPackage(projectId, id)
            .toWorkPackageOutputDTOSet()
    }

    override fun updateWorkPackageOutputs(
        projectId: Long,
        id: Long,
        workPackageOutputUpdateDTO: Set<WorkPackageOutputUpdateDTO>
    ): Set<WorkPackageOutputDTO> =
        updateWorkPackageOutputInteractor.updateWorkPackageOutputs(
            projectId,
            workPackageOutputUpdateDTO.toWorkPackageOutputUpdateSet(),
            id
        ).toWorkPackageOutputDTOSet()


}

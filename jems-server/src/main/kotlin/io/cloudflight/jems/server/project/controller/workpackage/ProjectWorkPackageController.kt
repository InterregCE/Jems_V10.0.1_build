package io.cloudflight.jems.server.project.controller.workpackage

import io.cloudflight.jems.api.project.workpackage.ProjectWorkPackageApi
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageCreate
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageUpdate
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackageSimple
import io.cloudflight.jems.api.project.dto.workpackage.ProjectWorkPackageDTO
import io.cloudflight.jems.server.project.service.workpackage.WorkPackageService
import io.cloudflight.jems.server.project.service.workpackage.get_workpackage.GetWorkPackageInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectWorkPackageController(
    private val workPackageService: WorkPackageService,
    private val getWorkPackage: GetWorkPackageInteractor,
) : ProjectWorkPackageApi {

    override fun getWorkPackageById(projectId: Long, workPackageId: Long, version: String?): OutputWorkPackage =
        getWorkPackage.getWorkPackageById(projectId, workPackageId, version)

    override fun getWorkPackagesByProjectId(projectId: Long, version: String?): List<OutputWorkPackageSimple> =
        getWorkPackage.getWorkPackagesByProjectId(projectId, version)

    override fun getWorkPackagesForTimePlanByProjectId(projectId: Long): List<ProjectWorkPackageDTO> =
        getWorkPackage.getWorkPackagesForTimePlanByProjectId(projectId).toDto()

    override fun createWorkPackage(projectId: Long, inputWorkPackageCreate: InputWorkPackageCreate): OutputWorkPackage =
        workPackageService.createWorkPackage(projectId, inputWorkPackageCreate)

    override fun updateWorkPackage(projectId: Long, inputWorkPackageUpdate: InputWorkPackageUpdate): OutputWorkPackage =
        workPackageService.updateWorkPackage(projectId, inputWorkPackageUpdate)

    override fun deleteWorkPackage(projectId: Long, workPackageId: Long) {
        return workPackageService.deleteWorkPackage(projectId, workPackageId)
    }

}

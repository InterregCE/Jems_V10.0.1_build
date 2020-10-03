package io.cloudflight.jems.server.workpackage.controller

import io.cloudflight.jems.api.workpackage.WorkPackageApi
import io.cloudflight.jems.api.workpackage.dto.InputWorkPackageCreate
import io.cloudflight.jems.api.workpackage.dto.InputWorkPackageUpdate
import io.cloudflight.jems.api.workpackage.dto.OutputWorkPackage
import io.cloudflight.jems.api.workpackage.dto.OutputWorkPackageSimple
import io.cloudflight.jems.server.workpackage.service.WorkPackageService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class WorkPackageController(
    private val workPackageService: WorkPackageService
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

}

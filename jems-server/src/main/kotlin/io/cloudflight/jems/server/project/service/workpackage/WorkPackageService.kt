package io.cloudflight.jems.server.project.service.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageCreate
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageUpdate
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackageSimple
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface WorkPackageService {

    fun getWorkPackageById(workPackageId: Long): OutputWorkPackage

    fun getProjectForWorkPackageId(id: Long): ProjectApplicantAndStatus

    fun getWorkPackagesByProjectId(projectId: Long, pageable: Pageable): Page<OutputWorkPackageSimple>

    fun createWorkPackage(projectId: Long, inputWorkPackageCreate: InputWorkPackageCreate): OutputWorkPackage

    fun updateWorkPackage(inputWorkPackageUpdate: InputWorkPackageUpdate): OutputWorkPackage

    fun deleteWorkPackage(workPackageId: Long)

}

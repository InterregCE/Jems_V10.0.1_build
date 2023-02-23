package io.cloudflight.jems.server.project.service.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageCreate
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageUpdate
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackageSimple
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus

interface WorkPackageService {
    fun getProjectForWorkPackageId(id: Long): ProjectApplicantAndStatus

    fun createWorkPackage(projectId: Long, inputWorkPackageCreate: InputWorkPackageCreate): OutputWorkPackage

    fun updateWorkPackage(projectId: Long, inputWorkPackageUpdate: InputWorkPackageUpdate): OutputWorkPackage

    fun deleteWorkPackage(projectId: Long, workPackageId: Long)

    fun deactivateWorkPackage(projectId: Long, workPackageId: Long): OutputWorkPackageSimple

}

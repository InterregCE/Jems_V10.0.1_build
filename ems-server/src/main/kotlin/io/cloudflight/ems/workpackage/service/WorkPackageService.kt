package io.cloudflight.ems.workpackage.service

import io.cloudflight.ems.api.workpackage.dto.InputWorkPackageCreate
import io.cloudflight.ems.api.workpackage.dto.InputWorkPackageUpdate
import io.cloudflight.ems.api.workpackage.dto.OutputWorkPackage
import io.cloudflight.ems.api.workpackage.dto.OutputWorkPackageSimple
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface WorkPackageService {

    fun getWorkPackageById(id: Long): OutputWorkPackage

    fun getWorkPackagesByProjectId(projectId: Long, pageable: Pageable): Page<OutputWorkPackageSimple>

    fun createWorkPackage(projectId: Long, inputWorkPackageCreate: InputWorkPackageCreate): OutputWorkPackage

    fun updateWorkPackage(projectId: Long, inputWorkPackageUpdate: InputWorkPackageUpdate): OutputWorkPackage
}

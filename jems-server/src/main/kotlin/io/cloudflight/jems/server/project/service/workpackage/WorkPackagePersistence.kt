package io.cloudflight.jems.server.project.service.workpackage

import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutputUpdate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface WorkPackagePersistence {

    fun updateWorkPackageOutputs(projectId: Long, workPackageOutputs: Set<WorkPackageOutputUpdate>, workPackageId: Long): Set<WorkPackageOutput>
    fun getWorkPackageOutputsForWorkPackage(workPackageId: Long): Set<WorkPackageOutput>
    fun getWorkPackageInvestment(workPackageInvestmentId: UUID): WorkPackageInvestment
    fun getWorkPackageInvestments(workPackageId: Long, pageable: Pageable): Page<WorkPackageInvestment>
    fun addWorkPackageInvestment(workPackageId: Long, workPackageInvestment: WorkPackageInvestment): UUID
    fun updateWorkPackageInvestment(workPackageInvestment: WorkPackageInvestment)
    fun deleteWorkPackageInvestment(workPackageInvestmentId: UUID)
}

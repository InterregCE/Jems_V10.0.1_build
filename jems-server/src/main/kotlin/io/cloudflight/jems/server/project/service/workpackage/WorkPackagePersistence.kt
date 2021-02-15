package io.cloudflight.jems.server.project.service.workpackage

import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.model.InvestmentSummary
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface WorkPackagePersistence {

    fun getRichWorkPackagesByProjectId(projectId: Long, pageable: Pageable): Page<ProjectWorkPackage>

    fun updateWorkPackageOutputs(workPackageId: Long, workPackageOutputs: List<WorkPackageOutput>): List<WorkPackageOutput>
    fun getWorkPackageOutputsForWorkPackage(workPackageId: Long): List<WorkPackageOutput>

    fun getWorkPackageInvestment(workPackageInvestmentId: Long): WorkPackageInvestment
    fun getWorkPackageInvestments(workPackageId: Long, pageable: Pageable): Page<WorkPackageInvestment>
    fun addWorkPackageInvestment(workPackageId: Long, workPackageInvestment: WorkPackageInvestment): Long
    fun updateWorkPackageInvestment(workPackageId: Long, workPackageInvestment: WorkPackageInvestment)
    fun deleteWorkPackageInvestment(workPackageId: Long, workPackageInvestmentId: Long)
    fun getProjectInvestmentSummaries(projectId: Long): List<InvestmentSummary>
    fun countWorkPackageInvestments(workPackageId: Long): Long

    fun getWorkPackageActivitiesForWorkPackage(workPackageId: Long): List<WorkPackageActivity>
    fun updateWorkPackageActivities(workPackageId: Long, workPackageActivities: List<WorkPackageActivity>): List<WorkPackageActivity>
    fun getProjectIdFromWorkPackageInvestment(workPackageInvestmentId: Long): Long
}

package io.cloudflight.jems.server.project.service.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackageSimple
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.model.InvestmentSummary
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackageFull
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface WorkPackagePersistence {

    fun getWorkPackagesWithOutputsAndActivitiesByProjectId(projectId: Long): List<ProjectWorkPackage>
    fun getWorkPackagesWithAllDataByProjectId(projectId: Long): List<ProjectWorkPackageFull>
    fun getWorkPackagesByProjectId(projectId: Long, version: String?): List<OutputWorkPackageSimple>
    fun getWorkPackageById(workPackageId: Long, version: String?): OutputWorkPackage

    fun updateWorkPackageOutputs(workPackageId: Long, workPackageOutputs: List<WorkPackageOutput>): List<WorkPackageOutput>
    fun getWorkPackageOutputsForWorkPackage(workPackageId: Long, version: String? = null): List<WorkPackageOutput>

    fun getWorkPackageInvestment(workPackageInvestmentId: Long): WorkPackageInvestment
    fun getWorkPackageInvestments(workPackageId: Long, pageable: Pageable): Page<WorkPackageInvestment>
    fun addWorkPackageInvestment(workPackageId: Long, workPackageInvestment: WorkPackageInvestment): Long
    fun updateWorkPackageInvestment(workPackageId: Long, workPackageInvestment: WorkPackageInvestment)
    fun deleteWorkPackageInvestment(workPackageId: Long, workPackageInvestmentId: Long)
    fun getProjectInvestmentSummaries(projectId: Long): List<InvestmentSummary>
    fun countWorkPackageInvestments(workPackageId: Long): Long

    fun getWorkPackageActivitiesForWorkPackage(workPackageId: Long, version: String? = null): List<WorkPackageActivity>
    fun updateWorkPackageActivities(workPackageId: Long, workPackageActivities: List<WorkPackageActivity>): List<WorkPackageActivity>
    fun getProjectFromWorkPackageInvestment(workPackageInvestmentId: Long): ProjectApplicantAndStatus
}

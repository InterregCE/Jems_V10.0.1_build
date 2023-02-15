package io.cloudflight.jems.server.project.service.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackageSimple
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.result.model.OutputRow
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivitySummary
import io.cloudflight.jems.server.project.service.workpackage.model.InvestmentSummary
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackageFull
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput

interface WorkPackagePersistence {

    fun getWorkPackagesWithOutputsAndActivitiesByProjectId(projectId: Long, version: String?): List<ProjectWorkPackage>
    fun getWorkPackagesWithAllDataByProjectId(projectId: Long, version: String? = null): List<ProjectWorkPackageFull>
    fun getWorkPackagesByProjectId(projectId: Long, version: String?): List<OutputWorkPackageSimple>
    fun getWorkPackageById(workPackageId: Long, projectId: Long, version: String?): OutputWorkPackage

    fun updateWorkPackageOutputs(workPackageId: Long, workPackageOutputs: List<WorkPackageOutput>): List<WorkPackageOutput>
    fun updateWorkPackageOutputsAfterContracted(workPackageId: Long, workPackageOutputs: List<WorkPackageOutput>): List<WorkPackageOutput>
    fun getWorkPackageOutputsForWorkPackage(workPackageId: Long, projectId: Long, version: String? = null): List<WorkPackageOutput>

    fun throwIfInvestmentNotExistsInProject(projectId: Long, investmentId: Long)
    fun getWorkPackageInvestment(workPackageInvestmentId: Long, projectId: Long, version: String? = null): WorkPackageInvestment
    fun getWorkPackageInvestments(workPackageId: Long, projectId: Long, version: String? = null): List<WorkPackageInvestment>
    fun addWorkPackageInvestment(workPackageId: Long, workPackageInvestment: WorkPackageInvestment): Long
    fun updateWorkPackageInvestment(workPackageId: Long, workPackageInvestment: WorkPackageInvestment)
    fun deleteWorkPackageInvestment(workPackageId: Long, workPackageInvestmentId: Long)
    fun deactivateWorkPackageInvestment(workPackageId: Long, workPackageInvestmentId: Long)
    fun getProjectInvestmentSummaries(projectId: Long, version: String? = null): List<InvestmentSummary>
    fun countWorkPackageInvestments(workPackageId: Long): Long

    fun getWorkPackageActivitiesForWorkPackage(workPackageId: Long, projectId: Long, version: String? = null): List<WorkPackageActivity>
    fun updateWorkPackageActivities(workPackageId: Long, workPackageActivities: List<WorkPackageActivity>): List<WorkPackageActivity>
    fun getWorkPackageActivitiesForProject(projectId: Long, version: String? = null): List<WorkPackageActivitySummary>

    fun getProjectFromWorkPackageInvestment(workPackageInvestmentId: Long): ProjectApplicantAndStatus

    fun getAllOutputsForProjectIdSortedByNumbers(projectId: Long, version: String? = null): List<OutputRow>
}

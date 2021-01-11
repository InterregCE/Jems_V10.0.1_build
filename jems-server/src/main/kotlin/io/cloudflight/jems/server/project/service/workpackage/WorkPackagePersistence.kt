package io.cloudflight.jems.server.project.service.workpackage

import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutputUpdate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface WorkPackagePersistence {

    fun updateWorkPackageOutputs(workPackageId: Long, workPackageOutputs: Set<WorkPackageOutputUpdate>): Set<WorkPackageOutput>
    fun getWorkPackageOutputsForWorkPackage(workPackageId: Long): Set<WorkPackageOutput>
    fun getWorkPackageInvestment(workPackageInvestmentId: Long): WorkPackageInvestment
    fun getWorkPackageInvestments(workPackageId: Long, pageable: Pageable): Page<WorkPackageInvestment>
    fun getWorkPackageInvestmentIdsOfProject(projectId: Long): List<Long>
    fun addWorkPackageInvestment(workPackageId: Long, workPackageInvestment: WorkPackageInvestment): Long
    fun updateWorkPackageInvestment(workPackageId: Long, workPackageInvestment: WorkPackageInvestment)
    fun deleteWorkPackageInvestment(workPackageId: Long, workPackageInvestmentId: Long)

    fun getWorkPackageActivitiesForWorkPackage(workPackageId: Long): List<WorkPackageActivity>
    fun updateWorkPackageActivities(workPackageId: Long, workPackageActivities: List<WorkPackageActivity>): List<WorkPackageActivity>

    fun getProjectIdFromWorkPackageInvestment(workPackageInvestmentId: Long): Long
}

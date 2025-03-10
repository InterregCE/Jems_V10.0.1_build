package io.cloudflight.jems.server.project.service.report.project.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageOnlyUpdate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanStatus
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputLineOverview
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanInvestmentStatus
import java.math.BigDecimal

interface ProjectReportWorkPlanPersistence {

    fun getReportWorkPlanById(projectId: Long, reportId: Long): List<ProjectReportWorkPackage>

    fun getReportWorkPackageOutputsById(projectId: Long, reportId: Long): List<ProjectReportOutputLineOverview>

    fun existsByActivityId(projectId: Long, reportId: Long, workPackageId: Long, activityId: Long): Boolean

    fun existsByDeliverableId(projectId: Long, reportId: Long, workPackageId: Long, activityId: Long, deliverableId: Long): Boolean

    fun existsByOutputId(projectId: Long, reportId: Long, workPackageId: Long, outputId: Long): Boolean

    fun updateReportWorkPackage(workPackageId: Long, data: ProjectReportWorkPackageOnlyUpdate)

    fun updateReportWorkPackageActivity(activityId: Long, status: ProjectReportWorkPlanStatus?, progress: Set<InputTranslation>)

    fun updateReportWorkPackageDeliverable(deliverableId: Long, currentReport: BigDecimal, progress: Set<InputTranslation>)

    fun updateReportWorkPackageOutput(outputId: Long, currentReport: BigDecimal, progress: Set<InputTranslation>)

    fun updateReportWorkPackageInvestment(investmentId: Long, progress: Set<InputTranslation>, status: ProjectReportWorkPlanInvestmentStatus?)

    fun getDeliverableCumulative(reportIds: Set<Long>): Map<Int, Map<Int, Map<Int, BigDecimal>>>

    fun getOutputCumulative(reportIds: Set<Long>): Map<Int, Map<Int, BigDecimal>>

    fun deleteWorkPlan(reportId: Long)
}

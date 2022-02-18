package io.cloudflight.jems.server.project.service.report

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime

interface ProjectReportPersistence {

    fun createPartnerReport(report: ProjectPartnerReportCreate): ProjectPartnerReportSummary

    fun submitReportById(partnerId: Long, reportId: Long, submissionTime: ZonedDateTime): ProjectPartnerReportSubmissionSummary

    fun getPartnerReportStatusById(partnerId: Long, reportId: Long): ReportStatus

    fun getPartnerReportById(partnerId: Long, reportId: Long): ProjectPartnerReport

    fun listPartnerReports(partnerId: Long, pageable: Pageable): Page<ProjectPartnerReportSummary>

    fun getCurrentLatestReportNumberForPartner(partnerId: Long): Int

    fun getPartnerReportWorkPlanById(partnerId: Long, reportId: Long): List<ProjectPartnerReportWorkPackage>

    fun updatePartnerReportWorkPackage(workPackageId: Long, translations: Set<InputTranslation>)

    fun updatePartnerReportWorkPackageActivity(activityId: Long, translations: Set<InputTranslation>)

    fun updatePartnerReportWorkPackageDeliverable(deliverableId: Long, contribution: Boolean?, evidence: Boolean?)

    fun updatePartnerReportWorkPackageOutput(outputId: Long, contribution: Boolean?, evidence: Boolean?)

}

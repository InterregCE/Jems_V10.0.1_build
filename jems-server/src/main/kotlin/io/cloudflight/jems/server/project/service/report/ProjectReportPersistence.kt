package io.cloudflight.jems.server.project.service.report

import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.create.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime

interface ProjectReportPersistence {

    fun submitReportById(partnerId: Long, reportId: Long, submissionTime: ZonedDateTime): ProjectPartnerReportSubmissionSummary

    fun getPartnerReportStatusAndVersion(partnerId: Long, reportId: Long): ProjectPartnerReportStatusAndVersion

    fun getPartnerReportById(partnerId: Long, reportId: Long): ProjectPartnerReport

    fun listPartnerReports(partnerId: Long, pageable: Pageable): Page<ProjectPartnerReportSummary>

    fun listSubmittedPartnerReports(partnerId: Long): List<ProjectPartnerReportSummary>

    fun getReportIdsBefore(partnerId: Long, beforeReportId: Long): Set<Long>

    fun exists(partnerId: Long, reportId: Long): Boolean

    fun getCurrentLatestReportNumberForPartner(partnerId: Long): Int

    fun countForPartner(partnerId: Long): Int

}

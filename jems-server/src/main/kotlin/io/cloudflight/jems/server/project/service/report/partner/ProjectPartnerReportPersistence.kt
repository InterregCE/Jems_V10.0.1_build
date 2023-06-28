package io.cloudflight.jems.server.project.service.report.partner

import io.cloudflight.jems.plugin.contract.models.report.partner.identification.ProjectPartnerReportBaseData
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.certificate.PartnerReportCertificate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime

interface ProjectPartnerReportPersistence {

    fun updateStatusAndTimes(
        partnerId: Long,
        reportId: Long,
        status: ReportStatus,
        firstSubmissionTime: ZonedDateTime? = null,
        lastReSubmissionTime: ZonedDateTime? = null,
        lastControlReopening: ZonedDateTime? = null,
    ): ProjectPartnerReportSubmissionSummary

    fun finalizeControlOnReportById(
        partnerId: Long,
        reportId: Long,
        controlEnd: ZonedDateTime,
    ): ProjectPartnerReportSubmissionSummary

    fun getPartnerReportStatusAndVersion(partnerId: Long, reportId: Long): ProjectPartnerReportStatusAndVersion

    fun getPartnerReportById(partnerId: Long, reportId: Long): ProjectPartnerReport

    fun listPartnerReports(partnerId: Long, pageable: Pageable): Page<ProjectPartnerReportSummary>

    fun getAllPartnerReportsBaseDataByProjectId(projectId: Long): Sequence<ProjectPartnerReportBaseData>

    fun listCertificates(partnerIds: Set<Long>, pageable: Pageable): Page<PartnerReportCertificate>

    fun getSubmittedPartnerReports(partnerId: Long): List<ProjectPartnerReportStatusAndVersion>

    fun getReportIdsBefore(partnerId: Long, beforeReportId: Long): Set<Long>

    fun getLastCertifiedPartnerReportId(partnerId: Long): Long?

    fun exists(partnerId: Long, reportId: Long): Boolean

    fun existsByStatusIn(partnerId: Long, statuses: Set<ReportStatus>): Boolean

    fun getCurrentLatestReportForPartner(partnerId: Long): ProjectPartnerReport?

    fun countForPartner(partnerId: Long): Int

    fun isAnyReportCreated(): Boolean

    fun deletePartnerReportById(reportId: Long)

    fun getReportStatusById(reportId: Long): ReportStatus
}

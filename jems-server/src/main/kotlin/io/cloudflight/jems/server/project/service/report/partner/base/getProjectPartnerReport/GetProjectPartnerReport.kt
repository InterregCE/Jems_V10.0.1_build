package io.cloudflight.jems.server.project.service.report.partner.base.getProjectPartnerReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.base.removeEligibleAfterControlFromNotInControlOnes
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerReport(
    private val reportPersistence: ProjectPartnerReportPersistence,
) : GetProjectPartnerReportInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerReportException::class)
    override fun findById(partnerId: Long, reportId: Long): ProjectPartnerReport =
        reportPersistence.getPartnerReportById(partnerId, reportId = reportId)

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerReportListException::class)
    override fun findAll(partnerId: Long, pageable: Pageable): Page<ProjectPartnerReportSummary> {
        val latestReportId = reportPersistence.getCurrentLatestReportForPartner(partnerId)?.id

        return reportPersistence.listPartnerReports(setOf(partnerId), ReportStatus.values().toSet(), pageable)
            .fillInDeletableFor(latestReportId)
            .removeEligibleAfterControlFromNotInControlOnes()
    }

    private fun Page<ProjectPartnerReportSummary>.fillInDeletableFor(latestReportId: Long?) =
        this.onEach { it.deletable = it.id == latestReportId && it.status.isOpenInitially() }

}

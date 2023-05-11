package io.cloudflight.jems.server.project.service.report.partner.base.getProjectPartnerReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSummary
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

        return reportPersistence.listPartnerReports(partnerId, pageable)
            .fillInDeletableFor(latestReportId)
            .removeEligibleAfterControlFromNotInControlOnes()
    }

    private fun Page<ProjectPartnerReportSummary>.fillInDeletableFor(latestReportId: Long?) =
        this.onEach { it.deletable = it.id == latestReportId && it.status.isOpenInitially() }

    private fun Page<ProjectPartnerReportSummary>.removeEligibleAfterControlFromNotInControlOnes() =
        this.onEach {
            if (it.status.controlNotStartedYet()) {
                it.totalEligibleAfterControl = null
            }
            if (it.status.isOpenForNumbersChanges()) {
                it.totalAfterSubmitted = null
            }
        }

}

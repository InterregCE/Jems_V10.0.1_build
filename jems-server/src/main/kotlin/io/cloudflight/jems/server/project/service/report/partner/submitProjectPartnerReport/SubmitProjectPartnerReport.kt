package io.cloudflight.jems.server.project.service.report.partner.submitProjectPartnerReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class SubmitProjectPartnerReport(
    private val reportPersistence: ProjectReportPersistence,
) : SubmitProjectPartnerReportInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(SubmitProjectPartnerReportException::class)
    override fun submit(partnerId: Long, reportId: Long): ProjectPartnerReportSummary {
        validateReportIsStillDraft(partnerId = partnerId, reportId = reportId)

        return reportPersistence.submitReportById(
            partnerId = partnerId,
            reportId = reportId,
            submissionTime = ZonedDateTime.now()
        )
    }

    private fun validateReportIsStillDraft(partnerId: Long, reportId: Long) {
        val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId)
        if (report.status.isClosed())
            throw ReportAlreadyClosed()
    }

}

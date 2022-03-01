package io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectReportIdentificationPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerReportIdentification(
    private val reportIdentificationPersistence: ProjectReportIdentificationPersistence,
) : GetProjectPartnerReportIdentificationInteractor {

    companion object {
        private val emptyIdentification = ProjectPartnerReportIdentification(
            startDate = null,
            endDate = null,
            period = null,
            summary = emptySet(),
            problemsAndDeviations = emptySet(),
            targetGroups = emptyList(),
        )
    }

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerReportIdentificationException::class)
    override fun getIdentification(partnerId: Long, reportId: Long): ProjectPartnerReportIdentification =
        reportIdentificationPersistence.getPartnerReportIdentification(partnerId = partnerId, reportId = reportId)
            .orElse(emptyIdentification)
}

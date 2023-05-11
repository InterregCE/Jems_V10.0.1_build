package io.cloudflight.jems.server.project.service.report.partner.base.canCreateProjectPartnerReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReportNotSpecific
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CanCreateProjectPartnerReport(
    private val reportPersistence: ProjectPartnerReportPersistence,
) : CanCreateProjectPartnerReportInteractor {

    @CanEditPartnerReportNotSpecific
    @Transactional
    @ExceptionWrapper(CanCreateProjectPartnerReportException::class)
    override fun canCreateReportFor(partnerId: Long): Boolean =
        !reportPersistence.existsByStatusIn(partnerId, ReportStatus.ARE_LAST_OPEN_STATUSES)

}

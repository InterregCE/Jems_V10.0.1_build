package io.cloudflight.jems.server.project.service.report.partner.control.overview.updateReportControlOverview

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReport
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlOverview
import io.cloudflight.jems.server.project.service.report.partner.control.overview.ProjectPartnerReportControlOverviewPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateReportControlOverview(
    private val projectPartnerReportControlOverviewPersistence: ProjectPartnerReportControlOverviewPersistence
): UpdateReportControlOverviewInteractor {

    @CanEditPartnerControlReport
    @Transactional
    @ExceptionWrapper(UpdateReportControlOverviewException::class)
    override fun update(partnerId: Long, reportId: Long, controlOverview: ControlOverview): ControlOverview {
        return projectPartnerReportControlOverviewPersistence.updatePartnerControlReportOverview(
            partnerId,
            reportId,
            controlOverview
        )
    }
}

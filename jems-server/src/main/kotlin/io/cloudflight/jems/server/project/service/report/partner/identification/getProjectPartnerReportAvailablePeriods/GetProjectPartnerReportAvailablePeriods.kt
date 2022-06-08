package io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportAvailablePeriods

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectReportIdentificationPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerReportAvailablePeriods(
    private val reportIdentificationPersistence: ProjectReportIdentificationPersistence,
) : GetProjectPartnerReportAvailablePeriodsInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerReportAvailablePeriodsException::class)
    override fun get(partnerId: Long, reportId: Long): List<ProjectPartnerReportPeriod> =
        reportIdentificationPersistence.getAvailablePeriods(partnerId = partnerId, reportId = reportId)
            .filterOutPreparationAndClosure()

}

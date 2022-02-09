package io.cloudflight.jems.server.project.service.report.partner.getProjectPartnerReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerReport(
    private val reportPersistence: ProjectReportPersistence,
) : GetProjectPartnerReportInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerReportException::class)
    override fun findById(partnerId: Long, reportId: Long): ProjectPartnerReport =
        reportPersistence.getPartnerReportById(partnerId, reportId = reportId)

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerReportListException::class)
    override fun findAll(partnerId: Long, pageable: Pageable): Page<ProjectPartnerReportSummary> =
        reportPersistence.listPartnerReports(partnerId, pageable)

}

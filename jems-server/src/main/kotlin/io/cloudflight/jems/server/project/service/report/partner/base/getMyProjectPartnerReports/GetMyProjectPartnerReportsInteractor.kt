package io.cloudflight.jems.server.project.service.report.partner.base.getMyProjectPartnerReports

import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetMyProjectPartnerReportsInteractor {

    fun findAllOfMine(pageable: Pageable): Page<ProjectPartnerReportSummary>
}

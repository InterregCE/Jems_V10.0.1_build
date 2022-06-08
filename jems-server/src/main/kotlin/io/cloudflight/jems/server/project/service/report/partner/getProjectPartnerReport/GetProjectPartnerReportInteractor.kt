package io.cloudflight.jems.server.project.service.report.partner.getProjectPartnerReport

import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetProjectPartnerReportInteractor {

    fun findById(partnerId: Long, reportId: Long): ProjectPartnerReport

    fun findAll(partnerId: Long, pageable: Pageable): Page<ProjectPartnerReportSummary>

}

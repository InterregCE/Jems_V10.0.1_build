package io.cloudflight.jems.server.project.service.report.partner.base.getProjectPartnerReport

import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetProjectPartnerReportInteractor {

    fun findById(partnerId: Long, reportId: Long): ProjectPartnerReport

    fun findAll(partnerId: Long, pageable: Pageable): Page<ProjectPartnerReportSummary>

}

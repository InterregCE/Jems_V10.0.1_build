package io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableParkedExpenditureList

import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportParkedExpenditure
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetAvailableParkedExpenditureListInteractor {

    fun getParked(partnerId: Long, reportId: Long, pageable: Pageable): Page<ProjectPartnerReportParkedExpenditure>

}

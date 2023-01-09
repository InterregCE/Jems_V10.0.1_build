package io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableParkedExpenditureList

import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetAvailableParkedExpenditureListInteractor {

    fun getParked(partnerId: Long, pageable: Pageable): Page<ProjectPartnerReportExpenditureCost>

}

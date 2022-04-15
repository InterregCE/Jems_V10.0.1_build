package io.cloudflight.jems.server.project.service.report.partner.expenditure

import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost

interface ProjectReportExpenditurePersistence {

    fun getPartnerReportExpenditureCosts(partnerId: Long, reportId: Long): List<ProjectPartnerReportExpenditureCost>

    fun updatePartnerReportExpenditureCosts(
        partnerId: Long,
        reportId: Long,
        expenditureCosts: List<ProjectPartnerReportExpenditureCost>,
    )

    fun existsByExpenditureId(partnerId: Long, reportId: Long, expenditureId: Long): Boolean

}

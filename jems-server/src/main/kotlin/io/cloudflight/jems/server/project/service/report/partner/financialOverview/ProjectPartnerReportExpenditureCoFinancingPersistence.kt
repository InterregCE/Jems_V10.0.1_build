package io.cloudflight.jems.server.project.service.report.partner.financialOverview

import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingCurrentWithReIncluded
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancing
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import java.math.BigDecimal

interface ProjectPartnerReportExpenditureCoFinancingPersistence {

    fun getCoFinancing(partnerId: Long, reportId: Long): ReportExpenditureCoFinancing

    fun getCoFinancingCumulative(reportIds: Set<Long>): ExpenditureCoFinancingCurrent

    fun updateCurrentlyReportedValues(partnerId: Long, reportId: Long, currentlyReported: ExpenditureCoFinancingCurrentWithReIncluded)

    fun updateAfterControlValues(partnerId: Long, reportId: Long, afterControl: ExpenditureCoFinancingCurrent)

    fun getReportCurrentSum(partnerId: Long, reportId: Long): BigDecimal

}

package io.cloudflight.jems.server.project.service.report.partner.financialOverview

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingCurrentWithReIncluded
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingPrevious
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancing
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import java.math.BigDecimal

interface ProjectPartnerReportExpenditureCoFinancingPersistence {

    fun getCoFinancing(partnerId: Long, reportId: Long): ReportExpenditureCoFinancing

    fun getCoFinancingCumulative(submittedReportIds: Set<Long>, finalizedReportIds: Set<Long>): ExpenditureCoFinancingPrevious

    fun updateCurrentlyReportedValues(partnerId: Long, reportId: Long, currentlyReported: ExpenditureCoFinancingCurrentWithReIncluded)

    fun updateAfterControlValues(partnerId: Long, reportId: Long, afterControl: ExpenditureCoFinancingCurrent)

    fun getCoFinancingTotalEligible(reportIds: Set<Long>): ReportCertificateCoFinancingColumn

    fun getTotalsForProjectReports(projectReportIds: Set<Long>): Map<Long, BigDecimal>

    fun getAvailableFunds(reportId: Long): List<ProgrammeFund>

}

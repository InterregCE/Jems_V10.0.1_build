package io.cloudflight.jems.server.project.service.report.partner.expenditure.reincludeParkedExpenditure

interface ReIncludeParkedExpenditureInteractor {

    fun reIncludeParkedExpenditure(partnerId: Long, reportId: Long, expenditureId: Long)

}

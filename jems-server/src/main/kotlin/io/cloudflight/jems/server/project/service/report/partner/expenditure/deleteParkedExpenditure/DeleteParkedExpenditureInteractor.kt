package io.cloudflight.jems.server.project.service.report.partner.expenditure.deleteParkedExpenditure

interface DeleteParkedExpenditureInteractor {

    fun deleteParkedExpenditure(partnerId: Long, reportId: Long, expenditureId: Long)

}

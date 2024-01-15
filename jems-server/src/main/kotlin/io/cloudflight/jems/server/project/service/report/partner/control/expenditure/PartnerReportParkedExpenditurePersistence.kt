package io.cloudflight.jems.server.project.service.report.partner.control.expenditure

import io.cloudflight.jems.server.project.service.report.model.partner.control.expenditure.ParkExpenditureData
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata

interface PartnerReportParkedExpenditurePersistence {

    fun getParkedExpendituresByIdForPartnerReport(partnerId: Long, reportId: Long): Map<Long, ExpenditureParkingMetadata>

    fun getParkedExpenditureById(expenditureId: Long): ExpenditureParkingMetadata

    fun parkExpenditures(toPark: Collection<ParkExpenditureData>)

    fun unParkExpenditures(expenditureIds: Collection<Long>)

    fun getParkedExpenditureIds(reportId: Long): Set<Long>
}

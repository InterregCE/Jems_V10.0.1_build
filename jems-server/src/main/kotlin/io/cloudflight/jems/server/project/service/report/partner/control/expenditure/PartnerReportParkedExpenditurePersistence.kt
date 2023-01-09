package io.cloudflight.jems.server.project.service.report.partner.control.expenditure

import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.control.expenditure.ParkExpenditureData
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata

interface PartnerReportParkedExpenditurePersistence {

    fun getParkedExpendituresByIdForPartner(partnerId: Long, onlyAllowedStatusOfOrigin: ReportStatus): Map<Long, ExpenditureParkingMetadata>

    fun parkExpenditures(toPark: Collection<ParkExpenditureData>)

    fun unParkExpenditures(expenditureIds: Collection<Long>)

}

package io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableParkedExpenditureList

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.PartnerReportParkedExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetAvailableParkedExpenditureList(
    private val reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence,
    private val reportParkedExpenditurePersistence: PartnerReportParkedExpenditurePersistence,
) : GetAvailableParkedExpenditureListInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetAvailableParkedExpenditureListException::class)
    override fun getParked(partnerId: Long, pageable: Pageable): Page<ProjectPartnerReportExpenditureCost> {
        val parkedExpendituresById = reportParkedExpenditurePersistence
            .getParkedExpendituresByIdForPartner(partnerId, ReportStatus.Certified)

        return reportExpenditurePersistence.getPartnerReportExpenditureCosts(
            ids = parkedExpendituresById.keys,
            pageable = pageable,
        ).fillInParkingData(parkedExpendituresById)
    }

    private fun Page<ProjectPartnerReportExpenditureCost>.fillInParkingData(
        parkingById: Map<Long, ExpenditureParkingMetadata>,
    ) = this.onEach { expenditure ->
        expenditure.parkingMetadata = parkingById[expenditure.id]!!
    }

}

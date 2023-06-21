package io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableParkedExpenditureList

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportInvestment
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportParkedExpenditure
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportUnitCost
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
    override fun getParked(partnerId: Long, reportId: Long, pageable: Pageable): Page<ProjectPartnerReportParkedExpenditure> {
        val parkedExpendituresById = reportParkedExpenditurePersistence
            .getParkedExpendituresByIdForPartner(partnerId, ReportStatus.Certified)

        val availableLumpSums = reportExpenditurePersistence.getAvailableLumpSums(partnerId, reportId = reportId)
            .associateBy { Pair(it.lumpSumProgrammeId, it.orderNr) }
        val availableUnitCosts = reportExpenditurePersistence.getAvailableUnitCosts(partnerId, reportId = reportId)
            .associateBy { it.unitCostProgrammeId }
        val availableInvestments = reportExpenditurePersistence.getAvailableInvestments(partnerId, reportId = reportId)
            .associateBy { it.investmentId }

        val result = reportExpenditurePersistence.getPartnerReportExpenditureCosts(
            ids = parkedExpendituresById.keys,
            pageable = pageable,
        )

        return result
            .fillInParkingData(parkedExpendituresById)
            .fillInUnitCostAvailable(availableUnitCosts)
            .fillInLumpSumAvailable(availableLumpSums)
            .fillInInvestmentAvailable(availableInvestments)
    }

    private fun Page<ProjectPartnerReportParkedExpenditure>.fillInParkingData(
        parkingById: Map<Long, ExpenditureParkingMetadata>,
    ) = this.onEach { parked ->
        parked.expenditure.parkingMetadata = parkingById[parked.expenditure.id]!!
    }

    private fun Page<ProjectPartnerReportParkedExpenditure>.fillInUnitCostAvailable(
        availableUnitCosts: Map<Long, ProjectPartnerReportUnitCost>,
    ) = this.onEach { parked ->
        if (parked.unitCost != null) {
            parked.unitCost.entityStillAvailable = parked.unitCost.projectRelatedId in availableUnitCosts.keys
        }
    }

    private fun Page<ProjectPartnerReportParkedExpenditure>.fillInLumpSumAvailable(
        availableLumpSums: Map<Pair<Long, Int>, ProjectPartnerReportLumpSum>,
    ) = this.onEach { parked ->
        if (parked.lumpSum != null) {
            // lump sums can be used multiple times in AF, so we need to consider also their order number
            parked.lumpSum.entityStillAvailable =
                Pair(parked.lumpSum.projectRelatedId, parked.lumpSum.lumpSumNumber!!) in availableLumpSums.keys
        }
    }

    private fun Page<ProjectPartnerReportParkedExpenditure>.fillInInvestmentAvailable(
        availableInvestments: Map<Long, ProjectPartnerReportInvestment>,
    ) = this.onEach { parked ->
        if (parked.investment != null) {
            parked.investment.entityStillAvailable = parked.investment.projectRelatedId in availableInvestments.keys
        }
    }

}

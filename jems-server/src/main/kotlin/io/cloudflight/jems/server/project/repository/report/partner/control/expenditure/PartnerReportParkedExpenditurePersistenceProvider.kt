package io.cloudflight.jems.server.project.repository.report.partner.control.expenditure

import io.cloudflight.jems.server.project.entity.report.control.expenditure.PartnerReportParkedExpenditureEntity
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.partner.expenditure.ProjectPartnerReportExpenditureRepository
import io.cloudflight.jems.server.project.repository.report.project.base.ProjectReportRepository
import io.cloudflight.jems.server.project.service.report.model.partner.control.expenditure.ParkExpenditureData
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.PartnerReportParkedExpenditurePersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class PartnerReportParkedExpenditurePersistenceProvider(
    private val reportRepository: ProjectPartnerReportRepository,
    private val reportExpenditureRepository: ProjectPartnerReportExpenditureRepository,
    private val reportParkedExpenditureRepository: PartnerReportParkedExpenditureRepository,
    private val projectReportRepository: ProjectReportRepository
) : PartnerReportParkedExpenditurePersistence {

    @Transactional(readOnly = true)
    override fun getParkedExpenditureIds(reportId: Long) =
        reportParkedExpenditureRepository.getAvailableParkedExpenditureIdsFromPartnerReport(reportId)

    @Transactional(readOnly = true)
    override fun getParkedExpendituresByIdForPartnerReport(
        partnerId: Long,
        reportId: Long
    ): Map<Long, ExpenditureParkingMetadata> =
        reportParkedExpenditureRepository
            .findAllAvailableForPartnerReport(partnerId = partnerId, reportId = reportId)
            .associate {
                Pair(
                    it.parkedFromExpenditureId,
                    ExpenditureParkingMetadata(
                        reportOfOriginId = it.reportOfOrigin.id,
                        reportOfOriginNumber = it.reportOfOrigin.number,
                        reportProjectOfOriginId = it.parkedInProjectReport?.id,
                        originalExpenditureNumber = it.originalNumber,
                        parkedOn = it.parkedOn,
                        parkedFromExpenditureId = it.parkedFromExpenditureId
                    )
                )
            }

    @Transactional(readOnly = true)
    override fun getParkedExpenditureById(expenditureId: Long): ExpenditureParkingMetadata
         = reportParkedExpenditureRepository.getReferenceById(expenditureId).toModel()

    @Transactional
    override fun parkExpenditures(toPark: Collection<ParkExpenditureData>) {
        val newlyParked = toPark.map {
            PartnerReportParkedExpenditureEntity(
                parkedFromExpenditureId = it.expenditureId,
                parkedFrom = reportExpenditureRepository.getReferenceById(it.expenditureId),
                reportOfOrigin = reportRepository.getReferenceById(it.originalReportId),
                parkedInProjectReport = it.parkedInProjectReportId?.let { id -> projectReportRepository.getReferenceById(id) },
                originalNumber = it.originalNumber,
                parkedOn = it.parkedOn
            )
        }
        reportParkedExpenditureRepository.saveAll(newlyParked)
    }

    @Transactional
    override fun unParkExpenditures(expenditureIds: Collection<Long>) {
        reportParkedExpenditureRepository.deleteAllById(expenditureIds)
    }

    @Transactional
    override fun findAllByProjectReportId(projectReportId: Long): List<ExpenditureParkingMetadata> {
        return reportParkedExpenditureRepository.findAllByParkedInProjectReportId(projectReportId).map { it.toModel() }
    }
}

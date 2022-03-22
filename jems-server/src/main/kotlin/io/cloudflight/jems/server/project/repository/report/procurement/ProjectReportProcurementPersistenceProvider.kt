package io.cloudflight.jems.server.project.repository.report.procurement

import io.cloudflight.jems.server.project.repository.report.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementUpdate
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectReportProcurementPersistenceProvider(
    private val reportRepository: ProjectPartnerReportRepository,
    private val reportProcurementRepository: ProjectPartnerReportProcurementRepository,
) : ProjectReportProcurementPersistence {

    @Transactional(readOnly = true)
    override fun getProcurementIdsForReport(partnerId: Long, reportId: Long) =
        reportProcurementRepository.findProcurementIdsForReport(partnerId = partnerId, reportId = reportId)

    @Transactional(readOnly = true)
    override fun getProcurementsForReportIds(reportIds: Set<Long>) =
        reportProcurementRepository.findTop50ByReportEntityIdInOrderByReportEntityIdDescIdDesc(reportIds = reportIds)
            .toModel()

    @Transactional(readOnly = true)
    override fun getProcurementContractIdsForReportIds(reportIds: Set<Long>) =
        reportProcurementRepository.findProcurementContractIdsForReportsIn(reportIds = reportIds)

    @Transactional(readOnly = true)
    override fun countProcurementsForReportIds(reportIds: Set<Long>) =
        reportProcurementRepository.countByReportEntityIdIn(reportIds = reportIds)

    @Transactional
    override fun updatePartnerReportProcurement(
        partnerId: Long,
        reportId: Long,
        procurementNew: List<ProjectPartnerReportProcurementUpdate>
    ) {
        val report = reportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId)
        val existingIds = reportProcurementRepository.findProcurementIdsForReport(partnerId = partnerId, reportId = reportId)
        val toStayIds = procurementNew.filter { it.id > 0 }.mapTo(HashSet()) { it.id }

        reportProcurementRepository.deleteAllById(existingIds.minus(toStayIds))
        reportProcurementRepository.saveAll(procurementNew.asReversed().toEntity(report))
    }
}

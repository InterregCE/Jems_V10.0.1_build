package io.cloudflight.jems.server.project.repository.report.procurement.subcontract

import io.cloudflight.jems.server.project.entity.report.procurement.subcontract.ProjectPartnerReportProcurementSubcontractEntity
import io.cloudflight.jems.server.project.repository.report.procurement.ProjectPartnerReportProcurementRepository
import io.cloudflight.jems.server.project.service.report.model.procurement.subcontract.ProjectPartnerReportProcurementSubcontract
import io.cloudflight.jems.server.project.service.report.model.procurement.subcontract.ProjectPartnerReportProcurementSubcontractChange
import io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract.ProjectReportProcurementSubcontractPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Repository
class ProjectReportProcurementSubcontractPersistenceProvider(
    private val reportProcurementRepository: ProjectPartnerReportProcurementRepository,
    private val reportProcurementSubcontractRepository: ProjectPartnerReportProcurementSubcontractRepository,
) : ProjectReportProcurementSubcontractPersistence {

    @Transactional(readOnly = true)
    override fun getSubcontractBeforeAndIncludingReportId(procurementId: Long, reportId: Long) =
        reportProcurementSubcontractRepository
            .findTop50ByProcurementIdAndCreatedInReportIdLessThanEqualOrderByCreatedInReportIdAscIdAsc(procurementId, reportId = reportId)
            .toModel()

    @Transactional
    override fun updateSubcontract(
        partnerId: Long,
        reportId: Long,
        procurementId: Long,
        data: List<ProjectPartnerReportProcurementSubcontractChange>
    ): List<ProjectPartnerReportProcurementSubcontract> {
        val procurement = reportProcurementRepository.findByReportEntityPartnerIdAndId(partnerId, procurementId)

        val existingById = reportProcurementSubcontractRepository
            .findTop50ByProcurementAndCreatedInReportIdOrderByCreatedInReportIdAscIdAsc(procurement, reportId).associateBy { it.id }

        val toStayIds = data.filter { it.id > 0 }.mapTo(HashSet()) { it.id }

        reportProcurementSubcontractRepository.deleteAll(existingById.minus(toStayIds).values)

        data.forEach { newData ->
            existingById[newData.id].let { existing ->
                when {
                    existing != null -> existing.updateWith(newData)
                    else -> reportProcurementSubcontractRepository.save(newData.toEntity(procurement, reportId))
                }
            }
        }

        return reportProcurementSubcontractRepository
            .findTop50ByProcurementIdAndCreatedInReportIdLessThanEqualOrderByCreatedInReportIdAscIdAsc(procurementId, reportId = reportId)
            .toModel()
            .also { procurement.lastChanged = ZonedDateTime.now() }
    }

    private fun ProjectPartnerReportProcurementSubcontractEntity.updateWith(newData: ProjectPartnerReportProcurementSubcontractChange) {
        contractName = newData.contractName
        referenceNumber = newData.referenceNumber
        contractDate = newData.contractDate
        contractAmount = newData.contractAmount
        currencyCode = newData.currencyCode
        supplierName = newData.supplierName
        vatNumber = newData.vatNumber
    }

    @Transactional(readOnly = true)
    override fun countSubcontractorsCreatedBefore(procurementId: Long, reportId: Long) =
        reportProcurementSubcontractRepository.countSubcontractorsCreatedBefore(procurementId, reportId = reportId)

}

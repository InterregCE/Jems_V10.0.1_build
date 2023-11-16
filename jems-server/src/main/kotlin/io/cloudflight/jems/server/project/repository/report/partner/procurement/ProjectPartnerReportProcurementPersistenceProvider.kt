package io.cloudflight.jems.server.project.repository.report.partner.procurement

import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurementChange
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectPartnerReportProcurementPersistence
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Repository
class ProjectPartnerReportProcurementPersistenceProvider(
    private val reportRepository: ProjectPartnerReportRepository,
    private val reportProcurementRepository: ProjectPartnerReportProcurementRepository,
) : ProjectPartnerReportProcurementPersistence {

    @Transactional(readOnly = true)
    override fun getById(partnerId: Long, procurementId: Long) =
        reportProcurementRepository.findByReportEntityPartnerIdAndId(
            partnerId = partnerId,
            id = procurementId,
        ).toModel()

    @Transactional(readOnly = true)
    override fun getProcurementsForReportIds(reportIds: Set<Long>, pageable: Pageable) =
        reportProcurementRepository.findByReportEntityIdIn(reportIds = reportIds, pageable).toModel()

    @Transactional(readOnly = true)
    override fun getProcurementContractNamesForReportIds(reportIds: Set<Long>) =
        reportProcurementRepository.findTop50ByReportEntityIdIn(reportIds = reportIds)
            .mapTo(HashSet()) { Pair(it.id, it.contractName) }

    @Transactional(readOnly = true)
    override fun countProcurementsForPartner(partnerId: Long) =
        reportProcurementRepository.countByReportEntityPartnerId(partnerId = partnerId)

    @Transactional
    override fun updatePartnerReportProcurement(
        partnerId: Long,
        reportId: Long,
        procurement: ProjectPartnerReportProcurementChange,
    ): ProjectPartnerReportProcurement {
        val entity = reportProcurementRepository.findByReportEntityPartnerIdAndReportEntityIdAndId(
            partnerId = partnerId,
            reportId = reportId,
            id = procurement.id,
        )

        entity.contractName = procurement.contractName
        entity.referenceNumber = procurement.referenceNumber
        entity.contractDate = procurement.contractDate
        entity.contractType = procurement.contractType
        entity.contractAmount = procurement.contractAmount
        entity.currencyCode = procurement.currencyCode
        entity.supplierName = procurement.supplierName
        entity.vatNumber = procurement.vatNumber
        entity.comment = procurement.comment
        entity.lastChanged = ZonedDateTime.now()

        return entity.toModel()
    }

    @Transactional
    override fun createPartnerReportProcurement(
        partnerId: Long,
        reportId: Long,
        procurement: ProjectPartnerReportProcurementChange,
    ) = reportProcurementRepository.save(
        procurement.toEntity(
            report = reportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId),
            lastChanged = ZonedDateTime.now(),
        )
    ).toModel()

    @Transactional
    override fun deletePartnerReportProcurement(partnerId: Long, reportId: Long, procurementId: Long) {
        reportProcurementRepository.deleteByReportEntityPartnerIdAndReportEntityIdAndId(
            partnerId = partnerId,
            reportId = reportId,
            id = procurementId,
        )
    }

    @Transactional(readOnly = true)
    override fun existsByProcurementIdAndPartnerReportIdIn(procurementId: Long, partnerReportIds: Set<Long>): Boolean {
        return reportProcurementRepository.existsByIdAndReportEntityIdIn(procurementId, partnerReportIds)
    }
}

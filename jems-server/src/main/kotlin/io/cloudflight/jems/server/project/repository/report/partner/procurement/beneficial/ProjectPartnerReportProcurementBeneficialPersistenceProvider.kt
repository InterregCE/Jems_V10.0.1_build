package io.cloudflight.jems.server.project.repository.report.partner.procurement.beneficial

import io.cloudflight.jems.server.project.entity.report.partner.procurement.beneficial.ProjectPartnerReportProcurementBeneficialEntity
import io.cloudflight.jems.server.project.repository.report.partner.procurement.ProjectPartnerReportProcurementRepository
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.beneficial.ProjectPartnerReportProcurementBeneficialChange
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.beneficial.ProjectPartnerReportProcurementBeneficialOwner
import io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial.ProjectPartnerReportProcurementBeneficialPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Repository
class ProjectPartnerReportProcurementBeneficialPersistenceProvider(
    private val reportProcurementRepository: ProjectPartnerReportProcurementRepository,
    private val reportProcurementBeneficialRepository: ProjectPartnerReportProcurementBeneficialRepository,
) : ProjectPartnerReportProcurementBeneficialPersistence {

    @Transactional(readOnly = true)
    override fun getBeneficialOwnersBeforeAndIncludingReportId(procurementId: Long, reportId: Long) =
        reportProcurementBeneficialRepository
            .findTop30ByProcurementIdAndCreatedInReportIdLessThanEqualOrderByCreatedInReportIdAscIdAsc(procurementId, reportId = reportId)
            .toModel()

    @Transactional
    override fun updateBeneficialOwners(
        partnerId: Long,
        reportId: Long,
        procurementId: Long,
        owners: List<ProjectPartnerReportProcurementBeneficialChange>
    ): List<ProjectPartnerReportProcurementBeneficialOwner> {
        val procurement = reportProcurementRepository.findByReportEntityPartnerIdAndId(partnerId, procurementId)

        val existingById = reportProcurementBeneficialRepository
            .findTop30ByProcurementAndCreatedInReportIdOrderByCreatedInReportIdAscIdAsc(procurement, reportId).associateBy { it.id }

        val toStayIds = owners.filter { it.id > 0 }.mapTo(HashSet()) { it.id }

        reportProcurementBeneficialRepository.deleteAll(existingById.minus(toStayIds).values)

        owners.forEach { newData ->
            existingById[newData.id].let { existing ->
                when {
                    existing != null -> existing.updateWith(newData)
                    else -> reportProcurementBeneficialRepository.save(newData.toEntity(procurement, reportId))
                }
            }
        }

        return reportProcurementBeneficialRepository
            .findTop30ByProcurementIdAndCreatedInReportIdLessThanEqualOrderByCreatedInReportIdAscIdAsc(procurementId, reportId = reportId)
            .toModel()
            .also { procurement.lastChanged = ZonedDateTime.now() }
    }

    private fun ProjectPartnerReportProcurementBeneficialEntity.updateWith(newData: ProjectPartnerReportProcurementBeneficialChange) {
        firstName = newData.firstName
        lastName = newData.lastName
        birth = newData.birth
        vatNumber = newData.vatNumber
    }

    @Transactional(readOnly = true)
    override fun countBeneficialOwnersCreatedBefore(procurementId: Long, reportId: Long) =
        reportProcurementBeneficialRepository.countOwnersCreatedBefore(procurementId, reportId = reportId)

}

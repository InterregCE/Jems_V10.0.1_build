package io.cloudflight.jems.server.project.repository.report.procurement

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.project.entity.report.procurement.ProjectPartnerReportProcurementEntity
import io.cloudflight.jems.server.project.entity.report.procurement.ProjectPartnerReportProcurementTranslEntity
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
    override fun existsByProcurementId(partnerId: Long, reportId: Long, procurementId: Long) =
        reportProcurementRepository
            .existsByReportEntityPartnerIdAndReportEntityIdAndId(partnerId, reportId = reportId, procurementId = procurementId)

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
        val existingById = reportProcurementRepository.findByReportEntityOrderByIdDesc(report).associateBy { it.id }
        val toStayIds = procurementNew.filter { it.id > 0 }.mapTo(HashSet()) { it.id }

        reportProcurementRepository.deleteAllById(existingById.keys.minus(toStayIds))
        procurementNew.asReversed().forEach { newData ->
            existingById[newData.id].let { existing ->
                when {
                    existing != null -> existing.updateWith(newData)
                    else -> reportProcurementRepository.save(newData.toEntity(report))
                }
            }
        }
    }

    private fun ProjectPartnerReportProcurementEntity.updateWith(newData: ProjectPartnerReportProcurementUpdate) {
        contractId = newData.contractId
        contractAmount = newData.contractAmount
        supplierName = newData.supplierName

        // update translations
        val toBeUpdatedComments = newData.comment.associateBy({ it.language }, { it.translation })
        val toBeUpdatedContractTypes = newData.contractType.associateBy({ it.language }, { it.translation })
        addMissingLanguagesIfNeeded(languages = toBeUpdatedComments.keys union toBeUpdatedContractTypes.keys)
        translatedValues.forEach {
            it.comment = toBeUpdatedComments[it.language()]
            it.contractType = toBeUpdatedContractTypes[it.language()]
        }
    }

    private fun ProjectPartnerReportProcurementEntity.addMissingLanguagesIfNeeded(languages: Set<SystemLanguage>) {
        val existingLanguages = translatedValues.mapTo(HashSet()) { it.translationId.language }
        languages.filter { !existingLanguages.contains(it) }.forEach { language ->
            translatedValues.add(
                ProjectPartnerReportProcurementTranslEntity(
                    translationId = TranslationId(this, language = language),
                    comment = null,
                    contractType = null,
                )
            )
        }
    }

}

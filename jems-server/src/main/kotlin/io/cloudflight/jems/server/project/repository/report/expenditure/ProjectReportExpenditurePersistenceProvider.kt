package io.cloudflight.jems.server.project.repository.report.expenditure

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.minio.MinioStorage
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportExpenditureCostTranslEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportLumpSumEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportUnitCostEntity
import io.cloudflight.jems.server.project.repository.report.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.file.ProjectReportFileRepository
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportUnitCost
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import kotlin.collections.HashSet

@Repository
class ProjectReportExpenditurePersistenceProvider(
    private val reportRepository: ProjectPartnerReportRepository,
    private val reportExpenditureRepository: ProjectPartnerReportExpenditureRepository,
    private val reportLumpSumRepository: ProjectPartnerReportLumpSumRepository,
    private val reportUnitCostRepository: ProjectPartnerReportUnitCostRepository,
    private val minioStorage: MinioStorage,
    private val reportFileRepository: ProjectReportFileRepository,
) : ProjectReportExpenditurePersistence {

    @Transactional(readOnly = true)
    override fun getPartnerReportExpenditureCosts(
        partnerId: Long,
        reportId: Long,
    ): List<ProjectPartnerReportExpenditureCost> =
        reportExpenditureRepository.findTop150ByPartnerReportIdAndPartnerReportPartnerIdOrderById(
            partnerId = partnerId,
            reportId = reportId,
        ).toModel()

    @Transactional
    override fun updatePartnerReportExpenditureCosts(
        partnerId: Long,
        reportId: Long,
        expenditureCosts: List<ProjectPartnerReportExpenditureCost>,
    ): List<ProjectPartnerReportExpenditureCost> {
        val reportEntity = reportRepository.findByIdAndPartnerId(partnerId = partnerId, id = reportId)

        val toNotBeDeletedIds = expenditureCosts.mapNotNullTo(HashSet()) { it.id }
        val existingIds = reportExpenditureRepository.findByPartnerReportOrderByIdDesc(reportEntity).associateBy { it.id }

        reportExpenditureRepository.deleteAll(
            existingIds.minus(toNotBeDeletedIds).values.deleteAttachments()
        )
        return expenditureCosts.map { newData ->
            existingIds[newData.id].let { existing ->
                val lumpSumsById = reportLumpSumRepository
                    .findByReportEntityPartnerIdAndReportEntityIdOrderByPeriodAscIdAsc(partnerId, reportId)
                    .associateBy { it.id }
                val unitCostsById = reportUnitCostRepository
                    .findByReportEntityPartnerIdAndReportEntityIdOrderByIdAsc(partnerId, reportId)
                    .associateBy { it.id }
                when {
                    existing != null -> existing.apply { updateWith(newData, lumpSumsById, unitCostsById) }
                    else -> reportExpenditureRepository.save(newData.toEntity(reportEntity, lumpSumsById, unitCostsById))
                }
            }
        }.toModel()
    }

    @Transactional(readOnly = true)
    override fun existsByExpenditureId(partnerId: Long, reportId: Long, expenditureId: Long) =
        reportExpenditureRepository
            .existsByPartnerReportPartnerIdAndPartnerReportIdAndId(partnerId, reportId = reportId, expenditureId = expenditureId)

    @Transactional(readOnly = true)
    override fun getAvailableLumpSums(partnerId: Long, reportId: Long): List<ProjectPartnerReportLumpSum> =
        reportLumpSumRepository.findByReportEntityPartnerIdAndReportEntityIdOrderByPeriodAscIdAsc(
            partnerId = partnerId,
            reportId = reportId,
        ).toModel()

    @Transactional(readOnly = true)
    override fun getAvailableUnitCosts(partnerId: Long, reportId: Long): List<ProjectPartnerReportUnitCost> =
        reportUnitCostRepository.findByReportEntityPartnerIdAndReportEntityIdOrderByIdAsc(
            partnerId = partnerId,
            reportId = reportId,
        ).toModel()

    private fun PartnerReportExpenditureCostEntity.updateWith(
        newData: ProjectPartnerReportExpenditureCost,
        lumpSums: Map<Long, PartnerReportLumpSumEntity>,
        unitCosts: Map<Long, PartnerReportUnitCostEntity>,
    ) {
        reportLumpSum = if (newData.lumpSumId != null) lumpSums[newData.lumpSumId] else null
        reportUnitCost = if (newData.unitCostId != null) unitCosts[newData.unitCostId] else null
        costCategory = newData.costCategory
        investmentId = newData.investmentId
        internalReferenceNumber = newData.internalReferenceNumber
        invoiceNumber = newData.invoiceNumber
        invoiceDate = newData.invoiceDate
        dateOfPayment = newData.dateOfPayment
        totalValueInvoice = newData.totalValueInvoice
        vat = newData.vat
        numberOfUnits = newData.numberOfUnits
        pricePerUnit = newData.pricePerUnit
        declaredAmount = newData.declaredAmount
        currencyCode = newData.currencyCode
        currencyConversionRate = newData.currencyConversionRate
        declaredAmountAfterSubmission = newData.declaredAmountAfterSubmission

        // update translations
        val toBeUpdatedComments = newData.comment.associateBy({ it.language }, { it.translation })
        val toBeUpdatedDescriptions = newData.description.associateBy({ it.language }, { it.translation })
        addMissingLanguagesIfNeeded(languages = toBeUpdatedComments.keys union toBeUpdatedDescriptions.keys)
        translatedValues.forEach {
            it.comment = toBeUpdatedComments[it.language()]
            it.description = toBeUpdatedDescriptions[it.language()]
        }
    }

    private fun PartnerReportExpenditureCostEntity.addMissingLanguagesIfNeeded(languages: Set<SystemLanguage>) {
        val existingLanguages = translatedValues.mapTo(HashSet()) { it.translationId.language }
        languages.filter { !existingLanguages.contains(it) }.forEach { language ->
            translatedValues.add(
                PartnerReportExpenditureCostTranslEntity(
                    translationId = TranslationId(this, language = language),
                    comment = null,
                    description = null,
                )
            )
        }
    }

    private fun Collection<PartnerReportExpenditureCostEntity>.deleteAttachments() = map {
        it.attachment?.let { file ->
            minioStorage.deleteFile(bucket = file.minioBucket, filePath = file.minioLocation)
            reportFileRepository.delete(file)
        }
        it
    }
}

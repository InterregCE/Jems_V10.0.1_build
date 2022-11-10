package io.cloudflight.jems.server.project.repository.report.expenditure

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.minio.GenericProjectFileRepository
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportExpenditureCostTranslEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportInvestmentEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportLumpSumEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportUnitCostEntity
import io.cloudflight.jems.server.project.repository.report.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.financialOverview.costCategory.ReportProjectPartnerExpenditureCostCategoryRepository
import io.cloudflight.jems.server.project.repository.report.financialOverview.costCategory.toBudgetOptionsModel
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportInvestment
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
    private val reportInvestmentRepository: ProjectPartnerReportInvestmentRepository,
    private val genericFileRepository: GenericProjectFileRepository,
    private val reportCostCategoriesRepository: ReportProjectPartnerExpenditureCostCategoryRepository
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

        val lumpSumsById = reportLumpSumRepository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByOrderNrAscIdAsc(partnerId, reportId)
            .associateBy { it.id }
        val unitCostsById = reportUnitCostRepository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByIdAsc(partnerId, reportId)
            .associateBy { it.id }
        val investmentsById = reportInvestmentRepository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByWorkPackageNumberAscInvestmentNumberAsc(partnerId, reportId)
            .associateBy { it.id }

        return expenditureCosts.map { newData ->
            existingIds[newData.id].let { existing ->
                when {
                    existing != null -> existing.apply { updateWith(newData, lumpSumsById, unitCostsById, investmentsById) }
                    else -> reportExpenditureRepository.save(newData.toEntity(reportEntity, lumpSumsById, unitCostsById, investmentsById))
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
        reportLumpSumRepository.findByReportEntityPartnerIdAndReportEntityIdOrderByOrderNrAscIdAsc(
            partnerId = partnerId,
            reportId = reportId,
        ).toModel()

    @Transactional(readOnly = true)
    override fun getAvailableUnitCosts(partnerId: Long, reportId: Long): List<ProjectPartnerReportUnitCost> =
        reportUnitCostRepository.findByReportEntityPartnerIdAndReportEntityIdOrderByIdAsc(
            partnerId = partnerId,
            reportId = reportId,
        ).toModel()

    @Transactional(readOnly = true)
    override fun getAvailableInvestments(partnerId: Long, reportId: Long): List<ProjectPartnerReportInvestment> =
        reportInvestmentRepository.findByReportEntityPartnerIdAndReportEntityIdOrderByWorkPackageNumberAscInvestmentNumberAsc(
            partnerId = partnerId,
            reportId = reportId,
        ).toModel()

    @Transactional(readOnly = true)
    override fun getAvailableBudgetOptions(partnerId: Long, reportId: Long): ProjectPartnerBudgetOptions =
        reportCostCategoriesRepository.findFirstByReportEntityPartnerIdAndReportEntityId(partnerId, reportId)
            .toBudgetOptionsModel()


    private fun PartnerReportExpenditureCostEntity.updateWith(
        newData: ProjectPartnerReportExpenditureCost,
        lumpSums: Map<Long, PartnerReportLumpSumEntity>,
        unitCosts: Map<Long, PartnerReportUnitCostEntity>,
        investments: Map<Long, PartnerReportInvestmentEntity>,
    ) {
        reportLumpSum = if (newData.lumpSumId != null) lumpSums[newData.lumpSumId] else null
        reportUnitCost = if (newData.unitCostId != null) unitCosts[newData.unitCostId] else null
        costCategory = newData.costCategory
        reportInvestment = if (newData.investmentId != null) investments[newData.investmentId] else null
        procurementId = newData.contractId
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
        it.attachment?.let { file -> genericFileRepository.delete(file) }
        it
    }
}

package io.cloudflight.jems.server.project.repository.report.partner.expenditure

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.toFullModel
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportLumpSumEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportUnitCostEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportInvestmentEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportExpenditureCostTranslEntity
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.partner.control.expenditure.PartnerReportParkedExpenditureRepository
import io.cloudflight.jems.server.project.repository.report.partner.financialOverview.costCategory.ReportProjectPartnerExpenditureCostCategoryRepository
import io.cloudflight.jems.server.project.repository.report.partner.financialOverview.costCategory.toBudgetOptionsModel
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportInvestment
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportUnitCost
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectPartnerReportExpenditurePersistenceProvider(
    private val reportRepository: ProjectPartnerReportRepository,
    private val reportExpenditureRepository: ProjectPartnerReportExpenditureRepository,
    private val reportExpenditureParkedRepository: PartnerReportParkedExpenditureRepository,
    private val reportLumpSumRepository: ProjectPartnerReportLumpSumRepository,
    private val reportUnitCostRepository: ProjectPartnerReportUnitCostRepository,
    private val reportInvestmentRepository: ProjectPartnerReportInvestmentRepository,
    private val fileRepository: JemsProjectFileService,
    private val reportCostCategoriesRepository: ReportProjectPartnerExpenditureCostCategoryRepository
) : ProjectPartnerReportExpenditurePersistence {

    @Transactional(readOnly = true)
    override fun getPartnerReportExpenditureCosts(
        partnerId: Long,
        reportId: Long,
    ): List<ProjectPartnerReportExpenditureCost> =
        reportExpenditureRepository.findTop150ByPartnerReportIdAndPartnerReportPartnerIdOrderById(
            partnerId = partnerId,
            reportId = reportId,
        ).toModel()

    @Transactional(readOnly = true)
    override fun getPartnerReportExpenditureCosts(ids: Set<Long>, pageable: Pageable) =
        reportExpenditureRepository.findAllByIdIn(ids, pageable).toModel()

    @Transactional(readOnly = true)
    override fun getPartnerReportExpenditureCostsByProjectReportId(projectReportId: Long): List<ProjectPartnerReportExpenditureCost> =
        reportExpenditureRepository.findAllByPartnerReportProjectReportId(projectReportId).toModel()


    @Transactional
    override fun updatePartnerReportExpenditureCosts(
        partnerId: Long,
        reportId: Long,
        expenditureCosts: List<ProjectPartnerReportExpenditureCost>,
    ): List<ProjectPartnerReportExpenditureCost> {
        val reportEntity = reportRepository.findByIdAndPartnerId(partnerId = partnerId, id = reportId)

        val toNotBeDeletedIds = expenditureCosts.mapNotNullTo(HashSet()) { it.id }
        val existingIds = reportExpenditureRepository.findByPartnerReportIdOrderByIdDesc(reportId).associateBy { it.id }

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

        return expenditureCosts.mapIndexed { index, newData ->
            existingIds[newData.id].let { existing ->
                when {
                    existing != null -> existing.apply {
                        updateWith(newData, lumpSumsById, unitCostsById, investmentsById)
                    }
                    else -> reportExpenditureRepository.save(
                        newData.toNewEntity(reportEntity, lumpSumsById, unitCostsById, investmentsById)
                    )
                }
            }
        }.toModel()
    }

    @Transactional
    override fun reIncludeParkedExpenditure(
        partnerId: Long,
        reportId: Long,
        expenditureId: Long,
    ): ProjectPartnerReportExpenditureCost {
        val reportEntity = reportRepository.findByIdAndPartnerId(partnerId = partnerId, id = reportId)

        val parkedExpenditure = reportExpenditureParkedRepository
            .findParkedExpenditure(partnerId = partnerId, id = expenditureId)

        return reportExpenditureRepository.save(
            parkedExpenditure.clone(
                newReportToBeLinked = reportEntity,
                clonedAttachment = null,
                lumpSumResolver = {
                    reportLumpSumRepository.findByReportEntityIdAndProgrammeLumpSumIdAndOrderNr(reportId, it.first, it.second)
                },
                unitCostResolver = { reportUnitCostRepository.findByReportEntityIdAndProgrammeUnitCostId(reportId, programmeUnitCostId = it) },
                investmentResolver = { reportInvestmentRepository.findByReportEntityIdAndInvestmentId(reportId, projectInvestmentId = it) },
            )
        ).toModel()
    }

    @Transactional
    override fun markAsSampledAndLock(expenditureIds: Set<Long>) =
        reportExpenditureRepository.findAllById(expenditureIds).forEach {
            it.partOfSample = true
            it.partOfSampleLocked = true
        }


    @Transactional(readOnly = true)
    override fun existsByExpenditureId(partnerId: Long, reportId: Long, expenditureId: Long) =
        reportExpenditureRepository
            .existsByPartnerReportPartnerIdAndPartnerReportIdAndId(partnerId, reportId = reportId, expenditureId = expenditureId)

    @Transactional(readOnly = true)
    override fun getExpenditureAttachment(partnerId: Long, expenditureId: Long): JemsFile? =
        reportExpenditureParkedRepository
            .findParkedExpenditure(partnerId = partnerId, id = expenditureId)
            .parkedFrom.attachment?.toFullModel()

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


    @Transactional(readOnly = true)
    override fun existsByPartnerIdAndAttachmentIdAndGdprTrue(partnerId: Long, fileId: Long): Boolean =
        reportExpenditureRepository.existsByPartnerReportPartnerIdAndAttachmentIdAndGdprTrue(partnerId = partnerId, fileId = fileId)

    @Transactional(readOnly = true)
    override fun existsByProcurementId(procurementId: Long): Boolean =
        reportExpenditureRepository.existsByProcurementId(procurementId)

    private fun PartnerReportExpenditureCostEntity.updateWith(
        newData: ProjectPartnerReportExpenditureCost,
        lumpSums: Map<Long, PartnerReportLumpSumEntity>,
        unitCosts: Map<Long, PartnerReportUnitCostEntity>,
        investments: Map<Long, PartnerReportInvestmentEntity>,
    ) {
        number = newData.number
        reportLumpSum = newData.lumpSumId?.let { lumpSums[it]!! }
        reportUnitCost = newData.unitCostId?.let { unitCosts[it]!! }
        costCategory = newData.costCategory
        gdpr = newData.gdpr
        reportInvestment = newData.investmentId?.let { investments[it]!! }
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
        it.attachment?.let { file -> fileRepository.delete(file) }
        it
    }
}

package io.cloudflight.jems.server.project.repository.report.partner.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.entity.addTranslationEntities
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.common.entity.extractTranslation
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.project.entity.report.control.expenditure.PartnerReportParkedExpenditureEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportExpenditureCostTranslEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportInvestmentEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportLumpSumEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportUnitCostEntity
import io.cloudflight.jems.server.project.repository.report.partner.toModel
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportParkedExpenditure
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportParkedLinked
import org.springframework.data.domain.Page
import java.math.BigDecimal

fun List<PartnerReportExpenditureCostEntity>.toModel() = map { it.toModel() }
fun Page<PartnerReportExpenditureCostEntity>.toModel() = map { it.toParkedModel() }

fun PartnerReportExpenditureCostEntity.toModel() = ProjectPartnerReportExpenditureCost(
    id = id,
    number = number,
    lumpSumId = reportLumpSum?.id,
    unitCostId = reportUnitCost?.id,
    costCategory = costCategory,
    investmentId = reportInvestment?.id,
    contractId = procurementId,
    internalReferenceNumber = internalReferenceNumber,
    invoiceNumber = invoiceNumber,
    invoiceDate = invoiceDate,
    dateOfPayment = dateOfPayment,
    totalValueInvoice = totalValueInvoice,
    vat = vat,
    numberOfUnits = numberOfUnits,
    pricePerUnit = pricePerUnit,
    declaredAmount = declaredAmount,
    currencyCode = currencyCode,
    currencyConversionRate = currencyConversionRate,
    declaredAmountAfterSubmission = declaredAmountAfterSubmission,
    attachment = attachment?.toModel(),
    parkingMetadata = getParkingMetadata(),
    comment = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.comment) },
    description = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.description) }
)

fun PartnerReportExpenditureCostEntity.toParkedModel() = ProjectPartnerReportParkedExpenditure(
    expenditure = this.toModel(),

    lumpSum = reportLumpSum?.let { ProjectPartnerReportParkedLinked(it.id, it.programmeLumpSum.id, it.orderNr, false) },
    lumpSumName = reportLumpSum?.programmeLumpSum?.translatedValues?.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.name) },
    unitCost = reportUnitCost?.let { ProjectPartnerReportParkedLinked(it.id, it.programmeUnitCost.id, null, false) },
    unitCostName = reportUnitCost?.programmeUnitCost?.translatedValues?.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.name) },
    investment = reportInvestment?.let { ProjectPartnerReportParkedLinked(it.id, it.investmentId, null, false) },
    investmentName = reportInvestment?.let { "I${it.workPackageNumber}.${it.investmentNumber}" },
)

fun PartnerReportExpenditureCostEntity.getParkingMetadata(): ExpenditureParkingMetadata? {
    if (unParkedFrom != null && reportOfOrigin != null && originalNumber != null)
        return ExpenditureParkingMetadata(
            reportOfOriginId = reportOfOrigin!!.id,
            reportOfOriginNumber = reportOfOrigin!!.number,
            originalExpenditureNumber = originalNumber!!,
        )
    return null
}

fun ProjectPartnerReportExpenditureCost.toEntity(
    reportEntity: ProjectPartnerReportEntity,
    lumpSums: Map<Long, PartnerReportLumpSumEntity>,
    unitCosts: Map<Long, PartnerReportUnitCostEntity>,
    investments: Map<Long, PartnerReportInvestmentEntity>,
) =
    PartnerReportExpenditureCostEntity(
        id = id ?: 0L,
        number = number,
        partnerReport = reportEntity,
        reportLumpSum = if (lumpSumId != null) lumpSums[lumpSumId] else null,
        reportUnitCost = if (unitCostId != null) unitCosts[unitCostId] else null,
        costCategory = costCategory,
        reportInvestment = if (investmentId != null) investments[investmentId] else null,
        procurementId = contractId,
        internalReferenceNumber = internalReferenceNumber,
        invoiceNumber = invoiceNumber,
        invoiceDate = invoiceDate,
        dateOfPayment = dateOfPayment,
        totalValueInvoice = totalValueInvoice,
        vat = vat,
        numberOfUnits = numberOfUnits,
        pricePerUnit = pricePerUnit,
        declaredAmount = declaredAmount,
        currencyCode = currencyCode,
        currencyConversionRate = currencyConversionRate,
        declaredAmountAfterSubmission = declaredAmountAfterSubmission,
        translatedValues = mutableSetOf(),
        attachment = null,
        partOfSample = false,
        partOfSampleLocked = false,
        certifiedAmount = BigDecimal.ZERO,
        deductedAmount = BigDecimal.ZERO,
        typologyOfErrorId = null,
        parked = false,
        verificationComment = null,
        unParkedFrom = null,
        reportOfOrigin = null,
        originalNumber = null,
    ).apply {
        translatedValues.addTranslation(this, comment, description)
    }

fun PartnerReportParkedExpenditureEntity.clone(
    newReportToBeLinked: ProjectPartnerReportEntity,
    clonedAttachment: JemsFileMetadataEntity?,
    lumpSumResolver: (Pair<Long, Int>) -> PartnerReportLumpSumEntity,
    unitCostResolver: (Long) -> PartnerReportUnitCostEntity,
    investmentResolver: (Long) -> PartnerReportInvestmentEntity,
): PartnerReportExpenditureCostEntity {
    val comment = parkedFrom.translatedValues.extractField { it.comment }
    val description = parkedFrom.translatedValues.extractField { it.description }

    return PartnerReportExpenditureCostEntity(
        id = 0L,
        number = 0,
        partnerReport = newReportToBeLinked,
        reportLumpSum = parkedFrom.reportLumpSum?.let { lumpSumResolver.invoke(Pair(it.programmeLumpSum.id, it.orderNr)) },
        reportUnitCost = parkedFrom.reportUnitCost?.programmeUnitCost?.id?.let { unitCostResolver.invoke(it) },
        costCategory = parkedFrom.costCategory,
        reportInvestment = parkedFrom.reportInvestment?.investmentId?.let { investmentResolver.invoke(it) },
        procurementId = parkedFrom.procurementId,
        internalReferenceNumber = parkedFrom.internalReferenceNumber,
        invoiceNumber = parkedFrom.invoiceNumber,
        invoiceDate = parkedFrom.invoiceDate,
        dateOfPayment = parkedFrom.dateOfPayment,
        totalValueInvoice = parkedFrom.totalValueInvoice,
        vat = parkedFrom.vat,
        numberOfUnits = parkedFrom.numberOfUnits,
        pricePerUnit = parkedFrom.pricePerUnit,
        declaredAmount = parkedFrom.declaredAmount,
        currencyCode = parkedFrom.currencyCode,
        currencyConversionRate = parkedFrom.currencyConversionRate,
        declaredAmountAfterSubmission = parkedFrom.declaredAmountAfterSubmission,
        translatedValues = mutableSetOf(),
        attachment = clonedAttachment,
        partOfSample = false,
        partOfSampleLocked = false,
        certifiedAmount = BigDecimal.ZERO,
        deductedAmount = BigDecimal.ZERO,
        typologyOfErrorId = null,
        parked = false,
        verificationComment = null,
        unParkedFrom = parkedFrom,
        reportOfOrigin = parkedFrom.reportOfOrigin ?: parkedFrom.partnerReport,
        originalNumber = originalNumber,
    ).apply {
        translatedValues.addTranslation(this, comment, description)
    }
}

fun MutableSet<PartnerReportExpenditureCostTranslEntity>.addTranslation(
    sourceEntity: PartnerReportExpenditureCostEntity,
    comment: Set<InputTranslation>,
    description: Set<InputTranslation>
) =
    this.addTranslationEntities(
        { language ->
            PartnerReportExpenditureCostTranslEntity(
                translationId = TranslationId(sourceEntity, language),
                comment = comment.extractTranslation(language),
                description = description.extractTranslation(language),
            )
        }, arrayOf(comment, description)
    )

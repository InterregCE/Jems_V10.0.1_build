package io.cloudflight.jems.server.project.repository.report.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.entity.addTranslationEntities
import io.cloudflight.jems.server.common.entity.extractTranslation
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportExpenditureCostTranslEntity
import io.cloudflight.jems.server.project.repository.report.toModel
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost

fun List<PartnerReportExpenditureCostEntity>.toModel() = map {
    ProjectPartnerReportExpenditureCost(
        id = it.id,
        costCategory = it.costCategory,
        investmentId = it.investmentId,
        contractId = it.procurementId,
        internalReferenceNumber = it.internalReferenceNumber,
        invoiceNumber = it.invoiceNumber,
        invoiceDate = it.invoiceDate,
        dateOfPayment = it.dateOfPayment,
        totalValueInvoice = it.totalValueInvoice,
        vat = it.vat,
        declaredAmount = it.declaredAmount,
        currencyCode = it.currencyCode,
        currencyConversionRate = it.currencyConversionRate,
        declaredAmountAfterSubmission = it.declaredAmountAfterSubmission,
        attachment = it.attachment?.toModel(),
        comment = it.translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.comment) },
        description = it.translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.description) }
    )
}

fun List<ProjectPartnerReportExpenditureCost>.toEntities(reportEntity: ProjectPartnerReportEntity) = map {
    PartnerReportExpenditureCostEntity(
        id = it.id ?: 0L,
        partnerReport = reportEntity,
        costCategory = it.costCategory,
        investmentId = it.investmentId,
        procurementId = it.contractId,
        internalReferenceNumber = it.internalReferenceNumber,
        invoiceNumber = it.invoiceNumber,
        invoiceDate = it.invoiceDate,
        dateOfPayment = it.dateOfPayment,
        totalValueInvoice = it.totalValueInvoice,
        vat = it.vat,
        declaredAmount = it.declaredAmount,
        currencyCode = it.currencyCode,
        currencyConversionRate = it.currencyConversionRate,
        declaredAmountAfterSubmission = it.declaredAmountAfterSubmission,
        translatedValues = mutableSetOf(),
        attachment = null,
    ).apply {
        translatedValues.addTranslation(this, it.comment, it.description)
    }
}

fun ProjectPartnerReportExpenditureCost.toEntity(reportEntity: ProjectPartnerReportEntity) =
    PartnerReportExpenditureCostEntity(
        id = id ?: 0L,
        partnerReport = reportEntity,
        costCategory = costCategory,
        investmentId = investmentId,
        procurementId = contractId,
        internalReferenceNumber = internalReferenceNumber,
        invoiceNumber = invoiceNumber,
        invoiceDate = invoiceDate,
        dateOfPayment = dateOfPayment,
        totalValueInvoice = totalValueInvoice,
        vat = vat,
        declaredAmount = declaredAmount,
        currencyCode = currencyCode,
        currencyConversionRate = currencyConversionRate,
        declaredAmountAfterSubmission = declaredAmountAfterSubmission,
        translatedValues = mutableSetOf(),
        attachment = null,
    ).apply {
        translatedValues.addTranslation(this, comment, description)
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

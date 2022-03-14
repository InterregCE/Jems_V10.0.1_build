package io.cloudflight.jems.server.project.repository.report.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.entity.addTranslationEntities
import io.cloudflight.jems.server.common.entity.extractTranslation
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportExpenditureCostTranslEntity
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost

fun List<PartnerReportExpenditureCostEntity>.toModel() = map {
    ProjectPartnerReportExpenditureCost(
        id = it.id,
        costCategory = it.costCategory,
        investmentNumber = it.investmentNumber,
        contractId = it.contractId,
        internalReferenceNumber = it.internalReferenceNumber,
        invoiceNumber = it.invoiceNumber,
        invoiceDate = it.invoiceDate,
        dateOfPayment = it.dateOfPayment,
        totalValueInvoice = it.totalValueInvoice,
        vat = it.vat,
        declaredAmount = it.declaredAmount,
        comment = it.translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.comment) },
        description = it.translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.description) }
    )
}

fun List<ProjectPartnerReportExpenditureCost>.toEntities(reportEntity: ProjectPartnerReportEntity) = map {
    PartnerReportExpenditureCostEntity(
        id = it.id ?: 0L,
        partnerReport = reportEntity,
        costCategory = it.costCategory,
        investmentNumber = it.investmentNumber,
        contractId = it.contractId,
        internalReferenceNumber = it.internalReferenceNumber,
        invoiceNumber = it.invoiceNumber,
        invoiceDate = it.invoiceDate,
        dateOfPayment = it.dateOfPayment,
        totalValueInvoice = it.totalValueInvoice,
        vat = it.vat,
        declaredAmount = it.declaredAmount,
        translatedValues = mutableSetOf(),
    ).apply {
        translatedValues.addTranslation(this, it.comment, it.description)
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

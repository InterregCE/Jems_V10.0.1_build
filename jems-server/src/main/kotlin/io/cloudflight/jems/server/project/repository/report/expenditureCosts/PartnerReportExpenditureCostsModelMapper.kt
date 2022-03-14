package io.cloudflight.jems.server.project.repository.report.expenditureCosts

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.entity.addTranslationEntities
import io.cloudflight.jems.server.common.entity.extractTranslation
import io.cloudflight.jems.server.common.entity.toInstant
import io.cloudflight.jems.server.common.entity.toLocalDate
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.expenditureCosts.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.entity.report.expenditureCosts.PartnerReportExpenditureCostTranslEntity
import io.cloudflight.jems.server.project.service.report.model.PartnerReportExpenditureCost

fun PartnerReportExpenditureCostEntity.toModel() = PartnerReportExpenditureCost(
    id = id,
    costCategory = costCategory,
    investmentNumber = investmentNumber,
    contractId = contractId,
    internalReferenceNumber = internalReferenceNumber,
    invoiceNumber = invoiceNumber,
    invoiceDate = invoiceDate?.toLocalDate(),
    dateOfPayment = dateOfPayment?.toLocalDate(),
    totalValueInvoice = totalValueInvoice,
    vat = vat,
    declaredAmount = declaredAmount,
    comment = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.comment) },
    description = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.description) }
)

fun Set<PartnerReportExpenditureCostEntity>.toExpenditureModel(): List<PartnerReportExpenditureCost> =
    map { it.toModel() }

fun List<PartnerReportExpenditureCostEntity>.toExpenditureModel(): List<PartnerReportExpenditureCost> =
    map { it.toModel() }

fun PartnerReportExpenditureCostEntity.updateEntity(expenditureCost: PartnerReportExpenditureCost) {
    id = expenditureCost.id ?: 0
    costCategory = expenditureCost.costCategory
    investmentNumber = expenditureCost.investmentNumber
    contractId = expenditureCost.contractId
    internalReferenceNumber = expenditureCost.internalReferenceNumber
    invoiceNumber = expenditureCost.invoiceNumber
    invoiceDate = expenditureCost.invoiceDate?.toInstant()
    dateOfPayment = expenditureCost.dateOfPayment?.toInstant()
    totalValueInvoice = expenditureCost.totalValueInvoice
    vat = expenditureCost.vat
    declaredAmount = expenditureCost.declaredAmount
}


fun PartnerReportExpenditureCost.toEntity(partnerReport: ProjectPartnerReportEntity) =
    PartnerReportExpenditureCostEntity(
        id = id ?: 0,
        partnerReport = partnerReport,
        costCategory = costCategory,
        investmentNumber = investmentNumber,
        contractId = contractId,
        internalReferenceNumber = internalReferenceNumber,
        invoiceNumber = invoiceNumber,
        invoiceDate = invoiceDate?.toInstant(),
        dateOfPayment = dateOfPayment?.toInstant(),
        totalValueInvoice = totalValueInvoice,
        vat = vat,
        declaredAmount = declaredAmount
    ).apply {
        translatedValues.addTranslation(this, comment, description)
    }

fun PartnerReportExpenditureCostEntity.updateTranslations(
    comment: Set<InputTranslation>,
    description: Set<InputTranslation>
) {
    val toBeUpdatedComments = comment.associateBy({ it.language }, { it.translation })
    val toBeUpdatedDescriptions = description.associateBy({ it.language }, { it.translation })
    val languages = toBeUpdatedComments.keys.plus(toBeUpdatedDescriptions.keys)

    this.apply {
        this.addMissingLanguagesIfNeeded(languages)
        translatedValues.forEach {
            it.comment = toBeUpdatedComments[it.language()]
            it.description = toBeUpdatedDescriptions[it.language()]
        }
    }
}


fun PartnerReportExpenditureCostEntity.addMissingLanguagesIfNeeded(languages: Set<SystemLanguage>) {
    val existingLanguages = translatedValues.mapTo(HashSet()) { it.translationId.language }
    languages.filter { !existingLanguages.contains(it) }.forEach { language ->
        translatedValues.add(
            PartnerReportExpenditureCostTranslEntity(
                translationId = TranslationId(this, language = language),
                comment = null,
                description = null
            )
        )
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

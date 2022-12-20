package io.cloudflight.jems.server.project.repository.report.partner.control.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.repository.report.partner.toModel
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification

fun Collection<PartnerReportExpenditureCostEntity>.toExtendedModel() = map {
    ProjectPartnerReportExpenditureVerification(
        id = it.id,
        lumpSumId = it.reportLumpSum?.id,
        unitCostId = it.reportUnitCost?.id,
        costCategory = it.costCategory,
        investmentId = it.reportInvestment?.id,
        contractId = it.procurementId,
        internalReferenceNumber = it.internalReferenceNumber,
        invoiceNumber = it.invoiceNumber,
        invoiceDate = it.invoiceDate,
        dateOfPayment = it.dateOfPayment,
        totalValueInvoice = it.totalValueInvoice,
        vat = it.vat,
        numberOfUnits = it.numberOfUnits,
        pricePerUnit = it.pricePerUnit,
        declaredAmount = it.declaredAmount,
        currencyCode = it.currencyCode,
        currencyConversionRate = it.currencyConversionRate,
        declaredAmountAfterSubmission = it.declaredAmountAfterSubmission,
        attachment = it.attachment?.toModel(),
        comment = it.translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.comment) },
        description = it.translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.description) },
        partOfSample = it.partOfSample,
        certifiedAmount = it.certifiedAmount,
        deductedAmount = it.deductedAmount,
        typologyOfErrorId = it.typologyOfErrorId,
        verificationComment = it.verificationComment
    )
}

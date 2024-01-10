package io.cloudflight.jems.server.project.repository.report.partner.control.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.entity.report.control.expenditure.PartnerReportParkedExpenditureEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.repository.report.partner.expenditure.getParkingMetadata
import io.cloudflight.jems.server.project.repository.report.partner.toModel
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import java.time.ZonedDateTime

fun Collection<PartnerReportExpenditureCostEntity>.toExtendedModel(
    parkedMetadataMap: Map<Long, PartnerReportParkedExpenditureEntity>
) = map { it.toModel(parkedMetadataMap[it.id]?.parkedOn) }

fun PartnerReportExpenditureCostEntity.toModel(expenditureParkedOn: ZonedDateTime?) = ProjectPartnerReportExpenditureVerification(
    id = id,
    number = number,
    lumpSumId = reportLumpSum?.id,
    unitCostId = reportUnitCost?.id,
    costCategory = costCategory,
    gdpr = gdpr,
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
    comment = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.comment) },
    description = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.description) },
    partOfSample = partOfSample,
    partOfSampleLocked = partOfSampleLocked,
    certifiedAmount = certifiedAmount,
    deductedAmount = deductedAmount,
    typologyOfErrorId = typologyOfErrorId,
    parked = parked,
    parkedOn = expenditureParkedOn,
    verificationComment = verificationComment,
    parkingMetadata = getParkingMetadata()
)

fun PartnerReportParkedExpenditureEntity.toModel() = ExpenditureParkingMetadata (
    reportOfOriginId = reportOfOrigin.id,
    reportOfOriginNumber = reportOfOrigin.number,
    reportProjectOfOriginId = parkedInProjectReport?.id,
    originalExpenditureNumber = originalNumber
)

package io.cloudflight.jems.server.project.service.report.partner.expenditure

import io.cloudflight.jems.server.common.anonymize
import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCostOld
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCurrencyRateChange
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportParkedExpenditure
import org.springframework.data.domain.Page
import java.math.RoundingMode

fun List<ProjectPartnerReportExpenditureCost>.fillCurrencyRates(
    status: ReportStatus,
    ratesResolver: () -> Map<String, CurrencyConversion>
): List<ProjectPartnerReportExpenditureCost> {
    val rates = if (status.isOpenInitially()) ratesResolver() else emptyMap()
    return map { expenditure ->
        val currencyFrozen = expenditure.parkingMetadata != null || status.hasBeenClosed()
        val rateOrNull = if (currencyFrozen) expenditure.currencyConversionRate else rates.getOrDefault(expenditure.currencyCode, null)?.conversionRate
        expenditure.apply {
            currencyConversionRate = rateOrNull
            declaredAmountAfterSubmission = rateOrNull?.let { rate -> declaredAmount.divide(rate, 2, RoundingMode.HALF_UP) }
        }
    }
}

fun List<ProjectPartnerReportExpenditureCost>.toChanges() =
    map { ProjectPartnerReportExpenditureCurrencyRateChange(it.id!!, it.currencyConversionRate, it.declaredAmountAfterSubmission) }

fun List<ProjectPartnerReportExpenditureCost>.reNumberButSkipReIncluded(): List<ProjectPartnerReportExpenditureCost> {
    var skippedReIncluded = 0
    this.forEachIndexed { index, expenditure ->
        if (expenditure.parkingMetadata != null) {
            expenditure.number = 0
            skippedReIncluded += 1
        } else {
            expenditure.number = index.plus(1).minus(skippedReIncluded)
        }
    }
    return this
}

fun ProjectPartnerReportExpenditureCost.anonymizeIfSensitive() {
    if (this.gdpr) {
        this.apply {
            this.description = this.description.anonymize()
            this.comment = this.comment.anonymize()
            this.attachment?.anonymize()
        }
    }
}


fun List<ProjectPartnerReportExpenditureCost>.anonymizeSensitiveDataIf(canNotWorkWithSensitive: Boolean) {
    if (canNotWorkWithSensitive) {
        this.forEach { expenditureCost -> expenditureCost.anonymizeIfSensitive() }
    }
}

fun Page<ProjectPartnerReportParkedExpenditure>.anonymizeSensitiveDataIf(canNotWorkWithSensitive: Boolean) {
    if (canNotWorkWithSensitive) {
        this.content.onEach { it.expenditure.anonymizeIfSensitive() }
    }
}

fun ProjectPartnerReportExpenditureCost.asOld() = ProjectPartnerReportExpenditureCostOld(
    id = id!!,
    number = number,
    lumpSumId = lumpSumId,
    unitCostId = unitCostId,
    gdpr = gdpr,
    category = costCategory,
    investmentId = investmentId,
    procurementId = contractId,
    internalReferenceNumber = internalReferenceNumber,
    invoiceNumber = invoiceNumber,
    invoiceDate = invoiceDate,
    dateOfPayment = dateOfPayment,
    comment = comment,
    description = description,
    totalValueInvoice = totalValueInvoice,
    vat = vat,
    numberOfUnits = numberOfUnits,
    pricePerUnit = pricePerUnit,
    declaredAmount = declaredAmount,
    currencyCode = currencyCode,
    currencyConversionRate = currencyConversionRate,
    declaredAmountAfterSubmission = declaredAmountAfterSubmission,
    parkingMetadata = parkingMetadata,
)

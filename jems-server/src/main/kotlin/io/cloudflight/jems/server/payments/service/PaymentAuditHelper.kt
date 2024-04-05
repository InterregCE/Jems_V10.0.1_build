package io.cloudflight.jems.server.payments.service

import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.model.ec.CorrectionInEcPaymentMetadata
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import java.time.LocalDate

fun computeYearNumber(startingDate: LocalDate) =
    startingDate.year - 2020

fun getPartnerName(partnerRole: ProjectPartnerRole, partnerNumber: Int?): String =
    partnerRole.isLead.let {
        if (it) "LP${partnerNumber}" else "PP${partnerNumber}"
    }

fun PaymentApplicationToEcDetail.toDescription(previousStatus: PaymentEcStatus, newStatus: PaymentEcStatus) =
    "Payment application to EC number $id " +
        "created for Fund (${paymentApplicationToEcSummary.programmeFund.id}, ${paymentApplicationToEcSummary.programmeFund.type}) " +
        "for accounting Year ${computeYearNumber(paymentApplicationToEcSummary.accountingYear.startDate)}: " +
        "${paymentApplicationToEcSummary.accountingYear.startDate} - ${paymentApplicationToEcSummary.accountingYear.endDate} " +
        "changes status from ${previousStatus.name} to ${newStatus.name}"

fun PaymentAccount.toDescription(previousStatus: PaymentAccountStatus, newStatus: PaymentAccountStatus, linkedCorrectionsIds: List<Long>) =
    "Account ${id} Fund (${fund.id}, ${fund.type}) " +
            "for accounting Year ${computeYearNumber(accountingYear.startDate)}: " +
            "${accountingYear.startDate} - ${accountingYear.endDate} " +
            "changed  status from ${previousStatus.name} to ${newStatus.name}" +
            getIncludedCorrectionText(linkedCorrectionsIds)

private fun getIncludedCorrectionText(linkedCorrectionsIds: List<Long>): String {
    var corrections = arrayOf<String>()

    if (linkedCorrectionsIds.isEmpty())
        return ""
    else {
        linkedCorrectionsIds.forEach {
            corrections += "Correction ID ${it}"
        }
        return " and following items were included: " + corrections.joinToString()
    }
}

fun  Map<Long, CorrectionInEcPaymentMetadata>.formCorrectionId(): List<String> =
    this.values.map { "${it.projectId}_AC${it.auditControlNr}.${it.correctionNr}" }

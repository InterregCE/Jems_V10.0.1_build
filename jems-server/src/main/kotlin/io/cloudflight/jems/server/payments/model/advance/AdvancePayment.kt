package io.cloudflight.jems.server.payments.model.advance

import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import java.math.BigDecimal
import java.time.LocalDate

data class AdvancePayment (
    val id: Long,
    val projectCustomIdentifier: String,
    val projectAcronym: String,
    val projectId: Long,
    val linkedProjectVersion: String,
    var lastApprovedProjectVersion: String? = null,

    val partnerType: ProjectPartnerRole,
    val partnerSortNumber: Int?,
    val partnerAbbreviation: String,
    val partnerNameInOriginalLanguage: String,
    val partnerNameInEnglish: String,

    val programmeFund: ProgrammeFund? = null,
    val partnerContribution: IdNamePair? = null,
    val partnerContributionSpf: IdNamePair? = null,

    val paymentAuthorized: Boolean? = null,
    val amountPaid: BigDecimal?,
    val paymentDate: LocalDate? = null,
    var amountSettled: BigDecimal?,
    val paymentSettlements: List<AdvancePaymentSettlement>
)

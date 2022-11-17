package io.cloudflight.jems.server.payments.model.advance

import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import java.math.BigDecimal
import java.time.LocalDate

data class AdvancePaymentDetail (
    val id: Long?,
    val projectId: Long,
    val projectCustomIdentifier: String,
    val projectAcronym: String,

    val partnerId: Long,
    val partnerType: ProjectPartnerRole,
    val partnerNumber: Int?,
    val partnerAbbreviation: String,

    val programmeFund: ProgrammeFund? = null,
    val partnerContribution: IdNamePair? = null,
    val partnerContributionSpf: IdNamePair? = null,

    val amountAdvance: BigDecimal?,
    val dateOfPayment: LocalDate? = null,
    val comment: String?,

    val paymentAuthorized: Boolean? = null,
    val paymentAuthorizedUser: OutputUser? = null,
    val paymentAuthorizedDate: LocalDate? = null,
    val paymentConfirmed: Boolean? = null,
    val paymentConfirmedUser: OutputUser? = null,
    val paymentConfirmedDate: LocalDate? = null
)

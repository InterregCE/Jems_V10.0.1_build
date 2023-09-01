package io.cloudflight.jems.api.payments.dto

import io.cloudflight.jems.api.common.dto.IdNamePairDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.user.dto.OutputUser
import java.math.BigDecimal
import java.time.LocalDate

data class AdvancePaymentDetailDTO(
    val id: Long?,
    val projectId: Long,
    val projectCustomIdentifier: String,
    val projectAcronym: String,
    val projectVersion: String,

    val partnerId: Long,
    val partnerType: ProjectPartnerRoleDTO,
    val partnerNumber: Int?,
    val partnerAbbreviation: String,

    val programmeFund: ProgrammeFundDTO? = null,
    val partnerContribution: IdNamePairDTO? = null,
    val partnerContributionSpf: IdNamePairDTO? = null,

    val amountPaid: BigDecimal?,
    val paymentDate: LocalDate? = null,
    val comment: String?,

    val paymentAuthorized: Boolean? = null,
    val paymentAuthorizedUser: OutputUser? = null,
    val paymentAuthorizedDate: LocalDate? = null,
    val paymentConfirmed: Boolean? = null,
    val paymentConfirmedUser: OutputUser? = null,
    val paymentConfirmedDate: LocalDate? = null,

    val paymentSettlements: List<AdvancePaymentSettlementDTO>
)

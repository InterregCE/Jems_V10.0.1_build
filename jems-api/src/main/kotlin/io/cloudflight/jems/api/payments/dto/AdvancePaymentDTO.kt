package io.cloudflight.jems.api.payments.dto

import io.cloudflight.jems.api.common.dto.IdNamePairDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import java.math.BigDecimal
import java.time.LocalDate

data class AdvancePaymentDTO(
    val id: Long,
    val projectCustomIdentifier: String,
    val projectAcronym: String,

    val partnerType: ProjectPartnerRoleDTO,
    val partnerSortNumber: Int?,
    val partnerAbbreviation: String,
    val partnerNameInOriginalLanguage: String,
    val partnerNameInEnglish: String,

    val programmeFund: ProgrammeFundDTO? = null,
    val partnerContribution: IdNamePairDTO? = null,
    val partnerContributionSpf: IdNamePairDTO? = null,

    val paymentAuthorized: Boolean? = null,
    val amountPaid: BigDecimal?,
    val paymentDate: LocalDate? = null,
    val amountSettled: BigDecimal?,

    val linkedProjectVersion: String,
    val lastApprovedProjectVersion: String?,
)

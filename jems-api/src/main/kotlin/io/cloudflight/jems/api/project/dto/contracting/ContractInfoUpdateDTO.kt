package io.cloudflight.jems.api.project.dto.contracting

import java.time.LocalDate

data class ContractInfoUpdateDTO(
    val website: String?,
    val partnershipAgreementDate: LocalDate?
)

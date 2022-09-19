package io.cloudflight.jems.api.project.dto.contracting

import java.time.LocalDate

data class ProjectContractInfoDTO(
    val projectStartDate: LocalDate?,
    val projectEndDate: LocalDate?,
    val website: String?,
    val subsidyContractDate: LocalDate?,
    val partnershipAgreementDate: LocalDate?
)

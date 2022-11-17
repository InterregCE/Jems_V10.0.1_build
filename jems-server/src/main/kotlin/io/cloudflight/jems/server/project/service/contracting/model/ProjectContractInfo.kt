package io.cloudflight.jems.server.project.service.contracting.model

import java.time.LocalDate

data class ProjectContractInfo(
    var projectStartDate: LocalDate? = null,
    var projectEndDate: LocalDate? = null,
    val website: String?,
    var subsidyContractDate: LocalDate? = null,
    val partnershipAgreementDate: LocalDate?
)

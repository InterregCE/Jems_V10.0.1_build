package io.cloudflight.jems.server.project.service.contracting

import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.model.lastPaymentDate.ContractingClosureLastPaymentDate
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import java.time.LocalDate

fun ProjectContractingMonitoring.fillEndDateWithDuration(
    resolveDuration: () -> Int?
) = this.also {
    if (this.startDate != null) {
        this.endDate = getEndDate(this.startDate, durationInMonths = resolveDuration.invoke())
    }
}

private fun getEndDate(startDate: LocalDate, durationInMonths: Int?) =
    if (durationInMonths == null) null
    else startDate.plusMonths(durationInMonths.toLong()).minusDays(1)

fun ProjectContractingMonitoring.fillLumpSumsList(lumpSums: List<ProjectLumpSum>) = also {
    this.fastTrackLumpSums = lumpSums
}

fun ProjectContractingMonitoring.fillClosureLastPaymentDates(
    allPartnersSorted: List<ProjectPartnerSummary>,
    datePerPartner: Map<Long, LocalDate>,
) = also {
    this.lastPaymentDates = allPartnersSorted.map {
        ContractingClosureLastPaymentDate(
            partnerId = it.id!!,
            partnerNumber = it.sortNumber!!,
            partnerAbbreviation = it.abbreviation,
            partnerRole = it.role,
            partnerDisabled = !it.active,
            lastPaymentDate = datePerPartner[it.id],
        )
    }
}

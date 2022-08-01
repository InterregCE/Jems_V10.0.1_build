package io.cloudflight.jems.server.project.service.contracting

import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum

fun ProjectContractingMonitoring.fillEndDateWithDuration(
    resolveDuration: () -> Int?
) = this.also {
    if (this.startDate != null) {
        val duration = resolveDuration.invoke()
        if (duration != null)
            this.endDate = this.startDate.plusMonths(duration.toLong())
    }
}

fun ProjectContractingMonitoring.fillFTLumpSumsList(
    resolveLumpSums: () -> List<ProjectLumpSum>?
) = this.also {
    this.fastTrackLumpSums = resolveLumpSums.invoke()
}

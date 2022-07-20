package io.cloudflight.jems.server.project.service.contracting

import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring

fun ProjectContractingMonitoring.fillEndDateWithDuration(
    resolveDuration: () -> Int?
) = this.also {
    if (this.startDate != null) {
        val duration = resolveDuration.invoke()
        if (duration != null)
            this.endDate = this.startDate.plusMonths(duration.toLong())
    }
}

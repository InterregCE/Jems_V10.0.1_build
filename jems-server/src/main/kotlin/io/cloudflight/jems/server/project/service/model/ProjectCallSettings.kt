package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.server.call.service.flatrate.model.ProjectCallFlatRate
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import java.time.ZonedDateTime

data class ProjectCallSettings (
    val callId: Long,
    val callName: String,
    val startDate: ZonedDateTime,
    val endDate: ZonedDateTime,
    val lengthOfPeriod: Int,
    val flatRates: Set<ProjectCallFlatRate>,
    val lumpSums: List<ProgrammeLumpSum>,
    val unitCosts: List<ProgrammeUnitCost>,
)

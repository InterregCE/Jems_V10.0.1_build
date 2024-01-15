package io.cloudflight.jems.server.project.service.model

import java.time.LocalDate

data class ProjectPeriod (
    override val number: Int,
    override val start: Int,
    override val end: Int,
    override var startDate: LocalDate? = null,
    override var endDate: LocalDate? = null,
) : ProjectPeriodBase

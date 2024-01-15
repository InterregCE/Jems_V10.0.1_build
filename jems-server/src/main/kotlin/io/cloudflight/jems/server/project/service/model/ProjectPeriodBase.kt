package io.cloudflight.jems.server.project.service.model

import java.time.LocalDate

interface ProjectPeriodBase {
    val number: Int
    val start: Int
    val end: Int
    var startDate: LocalDate?
    var endDate: LocalDate?
}

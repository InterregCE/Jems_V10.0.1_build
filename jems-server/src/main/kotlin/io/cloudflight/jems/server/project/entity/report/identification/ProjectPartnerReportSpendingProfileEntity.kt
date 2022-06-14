package io.cloudflight.jems.server.project.entity.report.identification

import java.io.Serializable
import java.math.BigDecimal
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
class ProjectPartnerReportSpendingProfileEntity(

    @field:NotNull
    var currentReport: BigDecimal,

    @field:NotNull
    val previouslyReported: BigDecimal,

    @field:NotNull
    var nextReportForecast: BigDecimal,

) : Serializable

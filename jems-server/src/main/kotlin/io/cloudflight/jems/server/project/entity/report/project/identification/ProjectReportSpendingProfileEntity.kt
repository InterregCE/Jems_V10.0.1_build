package io.cloudflight.jems.server.project.entity.report.project.identification

import java.math.BigDecimal
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "report_project_spending_profile")
data class ProjectReportSpendingProfileEntity(

    @EmbeddedId
    val id: ProjectReportSpendingProfileId,

    @field:NotNull
    val previouslyReported: BigDecimal,

    @field:NotNull
    var currentlyReported: BigDecimal,
)

package io.cloudflight.jems.server.project.entity.report.identification

import java.math.BigDecimal
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "report_project_partner_budget_per_period")
class ProjectPartnerReportBudgetPerPeriodEntity (

    @EmbeddedId
    val id: ProjectPartnerReportBudgetPerPeriodId,

    @field:NotNull
    val periodBudget: BigDecimal,

    @field:NotNull
    val periodBudgetCumulative: BigDecimal,

    @field:NotNull
    val startMonth: Int,

    @field:NotNull
    val endMonth: Int,

)

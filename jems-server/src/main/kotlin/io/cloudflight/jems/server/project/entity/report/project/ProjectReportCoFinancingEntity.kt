package io.cloudflight.jems.server.project.entity.report.project

import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import java.math.BigDecimal
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "report_project_co_financing")
class ProjectReportCoFinancingEntity(

    @EmbeddedId
    @field:NotNull
    val id: ProjectReportCoFinancingIdEntity,

    @ManyToOne
    @JoinColumn(name = "programme_fund_id")
    val programmeFund: ProgrammeFundEntity?,

    @field:NotNull
    val percentage: BigDecimal,

    @field:NotNull
    val total: BigDecimal,
    @field:NotNull
    var current: BigDecimal,
    @field:NotNull
    val previouslyReported: BigDecimal,
    @field:NotNull
    val previouslyPaid: BigDecimal,

)

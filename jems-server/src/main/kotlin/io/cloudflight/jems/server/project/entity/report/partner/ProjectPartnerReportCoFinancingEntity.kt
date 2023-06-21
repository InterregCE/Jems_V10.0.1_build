package io.cloudflight.jems.server.project.entity.report.partner

import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import java.math.BigDecimal
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "report_project_partner_co_financing")
class ProjectPartnerReportCoFinancingEntity(

    @EmbeddedId
    @field:NotNull
    val id: ProjectPartnerReportCoFinancingIdEntity,

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
    var totalEligibleAfterControl: BigDecimal,
    @field:NotNull
    val previouslyReported: BigDecimal,
    @field:NotNull
    val previouslyValidated: BigDecimal,
    @field:NotNull
    val previouslyPaid: BigDecimal,

    // parking
    @field:NotNull var currentParked: BigDecimal,
    @field:NotNull var currentReIncluded: BigDecimal,
    @field:NotNull val previouslyReportedParked: BigDecimal,

)

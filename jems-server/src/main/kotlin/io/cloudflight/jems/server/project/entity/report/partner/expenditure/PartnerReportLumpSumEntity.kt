package io.cloudflight.jems.server.project.entity.report.partner.expenditure

import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "report_project_partner_lump_sum")
class PartnerReportLumpSumEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partner_report_id")
    @field:NotNull
    val reportEntity: ProjectPartnerReportEntity,

    @ManyToOne(optional = false)
    @field:NotNull
    val programmeLumpSum: ProgrammeLumpSumEntity,

    @field:NotNull
    val orderNr: Int,

    val period: Int?,

    @field:NotNull val total: BigDecimal,
    @field:NotNull var current: BigDecimal,
    @field:NotNull var totalEligibleAfterControl: BigDecimal,
    @field:NotNull val previouslyReported: BigDecimal,
    @field:NotNull val previouslyPaid: BigDecimal,

    // parking
    @field:NotNull var currentParked: BigDecimal,
    @field:NotNull var currentReIncluded: BigDecimal,
    @field:NotNull val previouslyReportedParked: BigDecimal,

)

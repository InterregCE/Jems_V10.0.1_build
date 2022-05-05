package io.cloudflight.jems.server.project.entity.report.expenditure

import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
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
    var programmeLumpSum: ProgrammeLumpSumEntity,

    val period: Int?,

    @field:NotNull
    val cost: BigDecimal,
)

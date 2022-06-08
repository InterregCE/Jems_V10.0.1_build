package io.cloudflight.jems.server.project.entity.report.expenditure

import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
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

@Entity(name = "report_project_partner_unit_cost")
class PartnerReportUnitCostEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partner_report_id")
    @field:NotNull
    val reportEntity: ProjectPartnerReportEntity,

    @ManyToOne(optional = false)
    @field:NotNull
    var programmeUnitCost: ProgrammeUnitCostEntity,

    @field:NotNull
    val totalCost: BigDecimal,

    @field:NotNull
    val numberOfUnits: BigDecimal
)

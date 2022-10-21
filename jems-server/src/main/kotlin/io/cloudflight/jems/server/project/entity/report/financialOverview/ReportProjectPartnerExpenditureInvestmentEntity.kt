package io.cloudflight.jems.server.project.entity.report.financialOverview

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import java.io.Serializable
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import javax.validation.constraints.NotNull

@Entity(name = "report_project_partner_expenditure_investment")
class ReportProjectPartnerExpenditureInvestmentEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(optional = false)
    @JoinColumn(name = "report_id")
    @field:NotNull
    val reportEntity: ProjectPartnerReportEntity,

    @field:NotNull
    val investmentId: Long,

    @field:NotNull
    val investmentNumber: Int,

    @field:NotNull
    val workPackageNumber: Int,

    @field:NotNull
    val total: BigDecimal,
    @field:NotNull
    var current: BigDecimal,
    @field:NotNull
    val previouslyReported: BigDecimal,

) : Serializable

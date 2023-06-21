package io.cloudflight.jems.server.project.entity.report.project.financialOverview

import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import java.math.BigDecimal
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "report_project_certificate_investment")
class ReportProjectCertificateInvestmentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id")
    @field:NotNull
    val reportEntity: ProjectReportEntity,

    @field:NotNull
    val investmentId: Long,

    @field:NotNull
    val investmentNumber: Int,

    @field:NotNull
    val workPackageNumber: Int,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<ReportProjectCertificateInvestmentTranslEntity> = mutableSetOf(),

    @field:NotNull
    val deactivated: Boolean,

    @field:NotNull val total: BigDecimal,
    @field:NotNull var current: BigDecimal,
    @field:NotNull val previouslyReported: BigDecimal,
)

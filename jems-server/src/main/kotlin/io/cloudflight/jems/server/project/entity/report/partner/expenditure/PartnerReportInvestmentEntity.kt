package io.cloudflight.jems.server.project.entity.report.partner.expenditure

import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import java.math.BigDecimal
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "report_project_partner_investment")
class PartnerReportInvestmentEntity (
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

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<PartnerReportInvestmentTranslEntity> = mutableSetOf(),

    @field:NotNull
    val deactivated: Boolean,

    @field:NotNull val total: BigDecimal,
    @field:NotNull var current: BigDecimal,
    @field:NotNull var totalEligibleAfterControl: BigDecimal,
    @field:NotNull val previouslyReported: BigDecimal,
    @field:NotNull val previouslyValidated: BigDecimal,

    // parking
    @field:NotNull var currentParked: BigDecimal,
    @field:NotNull var currentReIncluded: BigDecimal,
    @field:NotNull val previouslyReportedParked: BigDecimal,

)

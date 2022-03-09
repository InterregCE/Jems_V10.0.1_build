package io.cloudflight.jems.server.project.entity.report.contribution

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import java.math.BigDecimal
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "report_project_partner_contribution")
class ProjectPartnerReportContributionEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    @field:NotNull
    val reportEntity: ProjectPartnerReportEntity,

    val sourceOfContribution: String?,

    @Enumerated(EnumType.STRING)
    val legalStatus: ProjectPartnerContributionStatus?,

    val idFromApplicationForm: Long?,

    @field:NotNull
    val historyIdentifier: UUID,

    @field:NotNull
    val createdInThisReport: Boolean,

    @field:NotNull
    val amount: BigDecimal,

    @field:NotNull
    val previouslyReported: BigDecimal,

    @field:NotNull
    var currentlyReported: BigDecimal,
)

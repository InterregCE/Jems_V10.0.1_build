package io.cloudflight.jems.server.project.entity.report.identification

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.MapsId
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.validation.constraints.NotNull

@Entity(name = "report_project_partner_verification")
data class ProjectPartnerReportVerificationEntity (
    @Id
    val reportId: Long = 0,

    @OneToOne
    @JoinColumn(name = "report_id")
    @MapsId
    @field:NotNull
    val reportEntity: ProjectPartnerReportEntity,

    @OneToMany(mappedBy = "reportVerificationId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val generalMethodologies: MutableSet<ProjectPartnerReportVerificationGeneralMethodologyEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "reportVerificationId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val verificationInstances: List<ProjectPartnerReportOnTheSpotVerificationEntity> = emptyList(),

    @field:NotNull
    val riskBasedVerificationApplied: Boolean,
    val riskBasedVerificationDescription: String?
)

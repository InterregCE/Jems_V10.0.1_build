package io.cloudflight.jems.server.project.entity.report.partner.identification

import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
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
    var generalMethodologies: MutableSet<ProjectPartnerReportVerificationGeneralMethodologyEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "reportVerificationId", cascade = [CascadeType.ALL], orphanRemoval = true)
    var verificationInstances: MutableSet<ProjectPartnerReportOnTheSpotVerificationEntity> = mutableSetOf(),

    @field:NotNull
    var riskBasedVerificationApplied: Boolean,
    var riskBasedVerificationDescription: String?
)

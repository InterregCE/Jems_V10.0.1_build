package io.cloudflight.jems.server.project.entity.report.identification

import java.time.LocalDate
import javax.persistence.GeneratedValue
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.GenerationType
import javax.persistence.OneToMany
import javax.persistence.CascadeType
import javax.validation.constraints.NotNull

@Entity(name = "report_project_partner_on_the_spot_verification")
data class ProjectPartnerReportOnTheSpotVerificationEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotNull
    val reportVerificationId: Long,

    val verificationFrom: LocalDate?,
    val verificationTo: LocalDate?,

    @OneToMany(mappedBy = "reportOnTheSpotVerificationId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val verificationLocations: MutableSet<ProjectPartnerReportVerificationOnTheSpotLocationEntity> = mutableSetOf(),

    val verificationFocus: String?
)

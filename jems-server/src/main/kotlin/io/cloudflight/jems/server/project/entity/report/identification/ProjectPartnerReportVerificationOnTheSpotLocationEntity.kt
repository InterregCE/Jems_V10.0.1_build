package io.cloudflight.jems.server.project.entity.report.identification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportLocationOnTheSpotVerification
import javax.persistence.GeneratedValue
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.GenerationType
import javax.validation.constraints.NotNull

@Entity(name = "report_project_partner_verification_on_the_spot_location")
data class ProjectPartnerReportVerificationOnTheSpotLocationEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotNull
    val reportOnTheSpotVerificationId: Long,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val location: ReportLocationOnTheSpotVerification,
)

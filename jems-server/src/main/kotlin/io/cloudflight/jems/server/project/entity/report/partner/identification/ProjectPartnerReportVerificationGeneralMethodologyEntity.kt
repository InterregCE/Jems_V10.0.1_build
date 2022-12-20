package io.cloudflight.jems.server.project.entity.report.partner.identification

import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportMethodology
import javax.persistence.GeneratedValue
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.GenerationType
import javax.validation.constraints.NotNull

@Entity(name = "report_project_partner_verification_general_methodology")
data class ProjectPartnerReportVerificationGeneralMethodologyEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotNull
    val reportVerificationId: Long,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val methodology: ReportMethodology,
)

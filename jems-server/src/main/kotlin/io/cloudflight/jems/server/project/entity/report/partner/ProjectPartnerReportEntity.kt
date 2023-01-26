package io.cloudflight.jems.server.project.entity.report.partner

import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import java.time.ZonedDateTime
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "report_project_partner")
class ProjectPartnerReportEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotNull
    val partnerId: Long,

    @field:NotNull
    val number: Int,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var status: ReportStatus,

    @field:NotNull
    val applicationFormVersion: String,

    var firstSubmission: ZonedDateTime?,

    var controlEnd: ZonedDateTime?,

    @Embedded
    val identification: PartnerReportIdentificationEntity,

    @field:NotNull
    val createdAt: ZonedDateTime = ZonedDateTime.now(),

    @ManyToOne
    var projectReport: ProjectReportEntity?,

)

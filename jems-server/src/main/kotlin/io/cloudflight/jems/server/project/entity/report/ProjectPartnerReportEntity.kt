package io.cloudflight.jems.server.project.entity.report

import io.cloudflight.jems.server.project.entity.report.expenditureCosts.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import java.time.ZonedDateTime
import javax.persistence.CascadeType
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
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

    @Embedded
    val identification: PartnerReportIdentificationEntity,

    @OneToMany(mappedBy = "partnerReport", cascade = [CascadeType.ALL], orphanRemoval = true)
    var expenditureCosts: MutableSet<PartnerReportExpenditureCostEntity> = mutableSetOf(),

    @field:NotNull
    val createdAt: ZonedDateTime = ZonedDateTime.now()
)

package io.cloudflight.jems.server.project.entity.report.control.overview

import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import java.time.LocalDate
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity(name = "report_project_partner_control_overview")
class PartnerReportControlOverviewEntity(

    @Id
    val partnerReportId: Long = 0,

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_report_id")
    @field:NotNull
    val partnerReport: ProjectPartnerReportEntity,

    @field:NotNull
    val startDate: LocalDate,

    val requestsForClarifications: String? = null,
    val receiptOfSatisfactoryAnswers: String? = null,
    val endDate: LocalDate? = null,
    val findingDescription: String? = null,
    val followUpMeasuresFromLastReport: String? = null,
    val conclusion: String? = null,
    val followUpMeasuresForNextReport: String? = null,
    val lastCertifiedReportIdWhenCreation: Long? = null,
)

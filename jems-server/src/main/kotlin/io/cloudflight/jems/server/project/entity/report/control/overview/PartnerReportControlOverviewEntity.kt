package io.cloudflight.jems.server.project.entity.report.control.overview

import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import java.time.LocalDate
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity(name = "report_project_partner_control_overview")
class PartnerReportControlOverviewEntity(

    @Id
    val partnerReportId: Long,

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_report_id")
    @field:NotNull
    val partnerReport: ProjectPartnerReportEntity,

    @field:NotNull
    val startDate: LocalDate,

    val lastCertifiedReportIdWhenCreation: Long?,

    var requestsForClarifications: String?,
    var receiptOfSatisfactoryAnswers: String?,
    var endDate: LocalDate?,
    var findingDescription: String?,
    var followUpMeasuresFromLastReport: String?,
    var conclusion: String?,
    var followUpMeasuresForNextReport: String?,
)

package io.cloudflight.jems.server.project.entity.report.control.expenditure

import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportExpenditureCostEntity
import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId
import javax.validation.constraints.NotNull

@Entity(name = "report_project_partner_expenditure_parked")
class PartnerReportParkedExpenditureEntity(

    @Id
    val parkedFromExpenditureId: Long,

    @ManyToOne
    @JoinColumn(name = "parked_from_expenditure_id")
    @MapsId
    @field:NotNull
    val parkedFrom: PartnerReportExpenditureCostEntity,

    @ManyToOne
    @JoinColumn(name = "report_of_origin_id")
    @field:NotNull
    val reportOfOrigin: ProjectPartnerReportEntity,

    @field:NotNull
    val originalNumber: Int,

    @field:NotNull
    val parkedOn: ZonedDateTime

)

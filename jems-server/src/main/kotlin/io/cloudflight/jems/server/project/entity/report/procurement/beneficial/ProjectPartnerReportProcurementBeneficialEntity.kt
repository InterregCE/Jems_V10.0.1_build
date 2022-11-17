package io.cloudflight.jems.server.project.entity.report.procurement.beneficial

import io.cloudflight.jems.server.project.entity.report.procurement.ProjectPartnerReportProcurementEntity
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "report_project_partner_procurement_beneficial")
class ProjectPartnerReportProcurementBeneficialEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @field:NotNull
    val procurement: ProjectPartnerReportProcurementEntity,

    @field:NotNull
    val createdInReportId: Long,

    @field:NotNull
    var firstName: String,

    @field:NotNull
    var lastName: String,

    var birth: LocalDate?,

    @field:NotNull
    var vatNumber: String,

)

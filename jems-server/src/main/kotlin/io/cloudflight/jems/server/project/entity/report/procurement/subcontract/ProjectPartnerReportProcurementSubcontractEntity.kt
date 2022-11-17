package io.cloudflight.jems.server.project.entity.report.procurement.subcontract

import io.cloudflight.jems.server.project.entity.report.procurement.ProjectPartnerReportProcurementEntity
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "report_project_partner_procurement_subcontract")
class ProjectPartnerReportProcurementSubcontractEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @field:NotNull
    val procurement: ProjectPartnerReportProcurementEntity,

    @field:NotNull
    val createdInReportId: Long,

    @field:NotNull
    var contractName: String,

    @field:NotNull
    var referenceNumber: String,

    var contractDate: LocalDate?,

    @field:NotNull
    var contractAmount: BigDecimal,

    @field:NotNull
    var currencyCode: String,

    @field:NotNull
    var supplierName: String,

    @field:NotNull
    var vatNumber: String,

)

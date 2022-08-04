package io.cloudflight.jems.server.project.entity.report.procurement

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "report_project_partner_procurement")
class ProjectPartnerReportProcurementEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "report_id")
    @field:NotNull
    val reportEntity: ProjectPartnerReportEntity,

    @field:NotNull
    var contractName: String,

    @field:NotNull
    var referenceNumber: String,

    var contractDate: LocalDate?,

    @field:NotNull
    var contractType: String,

    @field:NotNull
    var contractAmount: BigDecimal,

    @field:NotNull
    var currencyCode: String,

    @field:NotNull
    var supplierName: String,

    @field:NotNull
    var vatNumber: String,

    @field:NotNull
    var comment: String,

    @field:NotNull
    var lastChanged: ZonedDateTime,

)

package io.cloudflight.jems.server.project.entity.report.procurement

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.file.ReportProjectFileEntity
import java.math.BigDecimal
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
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
    var contractId: String,

    @field:NotNull
    var contractAmount: BigDecimal,

    @field:NotNull
    var supplierName: String,

    @ManyToOne
    @JoinColumn(name = "file_id")
    var attachment: ReportProjectFileEntity?,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<ProjectPartnerReportProcurementTranslEntity> = mutableSetOf(),

)

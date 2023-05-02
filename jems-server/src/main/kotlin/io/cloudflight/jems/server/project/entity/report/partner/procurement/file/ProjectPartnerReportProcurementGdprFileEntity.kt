package io.cloudflight.jems.server.project.entity.report.partner.procurement.file

import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.project.entity.report.partner.procurement.ProjectPartnerReportProcurementEntity
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull


@Entity(name = "report_project_partner_procurement_gdpr_file")
class ProjectPartnerReportProcurementGdprFileEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @field:NotNull
    val procurement: ProjectPartnerReportProcurementEntity,

    @field:NotNull
    val createdInReportId: Long,

    @ManyToOne
    @field:NotNull
    val file: JemsFileMetadataEntity,

)

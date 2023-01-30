package io.cloudflight.jems.server.project.entity.report.control.certificate

import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "report_project_partner_control_file")
class PartnerReportControlFileEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotNull
    val reportId: Long,

    @ManyToOne(optional = false)
    @JoinColumn(name = "generated_file_id")
    @field:NotNull
    val generatedFile: JemsFileMetadataEntity,

    @ManyToOne
    @JoinColumn(name = "signed_file_id")
    var signedFile: JemsFileMetadataEntity?,

)

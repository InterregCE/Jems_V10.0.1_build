package io.cloudflight.jems.server.project.entity.report.file

import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.user.entity.UserEntity
import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "report_project_file")
class ReportProjectFileEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val projectId: Long?,

    val partnerId: Long?,

    @field:NotNull
    val path: String,

    @field:NotNull
    val minioBucket: String,

    @field:NotNull
    val minioLocation: String,

    @field:NotNull
    val name: String,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val type: ProjectPartnerReportFileType,

    @field:NotNull
    val size: Long,

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id")
    val user: UserEntity?,

    @field:NotNull
    val uploaded: ZonedDateTime,

)

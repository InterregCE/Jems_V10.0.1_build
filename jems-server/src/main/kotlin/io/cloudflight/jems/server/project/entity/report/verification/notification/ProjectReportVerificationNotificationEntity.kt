package io.cloudflight.jems.server.project.entity.report.verification.notification

import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.user.entity.UserEntity
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "report_project_verification_notification")
class ProjectReportVerificationNotificationEntity  (

    @Id
    val id: Long,

    @ManyToOne
    @JoinColumn(name = "project_report_id")
    @field:NotNull
    var projectReport: ProjectReportEntity,

    @ManyToOne
    @JoinColumn(name = "user_id")
    @field:NotNull
    var user: UserEntity,

    @field:NotNull
    var createdAt: LocalDateTime,

)


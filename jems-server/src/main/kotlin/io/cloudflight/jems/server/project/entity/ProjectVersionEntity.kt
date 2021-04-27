package io.cloudflight.jems.server.project.entity

import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.user.entity.UserEntity
import java.sql.Timestamp
import java.time.ZonedDateTime
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "project_version")
class ProjectVersionEntity(

    @EmbeddedId
    val id: ProjectVersionId,

    @Transient
    val rowEnd: Timestamp? = null,

    @field:NotNull
    val createdAt: Timestamp = Timestamp.valueOf(ZonedDateTime.now().toLocalDateTime()),

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @field:NotNull
    val user: UserEntity,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val status: ApplicationStatus,

    )

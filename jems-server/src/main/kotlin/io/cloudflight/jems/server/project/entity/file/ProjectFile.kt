package io.cloudflight.jems.server.project.entity.file

import io.cloudflight.jems.api.project.dto.file.ProjectFileType
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.user.entity.User
import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "project_file")
data class ProjectFile(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotNull
    val bucket: String,

    @field:NotNull
    val identifier: String,

    @field:NotNull
    val name: String,

    @ManyToOne(optional = false)
    @field:NotNull
    val project: ProjectEntity,

    @ManyToOne(optional = false)
    @field:NotNull
    val author: User,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val type: ProjectFileType,

    var description: String? = null,

    @field:NotNull
    val size: Long,

    @field:NotNull
    val updated: ZonedDateTime

)

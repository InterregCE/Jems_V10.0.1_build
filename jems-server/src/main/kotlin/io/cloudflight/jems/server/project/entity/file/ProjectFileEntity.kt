package io.cloudflight.jems.server.project.entity.file

import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.user.entity.UserEntity
import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "project_file")
class ProjectFileEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotNull
    val name: String,

    @ManyToOne(optional = false)
    @field:NotNull
    val project: ProjectEntity,

    @ManyToOne(optional = false)
    @field:NotNull
    val user: UserEntity,

    var description: String? = null,

    @field:NotNull
    val size: Long,

    @field:NotNull
    val updated: ZonedDateTime

)

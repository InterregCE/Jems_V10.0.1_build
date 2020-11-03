package io.cloudflight.jems.server.project.entity.file

import io.cloudflight.jems.api.project.dto.file.ProjectFileType
import io.cloudflight.jems.server.project.entity.Project
import io.cloudflight.jems.server.user.entity.User
import java.time.ZonedDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity(name = "project_file")
data class ProjectFile(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @Column(nullable = false)
    val bucket: String,

    @Column(nullable = false)
    val identifier: String,

    @Column(nullable = false)
    val name: String,

    @ManyToOne(optional = false)
    val project: Project,

    @ManyToOne(optional = false)
    val author: User,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val type: ProjectFileType,

    @Column
    var description: String? = null,

    @Column(nullable = false)
    val size: Long,

    @Column(nullable = false)
    val updated: ZonedDateTime

)

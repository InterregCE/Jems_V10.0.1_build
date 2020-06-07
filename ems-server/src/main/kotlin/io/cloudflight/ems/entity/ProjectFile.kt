package io.cloudflight.ems.entity

import java.time.ZonedDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity(name = "project_file")
data class ProjectFile (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @Column
    val bucket: String?,

    @Column
    val identifier: String?,

    @ManyToOne
    val project: Project?,

    @Column
    val description: String?,

    @Column
    val size: Long?,

    @Column
    val updated: ZonedDateTime?

)

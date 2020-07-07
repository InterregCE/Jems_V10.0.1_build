package io.cloudflight.ems.entity

import java.time.LocalDate
import java.time.ZonedDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "project")
data class Project (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @Column(nullable = false)
    val acronym: String,

    @ManyToOne(optional = false)
    val applicant: User,

    @Column
    val submissionDate: ZonedDateTime?,

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_status_id")
    val projectStatus: ProjectStatus

)

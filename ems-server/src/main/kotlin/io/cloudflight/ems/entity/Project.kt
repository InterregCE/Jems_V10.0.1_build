package io.cloudflight.ems.entity

import java.time.ZonedDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToOne

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
    val projectStatus: ProjectStatus,

    @OneToOne(mappedBy = "project", cascade = [CascadeType.ALL])
    val qualityAssessment: ProjectQualityAssessment? = null,

    @OneToOne(mappedBy = "project", cascade = [CascadeType.ALL])
    val eligibilityAssessment: ProjectEligibilityAssessment? = null

)

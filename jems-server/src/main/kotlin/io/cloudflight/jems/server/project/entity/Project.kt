package io.cloudflight.jems.server.project.entity

import io.cloudflight.jems.server.call.entity.Call
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityPolicy
import io.cloudflight.jems.server.user.entity.User
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToOne

@Entity(name = "project")
data class Project(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_call_id")
    val call: Call,

    @Column(nullable = false)
    val acronym: String,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    val applicant: User,

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_status_id")
    val projectStatus: ProjectStatus,

    @ManyToOne(optional = true)
    @JoinColumn(name = "first_submission_id")
    val firstSubmission: ProjectStatus? = null,

    @ManyToOne(optional = true)
    @JoinColumn(name = "last_resubmission_id")
    val lastResubmission: ProjectStatus? = null,

    @OneToOne(mappedBy = "project", cascade = [CascadeType.ALL])
    val qualityAssessment: ProjectQualityAssessment? = null,

    @OneToOne(mappedBy = "project", cascade = [CascadeType.ALL])
    val eligibilityAssessment: ProjectEligibilityAssessment? = null,

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "eligibility_decision_id")
    val eligibilityDecision: ProjectStatus? = null,

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "funding_decision_id")
    val fundingDecision: ProjectStatus? = null,

    @Embedded
    val projectData: ProjectData? = null,

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_call_priority_policy_programme_priority_policy")
    val priorityPolicy: ProgrammePriorityPolicy? = null

)

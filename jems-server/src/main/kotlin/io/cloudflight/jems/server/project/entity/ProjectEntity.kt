package io.cloudflight.jems.server.project.entity

import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityPolicy
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumEntity
import io.cloudflight.jems.server.project.entity.result.ProjectResultEntity
import io.cloudflight.jems.server.user.entity.User
import javax.persistence.CascadeType
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.validation.constraints.NotNull

@Entity(name = "project")
data class ProjectEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_call_id")
    @field:NotNull
    val call: CallEntity,

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_priority_policy_objective_policy")
    val priorityPolicy: ProgrammePriorityPolicy? = null,

    @field:NotNull
    val acronym: String,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @field:NotNull
    val applicant: User,

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_status_id")
    @field:NotNull
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

    @OneToMany(mappedBy = "id.projectId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val periods: Collection<ProjectPeriodEntity> = emptyList(),

    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL], orphanRemoval = true)
    val projectResultEntities: MutableSet<ProjectResultEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "projectId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val lumpSums: Set<ProjectLumpSumEntity> = emptySet(),
)

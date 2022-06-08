package io.cloudflight.jems.server.project.entity

import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumEntity
import io.cloudflight.jems.server.project.entity.result.ProjectResultEntity
import io.cloudflight.jems.server.user.entity.UserEntity
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
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "project")
data class ProjectEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true)
    @field:NotNull
    var customIdentifier: String = "",

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_call_id")
    @field:NotNull
    val call: CallEntity,

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_priority_policy_objective_policy")
    val priorityPolicy: ProgrammeSpecificObjectiveEntity? = null,

    @field:NotNull
    val acronym: String,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @field:NotNull
    val applicant: UserEntity,

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_status_id")
    @field:NotNull
    var currentStatus: ProjectStatusHistoryEntity,

    @ManyToOne(optional = true)
    @JoinColumn(name = "first_submission_id")
    var firstSubmission: ProjectStatusHistoryEntity? = null,

    @ManyToOne(optional = true)
    @JoinColumn(name = "last_resubmission_id")
    var lastResubmission: ProjectStatusHistoryEntity? = null,

    @ManyToOne(optional = true)
    @JoinColumn(name = "first_submission_step1_id")
    var firstSubmissionStep1: ProjectStatusHistoryEntity? = null,

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "eligibility_decision_step1_id")
    var decisionEligibilityStep1: ProjectStatusHistoryEntity? = null,

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "funding_decision_step1_id")
    var decisionFundingStep1: ProjectStatusHistoryEntity? = null,

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "eligibility_decision_id")
    var decisionEligibilityStep2: ProjectStatusHistoryEntity? = null,

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "funding_pre_decision_id")
    var decisionPreFundingStep2: ProjectStatusHistoryEntity? = null,

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "funding_final_decision_id")
    var decisionFundingStep2: ProjectStatusHistoryEntity? = null,

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "modification_decision_id")
    var modificationDecision: ProjectStatusHistoryEntity? = null,

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "contracted_decision_id")
    var contractedDecision: ProjectStatusHistoryEntity? = null,

    @Embedded
    val projectData: ProjectData? = null,

    @OneToMany(mappedBy = "id.projectId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val periods: Collection<ProjectPeriodEntity> = emptyList(),

    @OneToMany(mappedBy = "resultId.projectId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val results: Set<ProjectResultEntity> = emptySet(),

    @OneToMany(mappedBy = "id.projectId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val lumpSums: List<ProjectLumpSumEntity> = emptyList(),
)

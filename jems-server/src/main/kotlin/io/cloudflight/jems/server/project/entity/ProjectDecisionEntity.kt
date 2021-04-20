package io.cloudflight.jems.server.project.entity

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToOne

@Entity(name = "project_decision")
data class ProjectDecisionEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne(mappedBy = "projectDecision", cascade = [CascadeType.ALL])
    val qualityAssessment: ProjectQualityAssessment? = null,

    @OneToOne(mappedBy = "projectDecision", cascade = [CascadeType.ALL])
    val eligibilityAssessment: ProjectEligibilityAssessment? = null,

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "eligibility_decision_id")
    var eligibilityDecision: ProjectStatusHistoryEntity? = null,

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "funding_decision_id")
    var fundingDecision: ProjectStatusHistoryEntity? = null

)
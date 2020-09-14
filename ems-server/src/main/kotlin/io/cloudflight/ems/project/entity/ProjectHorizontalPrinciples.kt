package io.cloudflight.ems.project.entity

import io.cloudflight.ems.api.project.dto.ProjectHorizontalPrinciplesEffect
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.MapsId
import javax.persistence.OneToOne

@Entity(name = "project_horizontal_principles")
data class ProjectHorizontalPrinciples(

    @Id
    @Column(name = "project_id", nullable = false)
    val projectId: Long,

    @OneToOne(optional = false)
    @MapsId
    val project: Project,

    @Column
    val sustainableDevelopmentCriteriaEffect: ProjectHorizontalPrinciplesEffect?,

    @Column
    val sustainableDevelopmentDescription: String?,

    @Column
    val equalOpportunitiesEffect: ProjectHorizontalPrinciplesEffect?,

    @Column
    val equalOpportunitiesDescription: String?,

    @Column
    val sexualEqualityEffect: ProjectHorizontalPrinciplesEffect?,

    @Column
    val sexualEqualityDescription: String?

)
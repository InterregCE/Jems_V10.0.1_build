package io.cloudflight.jems.server.project.entity.description

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

/**
 * C7
 */
@Entity(name = "project_description_c7_management")
data class ProjectManagementEntity(

    @Id
    @Column(name = "project_id")
    @field:NotNull
    val projectId: Long,

    @Embedded
    val projectCooperationCriteria: ProjectCooperationCriteriaEntity?,

    @Embedded
    val projectHorizontalPrinciples: ProjectHorizontalPrinciplesEntity?,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.projectId")
    val translatedValues: Set<ProjectManagementTransl> = emptySet()
)

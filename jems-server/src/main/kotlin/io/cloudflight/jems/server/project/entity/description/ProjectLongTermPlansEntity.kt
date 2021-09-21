package io.cloudflight.jems.server.project.entity.description

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

/**
 * C8
 */
@Entity(name = "project_description_c8_long_term_plans")
data class ProjectLongTermPlansEntity(

    @Id
    @Column(name = "project_id")
    @field:NotNull
    val projectId: Long,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.projectId")
    val translatedValues: Set<ProjectLongTermPlansTransl> = emptySet()
)

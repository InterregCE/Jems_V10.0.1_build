package io.cloudflight.jems.server.project.entity.description

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

/**
 * C3
 */
@Entity(name = "project_description_c3_partnership")
data class ProjectPartnershipEntity(

    @Id
    @Column(name = "project_id")
    @field:NotNull
    val projectId: Long,

    // projectPartnership
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.projectId")
    val translatedValues: Set<ProjectPartnershipTransl> = emptySet()

)

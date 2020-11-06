package io.cloudflight.jems.server.project.entity.description

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotNull

/**
 * C3
 */
@Entity(name = "project_description_c3_partnership")
data class ProjectPartnership(

    @Id
    @Column(name = "project_id")
    @field:NotNull
    val projectId: Long,

    val projectPartnership: String?

)

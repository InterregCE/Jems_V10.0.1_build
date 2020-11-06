package io.cloudflight.jems.server.project.entity.description

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotNull

/**
 * C8
 */
@Entity(name = "project_description_c8_long_term_plans")
data class ProjectLongTermPlans(

    @Id
    @Column(name = "project_id")
    @field:NotNull
    val projectId: Long,

    val projectOwnership: String?,

    val projectDurability: String?,

    val projectTransferability: String?
)

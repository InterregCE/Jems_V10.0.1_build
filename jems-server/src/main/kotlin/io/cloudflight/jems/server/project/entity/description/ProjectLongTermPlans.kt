package io.cloudflight.jems.server.project.entity.description

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

/**
 * C8
 */
@Entity(name = "project_description_c8_long_term_plans")
data class ProjectLongTermPlans(

    @Id
    @Column(name = "project_id", nullable = false)
    val projectId: Long,

    @Column
    val projectOwnership: String?,

    @Column
    val projectDurability: String?,

    @Column
    val projectTransferability: String?
)

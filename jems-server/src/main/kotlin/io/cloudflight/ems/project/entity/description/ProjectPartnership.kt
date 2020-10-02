package io.cloudflight.ems.project.entity.description

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

/**
 * C3
 */
@Entity(name = "project_description_c3_partnership")
data class ProjectPartnership(

    @Id
    @Column(name = "project_id", nullable = false)
    val projectId: Long,

    @Column
    val projectPartnership: String?

)

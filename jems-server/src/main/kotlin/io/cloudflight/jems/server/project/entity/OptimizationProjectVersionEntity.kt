package io.cloudflight.jems.server.project.entity

import java.sql.Timestamp
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "optimization_project_version")
class OptimizationProjectVersionEntity(

    @Id
    val projectId: Long = 0,

    @Transient
    val lastApprovedVersion: Timestamp? = null
)

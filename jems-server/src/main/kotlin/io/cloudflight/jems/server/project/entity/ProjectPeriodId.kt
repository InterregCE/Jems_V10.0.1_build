package io.cloudflight.jems.server.project.entity

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class ProjectPeriodId(

    @Column(name = "project_id", nullable = false)
    val projectId: Long,

    @Column(nullable = false)
    val number: Int

) : Serializable

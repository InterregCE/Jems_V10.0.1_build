package io.cloudflight.jems.server.project.entity

import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_period")
data class ProjectPeriod(

    @EmbeddedId
    val id: ProjectPeriodId,

    @Column(nullable = false)
    val start: Int,

    @Column(nullable = false)
    val end: Int

)

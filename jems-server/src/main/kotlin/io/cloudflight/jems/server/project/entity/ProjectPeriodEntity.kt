package io.cloudflight.jems.server.project.entity

import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "project_period")
data class ProjectPeriodEntity(

    @EmbeddedId
    val id: ProjectPeriodId,

    @field:NotNull
    val start: Int,

    @field:NotNull
    val end: Int

)

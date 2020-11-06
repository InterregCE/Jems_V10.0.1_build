package io.cloudflight.jems.server.project.entity

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
data class ProjectPeriodId(

    @Column(name = "project_id")
    @field:NotNull
    val projectId: Long,

    @field:NotNull
    val number: Int

) : Serializable

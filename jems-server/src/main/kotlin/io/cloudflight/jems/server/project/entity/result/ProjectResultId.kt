package io.cloudflight.jems.server.project.entity.result

import java.io.Serializable
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
data class ProjectResultId(

    @field:NotNull
    val projectId: Long,

    @field:NotNull
    val resultNumber: Int

) : Serializable

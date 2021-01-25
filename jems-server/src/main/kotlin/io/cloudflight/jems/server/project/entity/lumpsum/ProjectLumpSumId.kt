package io.cloudflight.jems.server.project.entity.lumpsum

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
data class ProjectLumpSumId(

    @field:NotNull
    val projectId: Long,

    @Column(name = "order_nr")
    @field:NotNull
    val orderNr: Int,
): Serializable

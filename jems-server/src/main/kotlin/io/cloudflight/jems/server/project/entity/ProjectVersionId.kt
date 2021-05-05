package io.cloudflight.jems.server.project.entity

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
data class ProjectVersionId(

    @Column
    @field:NotNull
    val version: String,

    @Column
    @field:NotNull
    val projectId: Long
) : Serializable

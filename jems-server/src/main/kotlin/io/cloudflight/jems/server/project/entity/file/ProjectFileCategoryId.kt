package io.cloudflight.jems.server.project.entity.file

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
class ProjectFileCategoryId(

    @Column
    val fileId: Long,

    @Column
    @field:NotNull
    val type: String

) : Serializable

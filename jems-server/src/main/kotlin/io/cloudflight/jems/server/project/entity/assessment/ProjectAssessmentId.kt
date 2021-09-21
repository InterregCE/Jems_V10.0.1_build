package io.cloudflight.jems.server.project.entity.assessment

import io.cloudflight.jems.server.project.entity.ProjectEntity
import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Embeddable
class ProjectAssessmentId(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @field:NotNull
    val project: ProjectEntity,

    @Column
    @field:NotNull
    val step: Int,
) : Serializable {

    override fun equals(other: Any?): Boolean = this === other ||
        other is ProjectAssessmentId && project == other.project && step == other.step

    override fun hashCode(): Int = Objects.hash(project, step)

}

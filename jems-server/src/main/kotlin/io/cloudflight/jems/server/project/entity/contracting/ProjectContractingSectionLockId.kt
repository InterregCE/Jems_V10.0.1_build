package io.cloudflight.jems.server.project.entity.contracting

import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingSection
import java.io.Serializable
import java.util.Objects
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotNull

@Embeddable
class ProjectContractingSectionLockId(

    @field:NotNull
    val projectId: Long,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val section: ProjectContractingSection,

) : Serializable {
    override fun equals(other: Any?): Boolean =
        this === other || other is ProjectContractingSectionLockId && section == other.section && projectId == other.projectId
    override fun hashCode() = Objects.hash(section, projectId)
}

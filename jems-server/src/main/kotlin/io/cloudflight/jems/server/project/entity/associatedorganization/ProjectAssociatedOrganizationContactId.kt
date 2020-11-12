package io.cloudflight.jems.server.project.entity.associatedorganization

import io.cloudflight.jems.api.project.dto.ProjectContactType
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotNull

@Embeddable
data class ProjectAssociatedOrganizationContactId (

    @Column(name = "organization_id")
    @field:NotNull
    val associatedOrganizationId: Long,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val type: ProjectContactType

) : Serializable


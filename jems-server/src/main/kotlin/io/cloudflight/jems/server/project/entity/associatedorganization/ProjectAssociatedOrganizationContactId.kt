package io.cloudflight.jems.server.project.entity.associatedorganization

import io.cloudflight.jems.api.project.dto.ProjectContactType
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
data class ProjectAssociatedOrganizationContactId (

    @Column(name = "organization_id", nullable = false)
    val associatedOrganizationId: Long,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val type: ProjectContactType

) : Serializable


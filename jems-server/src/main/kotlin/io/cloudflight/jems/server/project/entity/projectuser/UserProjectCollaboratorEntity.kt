package io.cloudflight.jems.server.project.entity.projectuser

import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotNull

@Entity(name = "account_project_collaborator")
class UserProjectCollaboratorEntity(

    @EmbeddedId
    val id: UserProjectId,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val level: ProjectCollaboratorLevel,
)

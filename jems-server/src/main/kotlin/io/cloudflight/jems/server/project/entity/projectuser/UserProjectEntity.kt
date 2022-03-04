package io.cloudflight.jems.server.project.entity.projectuser

import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "account_project")
class UserProjectEntity(

    @EmbeddedId
    val id: UserProjectId,
)

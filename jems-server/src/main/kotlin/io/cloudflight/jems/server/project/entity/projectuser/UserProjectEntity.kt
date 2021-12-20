package io.cloudflight.jems.server.project.entity.projectuser

import io.cloudflight.jems.server.project.entity.projectuser.UserProjectId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "account_project")
class UserProjectEntity(

    @EmbeddedId
    val id: UserProjectId,
)

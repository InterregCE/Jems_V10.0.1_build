package io.cloudflight.jems.server.user.entity

import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "account_project")
class UserProjectEntity(

    @EmbeddedId
    val id: UserProjectId,
)

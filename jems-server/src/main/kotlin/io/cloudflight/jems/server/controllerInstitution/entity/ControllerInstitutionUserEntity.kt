package io.cloudflight.jems.server.controllerInstitution.entity

import io.cloudflight.jems.server.controllerInstitution.service.model.UserInstitutionAccessLevel
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity(name = "controller_institution_user")
class ControllerInstitutionUserEntity(

    @EmbeddedId
    val id: ControllerInstitutionUserId,

    @Enumerated(EnumType.STRING)
    @Column(name = "permission")
    @field:NotNull
    var accessLevel: UserInstitutionAccessLevel,
)

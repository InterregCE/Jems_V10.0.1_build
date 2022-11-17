package io.cloudflight.jems.server.controllerInstitution.entity


import io.cloudflight.jems.server.user.entity.UserEntity
import java.io.Serializable
import java.util.Objects
import javax.persistence.Embeddable
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Embeddable
class ControllerInstitutionUserId(

    @field:NotNull
    @JoinColumn(name = "controller_institution_id", referencedColumnName = "id")
    val controllerInstitutionId: Long,

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @field:NotNull
    var user: UserEntity,

    ) : Serializable {

    override fun equals(other: Any?) = this === other ||
        other is ControllerInstitutionUserId &&
        user == other.user &&
        controllerInstitutionId == other.controllerInstitutionId

    override fun hashCode() = Objects.hash(user.id, controllerInstitutionId)
}


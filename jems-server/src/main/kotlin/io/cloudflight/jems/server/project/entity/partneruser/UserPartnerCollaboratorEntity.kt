package io.cloudflight.jems.server.project.entity.partneruser

import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotNull

@Entity(name = "account_partner_collaborator")
class UserPartnerCollaboratorEntity(

    @EmbeddedId
    val id: UserPartnerId,

    @field:NotNull
    val projectId: Long,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val level: PartnerCollaboratorLevel,
)

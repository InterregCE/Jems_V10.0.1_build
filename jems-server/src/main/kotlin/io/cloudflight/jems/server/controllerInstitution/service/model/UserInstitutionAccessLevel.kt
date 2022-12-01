package io.cloudflight.jems.server.controllerInstitution.service.model

import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel

enum class UserInstitutionAccessLevel(val correspondingCollaboratorLevel: PartnerCollaboratorLevel) {
    View(PartnerCollaboratorLevel.VIEW),
    Edit(PartnerCollaboratorLevel.EDIT),
}

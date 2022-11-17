package io.cloudflight.jems.server.controllerInstitution.repository

import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionPartnerEntity
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment
import io.cloudflight.jems.server.project.entity.partner.ControllerInstitutionEntity


fun List<InstitutionPartnerAssignment>.toEntities(institutionResolver: (Long) -> ControllerInstitutionEntity) = map {
    ControllerInstitutionPartnerEntity(
        partnerId = it.partnerId,
        institution = institutionResolver.invoke(it.institutionId),
        partnerProjectId = it.partnerProjectId,
    )
}

fun List<ControllerInstitutionPartnerEntity>.toModels() = map { it.toModel() }

fun ControllerInstitutionPartnerEntity.toModel() = InstitutionPartnerAssignment(
    institutionId = institution.id,
    partnerId = partnerId,
    partnerProjectId = partnerProjectId,
)

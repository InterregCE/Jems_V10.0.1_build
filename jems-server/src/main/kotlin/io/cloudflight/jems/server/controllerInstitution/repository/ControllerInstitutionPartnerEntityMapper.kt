package io.cloudflight.jems.server.controllerInstitution.repository

import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionPartnerEntity
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment
import io.cloudflight.jems.server.controllerInstitution.service.model.ProjectPartnerAssignmentMetadata
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity

fun List<ControllerInstitutionPartnerEntity>.toModels() = map { it.toModel() }

fun ControllerInstitutionPartnerEntity.toModel() = InstitutionPartnerAssignment(
    institutionId = institution?.id,
    partnerId = partnerId,
    partnerProjectId = projectIdentifier.toLong()
)

fun ProjectPartnerAssignmentMetadata.toNewEntity(partner: ProjectPartnerEntity) = ControllerInstitutionPartnerEntity(
    partnerId = 0L,
    institution = null,
    partner = partner,
    partnerNumber = partnerNumber,
    partnerAbbreviation = partnerAbbreviation,
    partnerRole = partnerRole,
    partnerActive = partnerActive,
    addressNuts3 = addressNuts3,
    addressNuts3Code = addressNuts3Code,
    addressCountry = addressCountry,
    addressCountryCode = addressCountryCode,
    addressCity = addressCity,
    addressPostalCode = addressPostalCode,
    projectIdentifier = projectIdentifier,
    projectAcronym = projectAcronym,
)

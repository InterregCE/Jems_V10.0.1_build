package io.cloudflight.jems.server.project.repository.contracting.partner.beneficialOwner

import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerBeneficialOwnerEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.ContractingPartnerBeneficialOwner

fun List<ProjectContractingPartnerBeneficialOwnerEntity>.toModel() = map {
    ContractingPartnerBeneficialOwner(
        id = it.id,
        partnerId = it.projectPartner.id,
        firstName = it.firstName,
        lastName = it.lastName,
        birth = it.birth,
        vatNumber = it.vatNumber,
    )
}

fun ContractingPartnerBeneficialOwner.toEntity(projectPartner: ProjectPartnerEntity) =
    ProjectContractingPartnerBeneficialOwnerEntity(
        projectPartner = projectPartner,
        firstName = firstName,
        lastName = lastName,
        birth = birth,
        vatNumber = vatNumber,
    )

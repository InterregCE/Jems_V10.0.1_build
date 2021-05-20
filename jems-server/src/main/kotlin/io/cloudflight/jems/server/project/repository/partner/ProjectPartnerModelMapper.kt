package io.cloudflight.jems.server.project.repository.partner

import io.cloudflight.jems.api.project.dto.InputProjectContact
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.ProjectPartnerMotivationDTO
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerCreate
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerUpdate
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerContact
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerDetail
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerAddressDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerAddressType
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.project.entity.AddressEntity
import io.cloudflight.jems.server.project.entity.Contact
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.TranslationPartnerId
import io.cloudflight.jems.server.project.entity.partner.PartnerAddressRow
import io.cloudflight.jems.server.project.entity.partner.PartnerContactRow
import io.cloudflight.jems.server.project.entity.partner.PartnerIdentityRow
import io.cloudflight.jems.server.project.entity.partner.PartnerMotivationRow
import io.cloudflight.jems.server.project.entity.partner.PartnerSimpleRow
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddress
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddressId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerContact
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerContactId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerMotivationEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerMotivationTranslEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerTranslEntity
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner

fun InputProjectPartnerCreate.toEntity(project: ProjectEntity, legalStatus: ProgrammeLegalStatusEntity) = ProjectPartnerEntity(
    project = project,
    abbreviation = abbreviation!!,
    role = role!!,
    nameInOriginalLanguage = nameInOriginalLanguage,
    nameInEnglish = nameInEnglish,
    // translatedValues - needs partnerId
    partnerType = partnerType,
    legalStatus = legalStatus,
    vat = vat,
    vatRecovery = vatRecovery,
)

fun InputProjectPartnerCreate.combineTranslatedValues(
    partnerId: Long
): MutableSet<ProjectPartnerTranslEntity> {
    val departmentMap = department.associateBy( { it.language }, { it.translation } )
    val languages = departmentMap.keys.toMutableSet()

    return languages.mapTo(HashSet()) {
        ProjectPartnerTranslEntity(
            TranslationPartnerId(partnerId, it),
            departmentMap[it]
        )
    }
}

fun InputProjectPartnerUpdate.combineTranslatedValues(
    partnerId: Long
): MutableSet<ProjectPartnerTranslEntity> {
    val departmentMap = department.associateBy( { it.language }, { it.translation } )
    val languages = departmentMap.keys.toMutableSet()

    return languages.mapTo(HashSet()) {
        ProjectPartnerTranslEntity(
            TranslationPartnerId(partnerId, it),
            departmentMap[it]
        )
    }
}

fun ProjectPartnerEntity.toProjectPartner() = ProjectPartner(
    id = id,
    abbreviation = abbreviation,
    role = role,
    sortNumber = sortNumber,
    country = addresses?.firstOrNull { it.addressId.type == ProjectPartnerAddressType.Organization }?.address?.country
)
fun Iterable<ProjectPartnerEntity>.toProjectPartner() = map { it.toProjectPartner() }

// todo remove when everything switched to Models
fun ProjectPartnerEntity.toOutputProjectPartner() = OutputProjectPartner(
    id = id,
    abbreviation = abbreviation,
    role = role,
    sortNumber = sortNumber,
    country = addresses?.firstOrNull { it.addressId.type == ProjectPartnerAddressType.Organization }?.address?.country
)
fun Iterable<ProjectPartnerEntity>.toOutputProjectPartner() = map { it.toOutputProjectPartner() }

fun ProjectPartnerEntity.toOutputProjectPartnerDetail() = OutputProjectPartnerDetail(
    id = id,
    abbreviation = abbreviation,
    role = role,
    sortNumber = sortNumber,
    nameInOriginalLanguage = nameInOriginalLanguage,
    nameInEnglish = nameInEnglish,
    department = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.department) },
    partnerType = partnerType,
    legalStatusId = legalStatus.id,
    vat = vat,
    vatRecovery = vatRecovery,
    addresses = addresses?.map { it.toDto() } ?: emptyList(),
    contacts = contacts?.map { it.toOutputProjectPartnerContact() } ?: emptyList(),
    motivation = motivation.map { it.toDto() }.firstOrNull()
)

fun ProjectPartnerAddressDTO.toEntity(partner: ProjectPartnerEntity) = ProjectPartnerAddress(
    addressId = ProjectPartnerAddressId(partner.id, type),
    address = AddressEntity(
        country = country,
        nutsRegion2 = nutsRegion2,
        nutsRegion3 = nutsRegion3,
        street = street,
        houseNumber = houseNumber,
        postalCode = postalCode,
        city = city,
        homepage = homepage
    )
)

fun InputProjectContact.toEntity(partner: ProjectPartnerEntity) = ProjectPartnerContact(
    contactId = ProjectPartnerContactId(partner.id, type),
    contact = Contact(
        title = title,
        firstName = firstName,
        lastName = lastName,
        email = email,
        telephone = telephone
    )
)

fun ProjectPartnerContact.toOutputProjectPartnerContact() = OutputProjectPartnerContact(
    type = contactId.type,
    title = contact?.title,
    firstName = contact?.firstName,
    lastName = contact?.lastName,
    email = contact?.email,
    telephone = contact?.telephone
)

fun ProjectPartnerMotivationDTO.toEntity(partnerId: Long): Set<ProjectPartnerMotivationEntity> {
    val motivation = ProjectPartnerMotivationEntity(
        partnerId = partnerId,
        translatedValues = combineTranslatedValuesRelevance(partnerId, organizationRelevance, organizationRole, organizationExperience)
    ).nullIfBlank() ?: return emptySet()

    return setOf(motivation)
}

fun combineTranslatedValuesRelevance(
    partnerId: Long,
    organizationRelevance: Set<InputTranslation>,
    organizationRole: Set<InputTranslation>,
    organizationExperience: Set<InputTranslation>
): Set<ProjectPartnerMotivationTranslEntity> {
    val organizationRelevanceMap = organizationRelevance.associateBy( { it.language }, { it.translation } )
    val organizationRoleMap = organizationRole.associateBy( { it.language }, { it.translation } )
    val organizationExperienceMap = organizationExperience.associateBy( { it.language }, { it.translation } )

    val languages = organizationRelevanceMap.keys.toMutableSet()
    languages.addAll(organizationRoleMap.keys)
    languages.addAll(organizationExperienceMap.keys)

    return languages.mapTo(HashSet()) {
        ProjectPartnerMotivationTranslEntity(
            TranslationPartnerId(partnerId, it),
            organizationRelevanceMap[it],
            organizationRoleMap[it],
            organizationExperienceMap[it]
        )
    }
}

fun ProjectPartnerMotivationEntity.toDto() = ProjectPartnerMotivationDTO(
    organizationRelevance = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.organizationRelevance) },
    organizationRole = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.organizationRole) },
    organizationExperience = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.organizationExperience) }
)

fun ProjectPartnerAddress.toDto() = ProjectPartnerAddressDTO(
    type = addressId.type,
    country = address.country,
    nutsRegion2 = address.nutsRegion2,
    nutsRegion3 = address.nutsRegion3,
    street = address.street,
    houseNumber = address.houseNumber,
    postalCode = address.postalCode,
    city = address.city,
    homepage = address.homepage
)

fun Collection<PartnerAddressRow>.toProjectPartnerAddressHistoricalData() = map { it.toModel() }.toList()

fun PartnerAddressRow.toModel() = ProjectPartnerAddressDTO(
    type = type,
    country = country,
    nutsRegion2 = nutsRegion2,
    nutsRegion3 = nutsRegion3,
    street = street,
    houseNumber = houseNumber,
    postalCode = postalCode,
    city = city,
    homepage = homepage
)

fun Collection<PartnerContactRow>.toProjectPartnerContactHistoricalData() = map { it.toModel() }.toList()

fun PartnerContactRow.toModel() = OutputProjectPartnerContact(
    type = type,
    title = title,
    firstName = firstName,
    lastName = lastName,
    email = email,
    telephone = telephone
)

fun List<PartnerMotivationRow>.toProjectPartnerMotivationHistoricalData() = this.groupBy { it.partnerId }.map { groupedRows -> ProjectPartnerMotivationDTO(
    organizationRelevance = groupedRows.value.extractField { it.organizationRelevance },
    organizationRole = groupedRows.value.extractField { it.organizationRole },
    organizationExperience = groupedRows.value.extractField { it.organizationExperience },
) }.first()

fun List<PartnerIdentityRow>.toProjectPartnerDetailHistoricalData(
    addresses: List<ProjectPartnerAddressDTO>,
    contacts: List<OutputProjectPartnerContact>,
    motivation: ProjectPartnerMotivationDTO
) = this.groupBy { it.id }.map { groupedRows -> OutputProjectPartnerDetail(
    id = groupedRows.value.first().id,
    abbreviation = groupedRows.value.first().abbreviation,
    role = groupedRows.value.first().role,
    sortNumber = groupedRows.value.first().sortNumber,
    nameInOriginalLanguage = groupedRows.value.first().nameInOriginalLanguage,
    nameInEnglish = groupedRows.value.first().nameInEnglish,
    department = groupedRows.value.extractField { it.department },
    partnerType = groupedRows.value.first().partnerType,
    legalStatusId = groupedRows.value.first().legalStatusId,
    vat = groupedRows.value.first().vat,
    vatRecovery = groupedRows.value.first().vatRecovery,
    addresses = addresses,
    contacts = contacts,
    motivation = motivation
) }.first()

fun Iterable<PartnerSimpleRow>.toOutputProjectPartnerHistoricalData() = map { it.toOutputProjectPartnerHistoricalData() }.toList()

fun PartnerSimpleRow.toOutputProjectPartnerHistoricalData() = OutputProjectPartner(
    id = id,
    abbreviation = abbreviation,
    role = role,
    sortNumber = sortNumber,
    country = country
)

package io.cloudflight.jems.server.project.repository.partner

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.entity.addTranslationEntities
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.common.entity.extractTranslation
import io.cloudflight.jems.server.controllerInstitution.service.model.ProjectPartnerAssignmentMetadata
import io.cloudflight.jems.server.payments.entity.PartnerWithContributionsRow
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.entity.stateaid.ProgrammeStateAidEntity
import io.cloudflight.jems.server.programme.repository.stateaid.toModel
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.entity.AddressEntity
import io.cloudflight.jems.server.project.entity.Contact
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.TranslationPartnerId
import io.cloudflight.jems.server.project.entity.partner.PartnerAddressRow
import io.cloudflight.jems.server.project.entity.partner.PartnerContactRow
import io.cloudflight.jems.server.project.entity.partner.PartnerDetailRow
import io.cloudflight.jems.server.project.entity.partner.PartnerIdentityRow
import io.cloudflight.jems.server.project.entity.partner.PartnerMotivationRow
import io.cloudflight.jems.server.project.entity.partner.PartnerSimpleRow
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddressEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddressId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerContactEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerContactId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerMotivationEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerMotivationTranslEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerTranslEntity
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetPerPeriodRow
import io.cloudflight.jems.server.project.entity.partner.budget.spf.ProjectSpfBeneficiaryBudgetPerPeriodRow
import io.cloudflight.jems.server.project.entity.partner.state_aid.PartnerStateAidRow
import io.cloudflight.jems.server.project.entity.partner.state_aid.ProjectPartnerStateAidActivityEntity
import io.cloudflight.jems.server.project.entity.partner.state_aid.ProjectPartnerStateAidActivityId
import io.cloudflight.jems.server.project.entity.partner.state_aid.ProjectPartnerStateAidEntity
import io.cloudflight.jems.server.project.entity.partner.state_aid.ProjectPartnerStateAidTranslEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityEntity
import io.cloudflight.jems.server.project.repository.partner.cofinancing.toContributionEntity
import io.cloudflight.jems.server.project.repository.workpackage.activity.toSummaryModel
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerBudget
import io.cloudflight.jems.server.project.service.budget.model.ProjectSpfBudgetPerPeriod
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionSpf
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddress
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddressType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerContact
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerMotivation
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerPaymentSummary
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerStateAid
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import java.math.BigDecimal
import java.time.ZoneOffset
import java.time.ZonedDateTime

fun ProjectPartner.toEntity(project: ProjectEntity, legalStatus: ProgrammeLegalStatusEntity) =
    ProjectPartnerEntity(
        id = id ?: 0,
        project = project,
        abbreviation = abbreviation!!,
        role = role!!,
        nameInOriginalLanguage = nameInOriginalLanguage,
        nameInEnglish = nameInEnglish,
        partnerType = partnerType,
        partnerSubType = partnerSubType,
        nace = nace,
        otherIdentifierNumber = otherIdentifierNumber,
        pic = pic,
        legalStatus = legalStatus,
        vat = vat,
        vatRecovery = vatRecovery,
        translatedValues = mutableSetOf()
    ).apply {
        translatedValues.addPartnerTranslations(this, department, otherIdentifierDescription)
    }

fun MutableSet<ProjectPartnerTranslEntity>.addPartnerTranslations(
    sourceEntity: ProjectPartnerEntity,
    department: Set<InputTranslation>,
    otherIdentifierDescription: Set<InputTranslation>
) =
    this.addTranslationEntities(
        { language ->
            ProjectPartnerTranslEntity(
                translationId = TranslationId(sourceEntity, language),
                department = department.extractTranslation(language),
                otherIdentifierDescription = otherIdentifierDescription.extractTranslation(language),
            )
        }, arrayOf(department, otherIdentifierDescription)
    )

fun ProjectPartnerEntity.toProjectPartner() = ProjectPartnerSummary(
    id = id,
    abbreviation = abbreviation,
    active = active,
    role = role,
    sortNumber = sortNumber,
    country = addresses?.firstOrNull { it.addressId.type == ProjectPartnerAddressType.Organization }?.address?.country,
    region = addresses?.firstOrNull { it.addressId.type == ProjectPartnerAddressType.Organization }?.address?.nutsRegion3
)

fun ProjectPartnerEntity.copy(
    projectPartner: ProjectPartner? = null,
    legalStatusRef: ProgrammeLegalStatusEntity? = null,
    newAddresses: Set<ProjectPartnerAddress>? = null,
    newContacts: Set<ProjectPartnerContact>? = null,
    newMotivation: ProjectPartnerMotivation? = null,
    newPartnerContributions: List<ProjectPartnerContribution>? = null,
): ProjectPartnerEntity = ProjectPartnerEntity(
    id = projectPartner?.id ?: id,
    active = active,
    project = project,
    abbreviation = projectPartner?.abbreviation ?: abbreviation,
    role = projectPartner?.role ?: role,
    sortNumber = sortNumber,
    createdAt = createdAt,
    nameInOriginalLanguage = projectPartner?.nameInOriginalLanguage ?: nameInOriginalLanguage,
    nameInEnglish = projectPartner?.nameInEnglish ?: nameInEnglish,
    partnerType = projectPartner?.partnerType ?: partnerType,
    partnerSubType = projectPartner?.partnerSubType ?: partnerSubType,
    nace = projectPartner?.nace ?: nace,
    otherIdentifierNumber = projectPartner?.otherIdentifierNumber ?: otherIdentifierNumber,
    pic = projectPartner?.pic ?: pic,
    legalStatus = legalStatusRef?: legalStatus,
    vat = projectPartner?.vat ?: vat,
    vatRecovery = projectPartner?.vatRecovery ?: vatRecovery,
    translatedValues = if (projectPartner != null) mutableSetOf() else translatedValues,
    addresses = newAddresses?.mapTo(HashSet()) { it.toEntity(this) } ?: addresses,
    contacts = newContacts?.mapTo(HashSet()) { it.toEntity(this) } ?: contacts,
    motivation = newMotivation?.toEntity(id) ?: motivation,
    partnerContributions = newPartnerContributions?.toContributionEntity(id) ?: partnerContributions
).apply {
    if(projectPartner != null) {
        partnerSubType = projectPartner.partnerSubType
        partnerType = projectPartner.partnerType
        translatedValues.addPartnerTranslations(this, projectPartner.department, projectPartner.otherIdentifierDescription)
    }
}

fun Iterable<ProjectPartnerEntity>.toProjectPartner() = map { it.toProjectPartner() }

// todo remove when everything switched to Models
fun ProjectPartnerEntity.toModel() = ProjectPartnerSummary(
    id = id,
    abbreviation = abbreviation,
    institutionName = null,
    active = active,
    role = role,
    sortNumber = sortNumber,
    country = addresses?.firstOrNull { it.addressId.type == ProjectPartnerAddressType.Organization }?.address?.country,
    region = addresses?.firstOrNull { it.addressId.type == ProjectPartnerAddressType.Organization }?.address?.nutsRegion3
)

fun Iterable<ProjectPartnerEntity>.toModel() = map { it.toModel() }

fun ProjectPartnerEntity.toProjectPartnerDetail() = ProjectPartnerDetail(
    projectId = project.id,
    id = id,
    active = active,
    abbreviation = abbreviation,
    role = role,
    createdAt = createdAt,
    sortNumber = sortNumber,
    nameInOriginalLanguage = nameInOriginalLanguage,
    nameInEnglish = nameInEnglish,
    department = translatedValues.extractField { it.department },
    partnerType = partnerType,
    partnerSubType = partnerSubType,
    nace = nace,
    otherIdentifierNumber = otherIdentifierNumber,
    otherIdentifierDescription = translatedValues.extractField { it.otherIdentifierDescription },
    pic = pic,
    legalStatusId = legalStatus.id,
    vat = vat,
    vatRecovery = vatRecovery,
    addresses = addresses?.map { it.toModel() } ?: emptyList(),
    contacts = contacts?.map { it.toProjectPartnerContact() } ?: emptyList(),
    motivation = motivation.map { it.toModel() }.firstOrNull(),
)

fun ProjectPartnerAddress.toEntity(partner: ProjectPartnerEntity) = ProjectPartnerAddressEntity(
    addressId = ProjectPartnerAddressId(partner.id, type),
    address = AddressEntity(
        country = country,
        countryCode = countryCode,
        nutsRegion2 = nutsRegion2,
        nutsRegion2Code = nutsRegion2Code,
        nutsRegion3 = nutsRegion3,
        nutsRegion3Code = nutsRegion3Code,
        street = street,
        houseNumber = houseNumber,
        postalCode = postalCode,
        city = city,
        homepage = homepage
    )
)

fun ProjectPartnerContact.toEntity(partner: ProjectPartnerEntity) = ProjectPartnerContactEntity(
    contactId = ProjectPartnerContactId(partner.id, type),
    contact = Contact(
        title = title,
        firstName = firstName,
        lastName = lastName,
        email = email,
        telephone = telephone
    )
)

fun ProjectPartnerContactEntity.toProjectPartnerContact() = ProjectPartnerContact(
    type = contactId.type,
    title = contact?.title,
    firstName = contact?.firstName,
    lastName = contact?.lastName,
    email = contact?.email,
    telephone = contact?.telephone
)

fun ProjectPartnerMotivation.toEntity(partnerId: Long): Set<ProjectPartnerMotivationEntity> {
    val motivation = ProjectPartnerMotivationEntity(
        partnerId = partnerId,
        translatedValues = combineTranslatedValuesRelevance(
            partnerId,
            organizationRelevance,
            organizationRole,
            organizationExperience
        )
    ).nullIfBlank() ?: return emptySet()

    return setOf(motivation)
}

fun combineTranslatedValuesRelevance(
    partnerId: Long,
    organizationRelevance: Set<InputTranslation>,
    organizationRole: Set<InputTranslation>,
    organizationExperience: Set<InputTranslation>
): Set<ProjectPartnerMotivationTranslEntity> {
    val organizationRelevanceMap = organizationRelevance.associateBy({ it.language }, { it.translation })
    val organizationRoleMap = organizationRole.associateBy({ it.language }, { it.translation })
    val organizationExperienceMap = organizationExperience.associateBy({ it.language }, { it.translation })

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

fun ProjectPartnerMotivationEntity.toModel() = ProjectPartnerMotivation(
    organizationRelevance = translatedValues.mapTo(HashSet()) {
        InputTranslation(
            it.translationId.language,
            it.organizationRelevance
        )
    },
    organizationRole = translatedValues.mapTo(HashSet()) {
        InputTranslation(
            it.translationId.language,
            it.organizationRole
        )
    },
    organizationExperience = translatedValues.mapTo(HashSet()) {
        InputTranslation(
            it.translationId.language,
            it.organizationExperience
        )
    }
)

fun ProjectPartnerAddressEntity.toModel() = ProjectPartnerAddress(
    type = addressId.type,
    country = address.country,
    countryCode = address.countryCode,
    nutsRegion2 = address.nutsRegion2,
    nutsRegion2Code = address.nutsRegion2Code,
    nutsRegion3 = address.nutsRegion3,
    nutsRegion3Code = address.nutsRegion3Code,
    street = address.street,
    houseNumber = address.houseNumber,
    postalCode = address.postalCode,
    city = address.city,
    homepage = address.homepage
)

fun Collection<PartnerAddressRow>.toProjectPartnerAddressHistoricalData() = map { it.toModel() }.toList()

fun PartnerAddressRow.toModel() = ProjectPartnerAddress(
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

fun PartnerContactRow.toModel() = ProjectPartnerContact(
    type = type,
    title = title,
    firstName = firstName,
    lastName = lastName,
    email = email,
    telephone = telephone
)

fun List<PartnerMotivationRow>.toProjectPartnerMotivationHistoricalData() =
    this.groupBy { it.partnerId }.map { groupedRows ->
        ProjectPartnerMotivation(
            organizationRelevance = groupedRows.value.extractField { it.organizationRelevance },
            organizationRole = groupedRows.value.extractField { it.organizationRole },
            organizationExperience = groupedRows.value.extractField { it.organizationExperience },
        )
    }.firstOrNull()

fun List<PartnerIdentityRow>.toProjectPartnerDetailHistoricalData(
    addresses: List<ProjectPartnerAddress>, contacts: List<ProjectPartnerContact>, motivation: ProjectPartnerMotivation?
) = this.groupBy { it.id }.map { groupedRows ->
    ProjectPartnerDetail(
        projectId = groupedRows.value.first().projectId,
        id = groupedRows.value.first().id,
        active = groupedRows.value.first().active,
        abbreviation = groupedRows.value.first().abbreviation,
        role = groupedRows.value.first().role,
        sortNumber = groupedRows.value.first().sortNumber,
        createdAt = ZonedDateTime.of(groupedRows.value.first().createdAt.toLocalDateTime(), ZoneOffset.UTC),
        nameInOriginalLanguage = groupedRows.value.first().nameInOriginalLanguage,
        nameInEnglish = groupedRows.value.first().nameInEnglish,
        department = extractField { it.department },
        partnerType = groupedRows.value.first().partnerType,
        partnerSubType = groupedRows.value.first().partnerSubType,
        nace = groupedRows.value.first().nace,
        otherIdentifierNumber = groupedRows.value.first().otherIdentifierNumber,
        otherIdentifierDescription = extractField { it.otherIdentifierDescription },
        pic = groupedRows.value.first().pic,
        legalStatusId = groupedRows.value.first().legalStatusId,
        vat = groupedRows.value.first().vat,
        vatRecovery = groupedRows.value.first().vatRecovery,
        addresses = addresses,
        contacts = contacts,
        motivation = motivation
    )
}.firstOrNull()

fun PartnerSimpleRow.toProjectPartnerHistoricalData() = ProjectPartnerSummary(
    id = id,
    abbreviation = abbreviation,
    active = active,
    role = role,
    sortNumber = sortNumber,
    country = country,
    region = nutsRegion3
)

fun PartnerSimpleRow.toProjectPartnerDTOHistoricalData() = ProjectPartnerSummary(
    id = id,
    abbreviation = abbreviation,
    active = active,
    role = role,
    sortNumber = sortNumber,
    country = country,
    region = nutsRegion3
)

fun ProjectPartnerStateAid.toEntity(
    partnerId: Long,
    workPackageActivities: List<WorkPackageActivityEntity>,
    programmeStateAid: ProgrammeStateAidEntity?
) = ProjectPartnerStateAidEntity(
    partnerId = partnerId,
    answer1 = answer1,
    answer2 = answer2,
    answer3 = answer3,
    answer4 = answer4,
    translatedValues = combineTranslatedValuesStateAid(
        partnerId,
        justification1,
        justification2,
        justification3,
        justification4
    ),
    activities = mutableListOf(),
    stateAidScheme = programmeStateAid
).also { entity ->
    entity.activities?.addAll(
        workPackageActivities.map {
            ProjectPartnerStateAidActivityEntity(ProjectPartnerStateAidActivityId(entity, it))
        }
    )
}

private fun combineTranslatedValuesStateAid(
    partnerId: Long,
    justification1: Set<InputTranslation>,
    justification2: Set<InputTranslation>,
    justification3: Set<InputTranslation>,
    justification4: Set<InputTranslation>,
): Set<ProjectPartnerStateAidTranslEntity> {
    val justification1Map = justification1.associateBy({ it.language }, { it.translation })
    val justification2Map = justification2.associateBy({ it.language }, { it.translation })
    val justification3Map = justification3.associateBy({ it.language }, { it.translation })
    val justification4Map = justification4.associateBy({ it.language }, { it.translation })

    val languages = justification1Map.keys.toMutableSet()
    languages.addAll(justification2Map.keys)
    languages.addAll(justification3Map.keys)
    languages.addAll(justification4Map.keys)

    return languages.mapTo(HashSet()) {
        ProjectPartnerStateAidTranslEntity(
            TranslationPartnerId(partnerId, it),
            justification1Map[it],
            justification2Map[it],
            justification3Map[it],
            justification4Map[it],
        )
    }
}

fun ProjectPartnerStateAidEntity.toModel() = ProjectPartnerStateAid(
    answer1 = answer1,
    justification1 = translatedValues.extractField { it.justification1 },
    answer2 = answer2,
    justification2 = translatedValues.extractField { it.justification2 },
    answer3 = answer3,
    justification3 = translatedValues.extractField { it.justification3 },
    answer4 = answer4,
    justification4 = translatedValues.extractField { it.justification4 },
    activities = activities?.map { it.id.activity.toSummaryModel() },
    stateAidScheme = stateAidScheme?.toModel()
)

private inline fun Set<ProjectPartnerStateAidTranslEntity>.extractField(extractFunction: (ProjectPartnerStateAidTranslEntity) -> String?) =
    map { InputTranslation(it.translationId.language, extractFunction.invoke(it)) }
        .filterTo(HashSet()) { !it.translation.isNullOrBlank() }

fun List<PartnerStateAidRow>.toModel(
    activities: List<WorkPackageActivity>,
    stateAid: ProgrammeStateAidEntity?
) =
    this.groupBy { it.partnerId }.map { groupedRows ->
        ProjectPartnerStateAid(
            answer1 = groupedRows.value.first().answer1,
            justification1 = groupedRows.value.extractField { it.justification1 },
            answer2 = groupedRows.value.first().answer2,
            justification2 = groupedRows.value.extractField { it.justification2 },
            answer3 = groupedRows.value.first().answer3,
            justification3 = groupedRows.value.extractField { it.justification3 },
            answer4 = groupedRows.value.first().answer4,
            justification4 = groupedRows.value.extractField { it.justification4 },
            activities = activities.map { it.toSummaryModel() },
            stateAidScheme = stateAid?.toModel()
        )
    }.firstOrNull()

fun List<ProjectPartnerBudgetPerPeriodRow>.toProjectPartnerBudgetPerPeriod() = map { it.toModel() }.toList()

fun ProjectPartnerBudgetPerPeriodRow.toModel() = ProjectPartnerBudget(
    id = id,
    periodNumber = periodNumber ?: 0,
    staffCostsPerPeriod = staffCostsPerPeriod ?: BigDecimal.ZERO,
    travelAndAccommodationCostsPerPeriod = travelAndAccommodationCostsPerPeriod ?: BigDecimal.ZERO,
    equipmentCostsPerPeriod = equipmentCostsPerPeriod ?: BigDecimal.ZERO,
    externalExpertiseAndServicesCostsPerPeriod = externalExpertiseAndServicesCostsPerPeriod ?: BigDecimal.ZERO,
    infrastructureAndWorksCostsPerPeriod = infrastructureAndWorksCostsPerPeriod ?: BigDecimal.ZERO,
    unitCostsPerPeriod = unitCostsPerPeriod ?: BigDecimal.ZERO,
    spfCostsPerPeriod = spfCostsPerPeriod ?: BigDecimal.ZERO,
)

fun List<PartnerDetailRow>.toModel(): List<ProjectPartnerDetail> =
    groupBy { it.id }.map { groupedRows ->
        ProjectPartnerDetail(
            id = groupedRows.key,
            active = groupedRows.value.first().active,
            projectId = groupedRows.value.first().projectId,
            abbreviation = groupedRows.value.first().abbreviation,
            role = groupedRows.value.first().role,
            sortNumber = groupedRows.value.first().sortNumber,
            createdAt = ZonedDateTime.of(groupedRows.value.first().createdAt.toLocalDateTime(), ZoneOffset.UTC),
            nameInOriginalLanguage = groupedRows.value.first().nameInOriginalLanguage,
            nameInEnglish = groupedRows.value.first().nameInEnglish,
            department = groupedRows.value.extractField { it.department },
            partnerType = groupedRows.value.first().partnerType,
            partnerSubType = groupedRows.value.first().partnerSubType,
            nace = groupedRows.value.first().nace,
            otherIdentifierNumber = groupedRows.value.first().otherIdentifierNumber,
            otherIdentifierDescription = groupedRows.value.extractField { it.otherIdentifierDescription },
            pic = groupedRows.value.first().pic,
            legalStatusId = groupedRows.value.first().legalStatusId,
            vat = groupedRows.value.first().vat,
            vatRecovery = groupedRows.value.first().vatRecovery,
            addresses = groupedRows.value.filter { it.addressType != null }.groupBy { it.addressType }.map { addressGroupedRows ->
                ProjectPartnerAddress(
                    type = addressGroupedRows.key!!,
                    country = addressGroupedRows.value.first().country,
                    nutsRegion2 = addressGroupedRows.value.first().nutsRegion2,
                    nutsRegion3 = addressGroupedRows.value.first().nutsRegion3,
                    street = addressGroupedRows.value.first().street,
                    houseNumber = addressGroupedRows.value.first().houseNumber,
                    postalCode = addressGroupedRows.value.first().postalCode,
                    city = addressGroupedRows.value.first().city,
                    homepage = addressGroupedRows.value.first().homepage
                )
            },
            contacts = groupedRows.value.filter { it.contactType != null }.groupBy { it.contactType }.map { contactGroupedRows ->
                ProjectPartnerContact(
                    type = contactGroupedRows.key!!,
                    title = contactGroupedRows.value.first().title,
                    firstName = contactGroupedRows.value.first().firstName,
                    lastName = contactGroupedRows.value.first().lastName,
                    email = contactGroupedRows.value.first().email,
                    telephone = contactGroupedRows.value.first().telephone
                )
            },
            motivation = groupedRows.value.firstOrNull{ it.motivationRowLanguage != null }?.let {
                ProjectPartnerMotivation(
                    organizationRelevance = groupedRows.value.extractField({ it.motivationRowLanguage }) { it.organizationRelevance },
                    organizationRole = groupedRows.value.extractField({ it.motivationRowLanguage }) { it.organizationRole },
                    organizationExperience = groupedRows.value.extractField({ it.motivationRowLanguage }) { it.organizationExperience }
                )
            }
        )

    }

fun List<ProjectSpfBeneficiaryBudgetPerPeriodRow>.toProjectPartnerSpfBudgetPerPeriod() = map { it.toModel() }.toList()

fun ProjectSpfBeneficiaryBudgetPerPeriodRow.toModel() = ProjectSpfBudgetPerPeriod(
    periodNumber = periodNumber ?: 0,
    spfCostPerPeriod = spfCostPerPeriod ?: BigDecimal.ZERO
)

fun List<PartnerWithContributionsRow>.toProjectPartnerPaymentSummaryList(): List<ProjectPartnerPaymentSummary> =
    this.groupBy { it.partnerId }.map { groupedRows ->
        extractPaymentSummaryFromRows(groupedRows.value)
    }


fun extractPaymentSummaryFromRows(rows: List<PartnerWithContributionsRow>): ProjectPartnerPaymentSummary {
    val funds = rows.groupBy { it.fundId }.map {
        ProgrammeFund(
            id = it.key,
            selected = true,
            abbreviation = it.value.extractField { it.fundAbbreviation }
        )
    }
    val contributions = rows.filter { it.partnerContributionName != null }
        .groupBy { it.partnerContributionId }.map {
        val contribution = it.value.first()
        ProjectPartnerContribution(
            id = contribution.partnerContributionId,
            name = contribution.partnerContributionName,
            status = contribution.partnerContributionStatus,
            isPartner = true,
            amount = contribution.partnerContributionAmount
        )
    }

    val spfContributions = rows.filter { it.partnerContributionSpfId != null }
        .groupBy { it.partnerContributionSpfId }.map {
        val contribution = it.value.first()
        ProjectPartnerContributionSpf(
            id = contribution.partnerContributionSpfId,
            name = contribution.partnerContributionSpfName,
            status = contribution.partnerContributionSpfStatus,
            amount = contribution.partnerContributionSpfAmount
        )
    }
    val partnerData = rows.first()
    return ProjectPartnerPaymentSummary(
        partnerSummary = ProjectPartnerSummary(
            id = partnerData.partnerId,
            abbreviation = partnerData.partnerAbbreviation,
            role = partnerData.partnerRole,
            active = partnerData.partnerActive,
            sortNumber = partnerData.partnerSortNumber
        ),
        partnerCoFinancing = funds.toMutableList(),
        partnerContributions = contributions,
        partnerContributionsSpf = spfContributions
    )
}

fun Iterable<ProjectPartnerEntity>.onlyAssignmentMetadata() = map {
    ProjectPartnerAssignmentMetadata(
        partnerId = it.id,
        partnerNumber = it.sortNumber,
        partnerAbbreviation = it.abbreviation,
        partnerRole = it.role,
        partnerActive = it.active,
        addressNuts3 = it.organizationAddress()?.nutsRegion3,
        addressNuts3Code = it.organizationAddress()?.nutsRegion3Code,
        addressCountry = it.organizationAddress()?.country,
        addressCountryCode = it.organizationAddress()?.countryCode,
        addressCity = it.organizationAddress()?.city,
        addressPostalCode = it.organizationAddress()?.postalCode,
        projectIdentifier = it.project.customIdentifier,
        projectAcronym = it.project.acronym
    )
}

private fun ProjectPartnerEntity.organizationAddress() = addresses
    ?.firstOrNull { it.addressId.type == ProjectPartnerAddressType.Organization }?.address

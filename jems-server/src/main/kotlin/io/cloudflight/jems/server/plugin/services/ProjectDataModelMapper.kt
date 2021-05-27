package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePriorityPolicySimpleDTO
import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePrioritySimple
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.ProjectPartnerMotivationDTO
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationAddress
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationDetail
import io.cloudflight.jems.api.project.dto.description.InputProjectCooperationCriteria
import io.cloudflight.jems.api.project.dto.description.InputProjectHorizontalPrinciples
import io.cloudflight.jems.api.project.dto.description.InputProjectOverallObjective
import io.cloudflight.jems.api.project.dto.description.InputProjectPartnership
import io.cloudflight.jems.api.project.dto.description.InputProjectRelevance
import io.cloudflight.jems.api.project.dto.description.OutputProjectDescription
import io.cloudflight.jems.api.project.dto.description.OutputProjectLongTermPlans
import io.cloudflight.jems.api.project.dto.description.OutputProjectManagement
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerContact
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerDetail
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerAddressDTO
import io.cloudflight.jems.plugin.contract.models.common.InputTranslationData
import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.plugin.contract.models.programme.lumpsum.ProgrammeLumpSumData
import io.cloudflight.jems.plugin.contract.models.programme.lumpsum.ProgrammeLumpSumPhaseData
import io.cloudflight.jems.plugin.contract.models.programme.priority.ProgrammeObjectivePolicyData
import io.cloudflight.jems.plugin.contract.models.programme.priority.ProgrammePriorityDataSimple
import io.cloudflight.jems.plugin.contract.models.programme.priority.ProgrammeSpecificObjectiveData
import io.cloudflight.jems.plugin.contract.models.programme.strategy.ProgrammeStrategyData
import io.cloudflight.jems.plugin.contract.models.programme.unitcost.BudgetCategoryData
import io.cloudflight.jems.plugin.contract.models.project.sectionA.ProjectDataSectionA
import io.cloudflight.jems.plugin.contract.models.project.sectionB.associatedOrganisation.ProjectAssociatedOrganizationAddressData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.associatedOrganisation.ProjectAssociatedOrganizationData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectContactTypeData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerAddressData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerAddressTypeData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerContactData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerEssentialData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerMotivationData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerRoleData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerVatRecoveryData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.BudgetCostData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.BudgetGeneralCostEntryData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.BudgetPeriodData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.BudgetStaffCostEntryData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.BudgetTravelAndAccommodationCostEntryData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.BudgetUnitCostEntryData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.PartnerBudgetData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerBudgetOptionsData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerCoFinancingAndContributionData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerCoFinancingData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerCoFinancingFundTypeData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerContributionData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerContributionStatusData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.ProjectDataSectionC
import io.cloudflight.jems.plugin.contract.models.project.sectionC.longTermPlans.ProjectLongTermPlansData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.management.ProjectCooperationCriteriaData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.management.ProjectHorizontalPrinciplesData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.management.ProjectHorizontalPrinciplesEffectData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.management.ProjectManagementData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.overallObjective.ProjectOverallObjectiveData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.partnership.ProjectPartnershipData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.relevance.ProjectRelevanceBenefitData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.relevance.ProjectRelevanceData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.relevance.ProjectRelevanceStrategyData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.relevance.ProjectRelevanceSynergyData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.relevance.ProjectTargetGroupData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.results.ProjectResultData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.results.ProjectResultTranslatedValueData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.ProjectWorkPackageData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageActivityData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageActivityDeliverableData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageActivityDeliverableTranslatedValueData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageActivityTranslatedValueData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageInvestmentAddressData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageInvestmentData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageOutputData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageOutputTranslatedValueData
import io.cloudflight.jems.plugin.contract.models.project.sectionE.lumpsum.ProjectLumpSumData
import io.cloudflight.jems.plugin.contract.models.project.sectionE.lumpsum.ProjectPartnerLumpSumData
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.project.controller.workpackage.extractField
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import io.cloudflight.jems.server.project.service.model.Address
import io.cloudflight.jems.server.project.service.model.Project
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.model.BudgetCosts
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetPeriod
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackageFull
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput

fun Project.toDataModel() = ProjectDataSectionA(
    acronym = acronym,
    title = title.toDataModel(),
    intro = intro.toDataModel(),
    duration = duration,
    specificObjective = specificObjective?.toDataModel(),
    programmePriority = programmePriority?.toDataModel()
)

fun OutputProgrammePriorityPolicySimpleDTO.toDataModel() = ProgrammeSpecificObjectiveData(
    code = code,
    programmeObjectivePolicy = ProgrammeObjectivePolicyData.valueOf(programmeObjectivePolicy.name),
)

fun OutputProgrammePrioritySimple.toDataModel() = ProgrammePriorityDataSimple(
    code = code,
    title = title.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }.toSet()
)

fun OutputProjectDescription.toDataModel(workPackages: List<ProjectWorkPackageData>, results: List<ProjectResultData>) =
    ProjectDataSectionC(
        projectOverallObjective = projectOverallObjective?.toDataModel(),
        projectRelevance = projectRelevance?.toDataModel(),
        projectPartnership = projectPartnership?.toDataModel(),
        projectWorkPackages = workPackages,
        projectResults = results,
        projectManagement = projectManagement?.toDataModel(),
        projectLongTermPlans = projectLongTermPlans?.toDataModel()
    )

fun InputProjectOverallObjective.toDataModel() = ProjectOverallObjectiveData(
    overallObjective = overallObjective.map {
        InputTranslationData(
            SystemLanguageData.valueOf(it.language.name),
            it.translation
        )
    }.toSet()
)

fun InputProjectRelevance.toDataModel() = ProjectRelevanceData(
    territorialChallenge = territorialChallenge.map {
        InputTranslationData(
            SystemLanguageData.valueOf(it.language.name),
            it.translation
        )
    }.toSet(),
    commonChallenge = commonChallenge.map {
        InputTranslationData(
            SystemLanguageData.valueOf(it.language.name),
            it.translation
        )
    }.toSet(),
    transnationalCooperation = transnationalCooperation.map {
        InputTranslationData(
            SystemLanguageData.valueOf(it.language.name),
            it.translation
        )
    }.toSet(),
    projectBenefits = projectBenefits?.map {
        ProjectRelevanceBenefitData(
            group = ProjectTargetGroupData.valueOf(it.group.name),
            specification = it.specification.map {
                InputTranslationData(
                    SystemLanguageData.valueOf(it.language.name),
                    it.translation
                )
            }.toSet()
        )
    }?.toList(),
    projectStrategies = projectStrategies?.map {
        ProjectRelevanceStrategyData(
            strategy = ProgrammeStrategyData.valueOf(it.strategy!!.name),
            specification = it.specification.map {
                InputTranslationData(
                    SystemLanguageData.valueOf(it.language.name),
                    it.translation
                )
            }.toSet()
        )
    }?.toList(),
    projectSynergies = projectSynergies?.map {
        ProjectRelevanceSynergyData(
            synergy = it.synergy.map {
                InputTranslationData(
                    SystemLanguageData.valueOf(it.language.name),
                    it.translation
                )
            }.toSet(),
            specification = it.specification.map {
                InputTranslationData(
                    SystemLanguageData.valueOf(it.language.name),
                    it.translation
                )
            }.toSet(),
        )
    }?.toList(),
    availableKnowledge = availableKnowledge.map {
        InputTranslationData(
            SystemLanguageData.valueOf(it.language.name),
            it.translation
        )
    }.toSet(),
)

fun InputProjectPartnership.toDataModel() = ProjectPartnershipData(
    partnership = partnership.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }
        .toSet(),
)

fun OutputProjectManagement.toDataModel() = ProjectManagementData(
    projectCoordination = projectCoordination?.map {
        InputTranslationData(
            SystemLanguageData.valueOf(it.language.name),
            it.translation
        )
    }?.toSet(),
    projectQualityAssurance = projectQualityAssurance?.map {
        InputTranslationData(
            SystemLanguageData.valueOf(it.language.name),
            it.translation
        )
    }?.toSet(),
    projectCommunication = projectCommunication?.map {
        InputTranslationData(
            SystemLanguageData.valueOf(it.language.name),
            it.translation
        )
    }?.toSet(),
    projectFinancialManagement = projectFinancialManagement?.map {
        InputTranslationData(
            SystemLanguageData.valueOf(it.language.name),
            it.translation
        )
    }?.toSet(),
    projectCooperationCriteria = projectCooperationCriteria?.toDataModel(),
    projectJointDevelopmentDescription = projectJointDevelopmentDescription?.map {
        InputTranslationData(
            SystemLanguageData.valueOf(it.language.name),
            it.translation
        )
    }?.toSet(),
    projectJointImplementationDescription = projectJointImplementationDescription?.map {
        InputTranslationData(
            SystemLanguageData.valueOf(it.language.name),
            it.translation
        )
    }?.toSet(),
    projectJointStaffingDescription = projectJointStaffingDescription?.map {
        InputTranslationData(
            SystemLanguageData.valueOf(
                it.language.name
            ), it.translation
        )
    }?.toSet(),
    projectJointFinancingDescription = projectJointFinancingDescription?.map {
        InputTranslationData(
            SystemLanguageData.valueOf(
                it.language.name
            ), it.translation
        )
    }?.toSet(),
    projectHorizontalPrinciples = projectHorizontalPrinciples?.toDataModel(),
    sustainableDevelopmentDescription = sustainableDevelopmentDescription?.map {
        InputTranslationData(
            SystemLanguageData.valueOf(
                it.language.name
            ), it.translation
        )
    }?.toSet(),
    equalOpportunitiesDescription = equalOpportunitiesDescription?.map {
        InputTranslationData(
            SystemLanguageData.valueOf(
                it.language.name
            ), it.translation
        )
    }?.toSet(),
    sexualEqualityDescription = sexualEqualityDescription?.map {
        InputTranslationData(
            SystemLanguageData.valueOf(it.language.name),
            it.translation
        )
    }?.toSet()
)

fun InputProjectCooperationCriteria.toDataModel() = ProjectCooperationCriteriaData(
    projectJointDevelopment = projectJointDevelopment,
    projectJointImplementation = projectJointImplementation,
    projectJointStaffing = projectJointStaffing,
    projectJointFinancing = projectJointFinancing
)

fun InputProjectHorizontalPrinciples.toDataModel() = ProjectHorizontalPrinciplesData(
    sustainableDevelopmentCriteriaEffect = if (sustainableDevelopmentCriteriaEffect != null) ProjectHorizontalPrinciplesEffectData.valueOf(
        sustainableDevelopmentCriteriaEffect!!.name
    ) else null,
    equalOpportunitiesEffect = if (equalOpportunitiesEffect != null) ProjectHorizontalPrinciplesEffectData.valueOf(
        equalOpportunitiesEffect!!.name
    ) else null,
    sexualEqualityEffect = if (sexualEqualityEffect != null) ProjectHorizontalPrinciplesEffectData.valueOf(
        sexualEqualityEffect!!.name
    ) else null
)

fun OutputProjectLongTermPlans.toDataModel() = ProjectLongTermPlansData(
    projectOwnership = projectOwnership.map {
        InputTranslationData(
            SystemLanguageData.valueOf(it.language.name),
            it.translation
        )
    }.toSet(),
    projectDurability = projectDurability.map {
        InputTranslationData(
            SystemLanguageData.valueOf(it.language.name),
            it.translation
        )
    }.toSet(),
    projectTransferability = projectTransferability.map {
        InputTranslationData(
            SystemLanguageData.valueOf(it.language.name),
            it.translation
        )
    }.toSet()
)

fun List<ProjectWorkPackageFull>.toDataModel() = map {
    ProjectWorkPackageData(
        id = it.id,
        workPackageNumber = it.workPackageNumber,
        name = it.translatedValues.extractField { it.name }.toDataModel(),
        specificObjective = it.translatedValues.extractField { it.specificObjective }.toDataModel(),
        objectiveAndAudience = it.translatedValues.extractField { it.objectiveAndAudience }.toDataModel(),
        activities = it.activities.toActivityDataModel(),
        outputs = it.outputs.toOutputDataModel(),
        investments = it.investments.toInvestmentDataModel()
    )
}.toList()

fun List<WorkPackageActivity>.toActivityDataModel() = map {
    WorkPackageActivityData(
        activityNumber = it.activityNumber,
        translatedValues = it.translatedValues.map {
            WorkPackageActivityTranslatedValueData(
                language = SystemLanguageData.valueOf(
                    it.language.name
                ), title = it.title, description = it.description
            )
        }.toSet(),
        startPeriod = it.startPeriod,
        endPeriod = it.endPeriod,
        deliverables = it.deliverables.toDeliverableDataModel()
    )
}.toList()

fun List<WorkPackageActivityDeliverable>.toDeliverableDataModel() = map {
    WorkPackageActivityDeliverableData(
        deliverableNumber = it.deliverableNumber,
        translatedValues = it.translatedValues.map {
            WorkPackageActivityDeliverableTranslatedValueData(
                language = SystemLanguageData.valueOf(
                    it.language.name
                ), description = it.description
            )
        }.toSet(),
        period = it.period
    )
}.toList()

fun List<WorkPackageOutput>.toOutputDataModel() = map {
    WorkPackageOutputData(
        outputNumber = it.outputNumber,
        programmeOutputIndicatorId = it.programmeOutputIndicatorId,
        programmeOutputIndicatorIdentifier = it.programmeOutputIndicatorIdentifier,
        targetValue = it.targetValue,
        periodNumber = it.periodNumber,
        translatedValues = it.translatedValues.map {
            WorkPackageOutputTranslatedValueData(
                language = SystemLanguageData.valueOf(
                    it.language.name
                ), title = it.title, description = it.description
            )
        }.toSet(),
    )
}.toList()

fun List<WorkPackageInvestment>.toInvestmentDataModel() = map {
    WorkPackageInvestmentData(
        id = it.id,
        investmentNumber = it.investmentNumber,
        address = it.address?.toDataModel(),
        title = it.title.toDataModel(),
        justificationExplanation = it.justificationExplanation.toDataModel(),
        justificationTransactionalRelevance = it.justificationTransactionalRelevance.toDataModel(),
        justificationBenefits = it.justificationBenefits.toDataModel(),
        justificationPilot = it.justificationPilot.toDataModel(),
        risk = it.risk.toDataModel(),
        documentation = it.documentation.toDataModel(),
        ownershipSiteLocation = it.ownershipSiteLocation.toDataModel(),
        ownershipRetain = it.ownershipRetain.toDataModel(),
        ownershipMaintenance = it.ownershipMaintenance.toDataModel()
    )
}.toList()

fun Address.toDataModel() = WorkPackageInvestmentAddressData(
    country = country,
    nutsRegion2 = nutsRegion2,
    nutsRegion3 = nutsRegion3,
    street = street,
    houseNumber = houseNumber,
    postalCode = postalCode,
    city = city
)

fun List<ProjectResult>.toResultDataModel() = map {
    ProjectResultData(
        resultNumber = it.resultNumber,
        programmeResultIndicatorId = it.programmeResultIndicatorId,
        programmeResultIndicatorIdentifier = it.programmeResultIndicatorIdentifier,
        targetValue = it.targetValue,
        periodNumber = it.periodNumber,
        translatedValues = it.translatedValues.map {
            ProjectResultTranslatedValueData(
                language = SystemLanguageData.valueOf(
                    it.language.name
                ), description = it.description
            )
        }.toSet(),
    )
}.toList()

fun ProjectPartnerBudgetOptions.toDataModel() = ProjectPartnerBudgetOptionsData(
    partnerId = partnerId,
    officeAndAdministrationOnStaffCostsFlatRate = officeAndAdministrationOnStaffCostsFlatRate,
    officeAndAdministrationOnDirectCostsFlatRate = officeAndAdministrationOnDirectCostsFlatRate,
    travelAndAccommodationOnStaffCostsFlatRate = travelAndAccommodationOnStaffCostsFlatRate,
    staffCostsFlatRate = staffCostsFlatRate,
    otherCostsOnStaffCostsFlatRate = otherCostsOnStaffCostsFlatRate
)

fun ProjectPartnerCoFinancingAndContribution.toDataModel() = ProjectPartnerCoFinancingAndContributionData(
    finances = finances.map { it.toDataModel() }.toSet(),
    partnerContributions = partnerContributions.map { it.toDataModel() }.toSet(),
    partnerAbbreviation = partnerAbbreviation
)

fun ProjectPartnerCoFinancing.toDataModel() = ProjectPartnerCoFinancingData(
    fundType = ProjectPartnerCoFinancingFundTypeData.valueOf(fundType.name),
    fund = fund?.toDataModel(),
    percentage = percentage
)

fun ProjectPartnerContribution.toDataModel() = ProjectPartnerContributionData(
    id = id,
    name = name,
    status = ProjectPartnerContributionStatusData.valueOf(status!!.name),
    amount = amount,
    isPartner = isPartner
)

fun BudgetCosts.toDataModel() = BudgetCostData(
    staffCosts = staffCosts.map { it.toDataModel() }.toList(),
    travelCosts = travelCosts.map { it.toDataModel() }.toList(),
    externalCosts = externalCosts.map { it.toDataModel() }.toList(),
    equipmentCosts = equipmentCosts.map { it.toDataModel() }.toList(),
    infrastructureCosts = infrastructureCosts.map { it.toDataModel() }.toList(),
    unitCosts = unitCosts.map { it.toDataModel() }.toList()
)

fun BudgetStaffCostEntry.toDataModel() = BudgetStaffCostEntryData(
    id = id,
    numberOfUnits = numberOfUnits,
    rowSum = rowSum,
    budgetPeriods = budgetPeriods.map { it.toDataModel() }.toMutableSet(),
    pricePerUnit = pricePerUnit,
    description = description.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }
        .toSet(),
    comment = comment.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }
        .toSet(),
    unitType = unitType.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }
        .toSet(),
    unitCostId = unitCostId
)

fun BudgetPeriod.toDataModel() = BudgetPeriodData(
    number = number,
    amount = amount
)

fun BudgetTravelAndAccommodationCostEntry.toDataModel() = BudgetTravelAndAccommodationCostEntryData(
    id = id,
    numberOfUnits = numberOfUnits,
    rowSum = rowSum,
    budgetPeriods = budgetPeriods.map { it.toDataModel() }.toMutableSet(),
    pricePerUnit = pricePerUnit,
    description = description.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }
        .toSet(),
    unitType = unitType.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }
        .toSet(),
    unitCostId = unitCostId
)

fun BudgetGeneralCostEntry.toDataModel() = BudgetGeneralCostEntryData(
    id = id,
    numberOfUnits = numberOfUnits,
    rowSum = rowSum,
    budgetPeriods = budgetPeriods.map { it.toDataModel() }.toMutableSet(),
    pricePerUnit = pricePerUnit,
    description = description.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }
        .toSet(),
    unitType = unitType.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }
        .toSet(),
    unitCostId = unitCostId
)

fun BudgetUnitCostEntry.toDataModel() = BudgetUnitCostEntryData(
    id = id,
    numberOfUnits = numberOfUnits,
    rowSum = rowSum,
    budgetPeriods = budgetPeriods.map { it.toDataModel() }.toMutableSet(),
    unitCostId = unitCostId
)

fun OutputProjectPartnerDetail.toDataModel(budget: PartnerBudgetData) = ProjectPartnerData(
    id = id,
    abbreviation = abbreviation,
    role = ProjectPartnerRoleData.valueOf(role.name),
    sortNumber = sortNumber,
    nameInOriginalLanguage = nameInOriginalLanguage,
    nameInEnglish = nameInEnglish,
    department = department.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }
        .toSet(),
    partnerType = if (partnerType != null) ProjectTargetGroupData.valueOf(partnerType!!.name) else null,
    legalStatusId = legalStatusId,
    vat = vat,
    vatRecovery = if (vatRecovery != null) ProjectPartnerVatRecoveryData.valueOf(vatRecovery!!.name) else null,
    addresses = addresses.map { it.toDataModel() }.toList(),
    contacts = contacts.map { it.toDataModel() }.toList(),
    motivation = motivation?.toDataModel(),
    budget = budget
)

fun ProjectPartnerAddressDTO.toDataModel() = ProjectPartnerAddressData(
    type = ProjectPartnerAddressTypeData.valueOf(type.name),
    country = country,
    nutsRegion2 = nutsRegion2,
    nutsRegion3 = nutsRegion3,
    street = street,
    houseNumber = houseNumber,
    postalCode = postalCode,
    city = city,
    homepage = homepage
)

fun OutputProjectPartnerContact.toDataModel() = ProjectPartnerContactData(
    type = ProjectContactTypeData.valueOf(type.name),
    title = title,
    firstName = firstName,
    lastName = lastName,
    email = email,
    telephone = telephone
)

fun ProjectPartnerMotivationDTO.toDataModel() = ProjectPartnerMotivationData(
    organizationRelevance = organizationRelevance.map {
        InputTranslationData(
            SystemLanguageData.valueOf(it.language.name),
            it.translation
        )
    }.toSet(),
    organizationRole = organizationRole.map {
        InputTranslationData(
            SystemLanguageData.valueOf(it.language.name),
            it.translation
        )
    }.toSet(),
    organizationExperience = organizationExperience.map {
        InputTranslationData(
            SystemLanguageData.valueOf(it.language.name),
            it.translation
        )
    }.toSet()
)

fun OutputProjectAssociatedOrganizationDetail.toDataModel() = ProjectAssociatedOrganizationData(
    id = id,
    partner = partner.toDataModel(),
    nameInOriginalLanguage = nameInOriginalLanguage,
    nameInEnglish = nameInEnglish,
    sortNumber = sortNumber,
    address = address?.toDataModel(),
    contacts = contacts.map { it.toDataModel() },
    roleDescription = roleDescription.map {
        InputTranslationData(
            SystemLanguageData.valueOf(it.language.name),
            it.translation
        )
    }.toSet()
)

fun OutputProjectAssociatedOrganizationAddress.toDataModel() = ProjectAssociatedOrganizationAddressData(
    country = country,
    nutsRegion2 = nutsRegion2,
    nutsRegion3 = nutsRegion3,
    street = street,
    houseNumber = houseNumber,
    postalCode = postalCode,
    city = city,
    homepage = homepage
)

fun OutputProjectPartner.toDataModel() = ProjectPartnerEssentialData(
    id = id,
    abbreviation = abbreviation,
    role = ProjectPartnerRoleData.valueOf(role.name),
    sortNumber = sortNumber,
    country = country
)

fun ProjectLumpSum.toDataModel(lumpSumsDetail: List<ProgrammeLumpSum>) = ProjectLumpSumData(
    programmeLumpSum = lumpSumsDetail.firstOrNull { it.id == programmeLumpSumId }?.toDataModel(),
    period = period,
    lumpSumContributions = lumpSumContributions.map { it.toDataModel() }.toList()
)

fun ProgrammeLumpSum.toDataModel() = ProgrammeLumpSumData(
    id = id,
    name = name.toDataModel(),
    description = description.toDataModel(),
    cost = cost,
    splittingAllowed = splittingAllowed,
    phase = phase?.toDataModel(),
    categories = categories.map { it.toDataModel() }.toSet()
)

fun ProgrammeLumpSumPhase.toDataModel() =
    ProgrammeLumpSumPhaseData.valueOf(this.name)

fun BudgetCategory.toDataModel() =
    BudgetCategoryData.valueOf(this.name)


fun ProjectPartnerLumpSum.toDataModel() = ProjectPartnerLumpSumData(
    partnerId = partnerId,
    amount = amount
)

fun Set<InputTranslation>?.toDataModel() =
    this?.map { InputTranslationData(it.language.toDataModel(), it.translation) }?.toSet() ?: emptySet()

fun SystemLanguage.toDataModel() =
    SystemLanguageData.valueOf(this.name)

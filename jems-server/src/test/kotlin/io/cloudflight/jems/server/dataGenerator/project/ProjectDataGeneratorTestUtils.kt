package io.cloudflight.jems.server.dataGenerator.project

import io.cloudflight.jems.api.project.dto.InputProjectData
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.InputProjectCooperationCriteria
import io.cloudflight.jems.api.project.dto.description.InputProjectHorizontalPrinciples
import io.cloudflight.jems.api.project.dto.description.InputProjectLongTermPlans
import io.cloudflight.jems.api.project.dto.description.InputProjectManagement
import io.cloudflight.jems.api.project.dto.description.InputProjectOverallObjective
import io.cloudflight.jems.api.project.dto.description.InputProjectPartnership
import io.cloudflight.jems.api.project.dto.description.InputProjectRelevance
import io.cloudflight.jems.api.project.dto.description.ProjectHorizontalPrinciplesEffect
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import io.cloudflight.jems.api.project.dto.partner.NaceGroupLevelDTO
import io.cloudflight.jems.api.project.dto.partner.PartnerSubTypeDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerVatRecoveryDTO
import io.cloudflight.jems.api.project.dto.result.ProjectResultUpdateRequestDTO
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageCreate
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageUpdate
import io.cloudflight.jems.api.project.dto.workpackage.activity.WorkPackageActivityDTO
import io.cloudflight.jems.api.project.dto.workpackage.investment.WorkPackageInvestmentDTO
import io.cloudflight.jems.api.project.dto.workpackage.output.WorkPackageOutputDTO
import io.cloudflight.jems.server.dataGenerator.PROGRAMME_INPUT_LANGUAGES
import io.cloudflight.jems.server.dataGenerator.PROGRAMME_LEGAL_STATUSES
import io.cloudflight.jems.server.dataGenerator.PROGRAMME_PRIORITY
import io.cloudflight.jems.server.dataGenerator.PROGRAMME_RESULT_INDICATOR
import io.cloudflight.jems.server.project.controller.workpackage.toAddress
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import java.math.BigDecimal

const val FIRST_VERSION: String = "1.0"


fun versionedInputTranslation(postfix: String, version: String) =
    PROGRAMME_INPUT_LANGUAGES.map {
        InputTranslation(
            it.code, "pv=$version - ".plus(it.code.name.plus(" - ").plus(postfix))
        )
    }.toSet()

fun versionedString(prefix: String, version: String) =
    "pv=$version - ".plus(prefix)

fun inputProjectData(version: String, duration: Int) =
    InputProjectData(
        acronym = versionedString("acronym", version),
        specificObjective = PROGRAMME_PRIORITY.specificObjectives.first().programmeObjectivePolicy,
        title = versionedInputTranslation("title", version),
        duration = duration,
        intro = versionedInputTranslation("intro", version),
    )

fun inputProjectOverallObjective(version: String) =
    InputProjectOverallObjective(versionedInputTranslation("overall objective", version))

fun inputProjectRelevance(version: String) =
    InputProjectRelevance(
        territorialChallenge = versionedInputTranslation("territorial challenge", version),
        commonChallenge = versionedInputTranslation("common challenge", version),
        transnationalCooperation = versionedInputTranslation("transnational cooperation", version),
        projectBenefits = emptyList(),
        projectSpfRecipients = emptyList(),
        projectStrategies = emptyList(),
        projectSynergies = emptyList(),
        availableKnowledge = versionedInputTranslation("available knowledge", version)
    )

fun inputProjectPartnership(version: String) =
    InputProjectPartnership(partnership = versionedInputTranslation("partnership", version))

fun inputProjectLongTermPlans(version: String) =
    InputProjectLongTermPlans(
        projectOwnership = versionedInputTranslation("project ownership", version),
        projectDurability = versionedInputTranslation("project durability", version),
        projectTransferability = versionedInputTranslation("project transferability", version)
    )

fun inputProjectManagement(version: String) =
    InputProjectManagement(
        projectCoordination = versionedInputTranslation("project coordination", version),
        projectQualityAssurance = versionedInputTranslation("project quality assurance", version),
        projectCommunication = versionedInputTranslation("project communication", version),
        projectFinancialManagement = versionedInputTranslation("project financial management", version),
        projectCooperationCriteria = InputProjectCooperationCriteria(
            projectJointDevelopment = true,
            projectJointFinancing = false,
            projectJointImplementation = true,
            projectJointStaffing = false
        ),
        projectHorizontalPrinciples = InputProjectHorizontalPrinciples(
            sustainableDevelopmentCriteriaEffect = ProjectHorizontalPrinciplesEffect.PositiveEffects,
            equalOpportunitiesEffect = ProjectHorizontalPrinciplesEffect.Neutral,
            sexualEqualityEffect = ProjectHorizontalPrinciplesEffect.PositiveEffects
        ),
        projectJointDevelopmentDescription = versionedInputTranslation(
            "project joint development description", version
        ),
        projectJointImplementationDescription = versionedInputTranslation(
            "project joint implementation description", version
        ),
        projectJointStaffingDescription = versionedInputTranslation(
            "project joint staffing description", version
        ),
        projectJointFinancingDescription = versionedInputTranslation(
            "project joint financing description", version
        ),
        sustainableDevelopmentDescription = versionedInputTranslation(
            "sustainable development description", version
        ),
        equalOpportunitiesDescription = versionedInputTranslation(
            "equal opportunities description", version
        ),
        sexualEqualityDescription = versionedInputTranslation("sexual equality description", version)
    )

fun projectResultUpdateRequestDTO(version: String) =
    ProjectResultUpdateRequestDTO(
        programmeResultIndicatorId = PROGRAMME_RESULT_INDICATOR.id,
        baseline = BigDecimal.valueOf(34521, 2),
        targetValue = BigDecimal.valueOf(64234, 2),
        periodNumber = 1,
        description = versionedInputTranslation("description", version)
    )

fun inputWorkPackageCreate(version: String) =
    InputWorkPackageCreate(
        name = versionedInputTranslation("name", version),
        specificObjective = versionedInputTranslation("specific objective", version),
        objectiveAndAudience = versionedInputTranslation("objective and audience", version)
    )

fun inputWorkPackageUpdate(id: Long, version: String) =
    InputWorkPackageUpdate(
        id = id,
        name = versionedInputTranslation("name", version),
        specificObjective = versionedInputTranslation("specific objective", version),
        objectiveAndAudience = versionedInputTranslation("objective and audience", version)
    )


fun workPackageActivity(workPackageActivityDto: WorkPackageActivityDTO, workPackageNumber: Int, version: String) =
    WorkPackageActivity(
        id = workPackageActivityDto.id,
        workPackageId = workPackageActivityDto.workPackageId,
        workPackageNumber = workPackageNumber,
        activityNumber = workPackageActivityDto.activityNumber!!,
        title = versionedInputTranslation("title", version),
        description = versionedInputTranslation("description", version),
        startPeriod = workPackageActivityDto.startPeriod,
        endPeriod = workPackageActivityDto.endPeriod,
        partnerIds = workPackageActivityDto.partnerIds,
        deliverables = workPackageActivityDto.deliverables.map { deliverable ->
            WorkPackageActivityDeliverable(
                deliverable.deliverableId, deliverable.deliverableNumber!!,
                description = versionedInputTranslation("description", version),
                title = versionedInputTranslation("title", version),
                period = deliverable.period
            )
        }
    )

fun workPackageOutput(workPackageId: Long, outputDTO: WorkPackageOutputDTO, version: String) =
    WorkPackageOutput(
        workPackageId = workPackageId,
        outputNumber = outputDTO.outputNumber!!,
        programmeOutputIndicatorId = outputDTO.programmeOutputIndicatorId,
        programmeOutputIndicatorIdentifier = outputDTO.programmeOutputIndicatorIdentifier,
        programmeOutputIndicatorName = versionedInputTranslation("programme output indicator name", version),
        programmeOutputIndicatorMeasurementUnit = versionedInputTranslation(
            "programme output indicator measurement unit",
            version
        ),
        targetValue = outputDTO.targetValue,
        periodNumber = outputDTO.periodNumber,
        title = versionedInputTranslation("title", version)
    )

fun workPackageInvestment(investment: WorkPackageInvestmentDTO, version: String) =
    WorkPackageInvestment(
        id = investment.id,
        investmentNumber = investment.investmentNumber,
        title = versionedInputTranslation("title", version),
        expectedDeliveryPeriod = investment.expectedDeliveryPeriod,
        justificationPilot = versionedInputTranslation("justification pilot", version),
        justificationExplanation = versionedInputTranslation("justification explanation", version),
        justificationTransactionalRelevance = versionedInputTranslation(
            "justification transactional relevance", version
        ),
        justificationBenefits = versionedInputTranslation("justification benefits", version),
        address = investment.address?.toAddress(),
        risk = versionedInputTranslation("risk", version),
        documentation = versionedInputTranslation("documentation", version),
        documentationExpectedImpacts = versionedInputTranslation("documentation expected impacts", version),
        ownershipSiteLocation = versionedInputTranslation("ownership site location", version),
        ownershipRetain = versionedInputTranslation("ownership retain", version),
        ownershipMaintenance = versionedInputTranslation("ownership maintenance", version)
    )

fun projectPartnerDTO(
    id: Long? = null,
    version: String, abbreviation: String,
    role: ProjectPartnerRoleDTO = ProjectPartnerRoleDTO.PARTNER
) =
    ProjectPartnerDTO(
        id = id,
        abbreviation = versionedString(abbreviation, version),
        role = role,
        nameInOriginalLanguage = versionedString("original name", version),
        nameInEnglish = versionedString("english name", version),
        department = setOf(),
        partnerType = ProjectTargetGroupDTO.BusinessSupportOrganisation,
        partnerSubType = PartnerSubTypeDTO.MEDIUM_SIZED_ENTERPRISE,
        nace = NaceGroupLevelDTO.A_01,
        otherIdentifierNumber = null,
        otherIdentifierDescription = setOf(),
        pic = null,
        legalStatusId = PROGRAMME_LEGAL_STATUSES.last().id,
        vat = null,
        vatRecovery = ProjectPartnerVatRecoveryDTO.Yes
    )

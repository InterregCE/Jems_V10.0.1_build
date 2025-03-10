package io.cloudflight.jems.server.project.service.report.project.base.createProjectReport

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectHorizontalPrinciplesEffect
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.project.service.ProjectDescriptionPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.contracting.reporting.ContractingReportingPersistence
import io.cloudflight.jems.server.project.service.model.PartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.model.ProjectHorizontalPrinciples
import io.cloudflight.jems.server.project.service.model.ProjectManagement
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.model.ProjectPartnerCostType
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddress
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddressType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.PartnerReportInvestmentSummary
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportUpdate
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.base.create.PreviouslyProjectReportedCoFinancing
import io.cloudflight.jems.server.project.service.report.model.project.base.create.PreviouslyProjectReportedFund
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportBudget
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportCreateModel
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportInvestment
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportPartnerCreateModel
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportResultCreate
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportStatusAndType
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportUnitCostBase
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.ReportCertificateCostCategory
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanStatus
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageActivityCreate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageActivityDeliverableCreate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageCreate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageInvestmentCreate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.create.ProjectReportWorkPackageOutputCreate
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportCreatePersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.identification.ProjectReportIdentificationPersistence
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.ProjectReportResultPrinciplePersistence
import io.cloudflight.jems.server.project.service.report.project.workPlan.ProjectReportWorkPlanPersistence
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackageFull
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import io.cloudflight.jems.server.utils.ERDF_FUND
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.ZonedDateTime

internal class CreateProjectReportTest : UnitTest() {

    companion object {
        private val YESTERDAY = LocalDate.now().minusDays(1)
        private val TOMORROW = LocalDate.now().plusDays(1)

        private fun project(id: Long, status: ApplicationStatus): ProjectFull {
            val statusMock = mockk<ProjectStatus>()
            every { statusMock.status } returns status
            val mock = mockk<ProjectFull>()

            every { mock.id } returns id
            every { mock.acronym } returns "proj-acr"
            every { mock.customIdentifier } returns "proj-custom-iden"
            every { mock.projectStatus } returns statusMock

            return mock
        }

        private fun currentLatestReport(): ProjectReportModel {
            val mock = mockk<ProjectReportModel>()
            every { mock.reportNumber } returns 7
            return mock
        }

        private fun leadPartner(): ProjectPartnerDetail {
            val mock = mockk<ProjectPartnerDetail>()
            every { mock.role } returns ProjectPartnerRole.LEAD_PARTNER
            every { mock.abbreviation } returns "LP abbr"
            every { mock.nameInEnglish } returns "lead-en"
            every { mock.nameInOriginalLanguage } returns "lead-orig"
            every { mock.id } returns 11L
            every { mock.sortNumber } returns 6
            every { mock.addresses } returns listOf(
                ProjectPartnerAddress(ProjectPartnerAddressType.Organization, country = "country-6")
            )
            return mock
        }

        private fun projectResult(): ProjectResult = mockk {
            every { resultNumber } returns 1
            every { periodNumber } returns 2
            every { programmeResultIndicatorId } returns null
            every { baseline } returns BigDecimal.valueOf(3)
            every { targetValue } returns BigDecimal.valueOf(4)
            every { deactivated } returns false
        }

        private val projectManagement = ProjectManagement(
            projectHorizontalPrinciples = ProjectHorizontalPrinciples(
                sustainableDevelopmentCriteriaEffect = ProjectHorizontalPrinciplesEffect.PositiveEffects,
                equalOpportunitiesEffect = ProjectHorizontalPrinciplesEffect.Neutral,
                sexualEqualityEffect = ProjectHorizontalPrinciplesEffect.NegativeEffects,
            ),
            projectCooperationCriteria = null,
        )

        private fun projectReportModel(projectId: Long) = ProjectReportModel(
            id = 0L,
            reportNumber = 8,
            status = ProjectReportStatus.Draft,
            linkedFormVersion = "version",
            startDate = YESTERDAY,
            endDate = TOMORROW,

            deadlineId = null,
            type = ContractingDeadlineType.Both,
            periodNumber = 4,
            reportingDate = YESTERDAY.minusDays(1),
            finalReport = false,

            projectId = projectId,
            projectIdentifier = "proj-custom-iden",
            projectAcronym = "proj-acr",
            leadPartnerNameInOriginalLanguage = "lead-orig",
            leadPartnerNameInEnglish = "lead-en",
            spfPartnerId = null,

            createdAt = ZonedDateTime.now(),
            firstSubmission = null,
            lastReSubmission = mockk(),
            verificationDate = null,
            verificationEndDate = null,
            amountRequested = BigDecimal.ZERO,
            totalEligibleAfterVerification = BigDecimal.ZERO,
            lastVerificationReOpening = null,
            riskBasedVerification = false,
            riskBasedVerificationDescription = null
        )

        private fun expectedProjectReport(projectId: Long) = ProjectReport(
            id = 0L,
            reportNumber = 8,
            status = ProjectReportStatus.Draft,
            linkedFormVersion = "version",
            startDate = YESTERDAY,
            endDate = TOMORROW,

            deadlineId = null,
            type = ContractingDeadlineType.Both,
            periodDetail = ProjectPeriod(4, 17, 22),
            reportingDate = YESTERDAY.minusDays(1),
            finalReport = false,

            projectId = projectId,
            projectIdentifier = "proj-custom-iden",
            projectAcronym = "proj-acr",
            leadPartnerNameInOriginalLanguage = "lead-orig",
            leadPartnerNameInEnglish = "lead-en",

            createdAt = ZonedDateTime.now(),
            firstSubmission = null,
            verificationDate = null,
            verificationEndDate = null,
            verificationLastReOpenDate = null
        )

        private fun projectRelevanceBenefits() = listOf(
            ProjectRelevanceBenefit(
                group = ProjectTargetGroupDTO.Hospitals,
                specification = setOf(
                    InputTranslation(SystemLanguage.EN, "en"),
                    InputTranslation(SystemLanguage.DE, "de")
                )
            ),
            ProjectRelevanceBenefit(
                group = ProjectTargetGroupDTO.CrossBorderLegalBody,
                specification = setOf(
                    InputTranslation(SystemLanguage.EN, "en 2"),
                    InputTranslation(SystemLanguage.DE, "de 2")
                )
            )
        )

        val result = ProjectPartnerBudgetPerFund(
            partner = null,
            budgetPerFund = emptySet(),
            publicContribution = BigDecimal.ZERO,
            autoPublicContribution = BigDecimal.ZERO,
            privateContribution = BigDecimal.ZERO,
            totalPartnerContribution = BigDecimal.ZERO,
            totalEligibleBudget = BigDecimal.ZERO,
            percentageOfTotalEligibleBudget = BigDecimal.ZERO
        )

        val budget = ProjectReportBudget(
            coFinancing = PreviouslyProjectReportedCoFinancing(
                fundsSorted = listOf(
                    PreviouslyProjectReportedFund(
                        fundId = 410L,
                        total = BigDecimal.valueOf(1500),
                        previouslyReported = BigDecimal.valueOf(256),
                        previouslyVerified = BigDecimal.valueOf(104),
                        previouslyPaid = BigDecimal.valueOf(512),
                    ),
                ),
                totalPartner = BigDecimal.valueOf(13),
                totalPublic = BigDecimal.valueOf(14),
                totalAutoPublic = BigDecimal.valueOf(15),
                totalPrivate = BigDecimal.valueOf(16),
                totalSum = BigDecimal.valueOf(17),
                previouslyReportedPartner = BigDecimal.valueOf(33),
                previouslyReportedPublic = BigDecimal.valueOf(34),
                previouslyReportedAutoPublic = BigDecimal.valueOf(35),
                previouslyReportedPrivate = BigDecimal.valueOf(36),
                previouslyReportedSum = BigDecimal.valueOf(37),
                previouslyVerifiedPartner = BigDecimal.valueOf(53),
                previouslyVerifiedPublic = BigDecimal.valueOf(54),
                previouslyVerifiedAutoPublic = BigDecimal.valueOf(55),
                previouslyVerifiedPrivate = BigDecimal.valueOf(56),
                previouslyVerifiedSum = BigDecimal.valueOf(57),
            ),
            costCategorySetup = ReportCertificateCostCategory(
                totalsFromAF = BudgetCostsCalculationResultFull(
                    staff = BigDecimal.valueOf(105),
                    office = BigDecimal.valueOf(115),
                    travel = BigDecimal.valueOf(125),
                    external = BigDecimal.valueOf(135),
                    equipment = BigDecimal.valueOf(145),
                    infrastructure = BigDecimal.valueOf(155),
                    other = BigDecimal.valueOf(165),
                    lumpSum = BigDecimal.valueOf(175),
                    unitCost = BigDecimal.valueOf(185),
                    spfCost = BigDecimal.valueOf(190),
                    sum = BigDecimal.valueOf(195),
                ),
                currentlyReported = BudgetCostsCalculationResultFull(
                    staff = BigDecimal.valueOf(106),
                    office = BigDecimal.valueOf(116),
                    travel = BigDecimal.valueOf(126),
                    external = BigDecimal.valueOf(136),
                    equipment = BigDecimal.valueOf(146),
                    infrastructure = BigDecimal.valueOf(156),
                    other = BigDecimal.valueOf(166),
                    lumpSum = BigDecimal.valueOf(176),
                    unitCost = BigDecimal.valueOf(186),
                    spfCost = BigDecimal.valueOf(191),
                    sum = BigDecimal.valueOf(196),
                ),
                previouslyReported = BudgetCostsCalculationResultFull(
                    staff = BigDecimal.valueOf(107),
                    office = BigDecimal.valueOf(117),
                    travel = BigDecimal.valueOf(127),
                    external = BigDecimal.valueOf(137),
                    equipment = BigDecimal.valueOf(147),
                    infrastructure = BigDecimal.valueOf(157),
                    other = BigDecimal.valueOf(167),
                    lumpSum = BigDecimal.valueOf(177),
                    unitCost = BigDecimal.valueOf(187),
                    spfCost = BigDecimal.valueOf(192),
                    sum = BigDecimal.valueOf(197),
                ),
                currentVerified = BudgetCostsCalculationResultFull(
                    staff = BigDecimal.valueOf(106),
                    office = BigDecimal.valueOf(116),
                    travel = BigDecimal.valueOf(126),
                    external = BigDecimal.valueOf(136),
                    equipment = BigDecimal.valueOf(146),
                    infrastructure = BigDecimal.valueOf(156),
                    other = BigDecimal.valueOf(166),
                    lumpSum = BigDecimal.valueOf(176),
                    unitCost = BigDecimal.valueOf(186),
                    spfCost = BigDecimal.valueOf(191),
                    sum = BigDecimal.valueOf(196),
                ),
                previouslyVerified = BudgetCostsCalculationResultFull(
                    staff = BigDecimal.valueOf(107),
                    office = BigDecimal.valueOf(117),
                    travel = BigDecimal.valueOf(127),
                    external = BigDecimal.valueOf(137),
                    equipment = BigDecimal.valueOf(147),
                    infrastructure = BigDecimal.valueOf(157),
                    other = BigDecimal.valueOf(167),
                    lumpSum = BigDecimal.valueOf(177),
                    unitCost = BigDecimal.valueOf(187),
                    spfCost = BigDecimal.valueOf(192),
                    sum = BigDecimal.valueOf(197),
                )
            ),
            availableLumpSums = listOf(
                ProjectReportLumpSum(
                    lumpSumId = 1L,
                    orderNr = 1,
                    period = 1,
                    total = BigDecimal.TEN,
                    previouslyReported = BigDecimal.ONE,
                    previouslyPaid = BigDecimal.ZERO,
                    previouslyVerified = BigDecimal.ONE,
                )
            ),
            unitCosts = setOf(
                ProjectReportUnitCostBase(
                    unitCostId = 1L,
                    numberOfUnits = BigDecimal.ONE,
                    totalCost = BigDecimal.TEN,
                    previouslyReported = BigDecimal.ONE,
                    previouslyVerified = BigDecimal.ONE,
                )
            ),
            investments = listOf(
                ProjectReportInvestment(
                    investmentId = 1L,
                    investmentNumber = 1,
                    workPackageNumber = 1,
                    title = emptySet(),
                    deactivated = false,
                    total = BigDecimal.TEN,
                    previouslyReported = BigDecimal.ONE,
                    previouslyVerified = BigDecimal.ONE,
                )
            ),
            spfContributionClaims = emptyList(),
            budgetPerPartner = listOf(
                ProjectPartnerBudgetPerFund(
                    ProjectPartnerSummary(
                        id = 11L,
                        sortNumber = 6,
                        abbreviation = "LP abbr",
                        role = ProjectPartnerRole.LEAD_PARTNER,
                        country = "country-6",
                        region = null,
                        currencyCode = null,
                        active = true,
                        institutionName = "BOR"
                    ),
                    costType = ProjectPartnerCostType.Management,
                    budgetPerFund = setOf(
                        PartnerBudgetPerFund(
                            fund = ERDF_FUND.copy(id = 410L),
                            percentage = BigDecimal(80),
                            percentageOfTotal = BigDecimal(100).setScale(2),
                            value = BigDecimal.valueOf(1500)
                        )
                    ),
                    publicContribution = BigDecimal(3),
                    autoPublicContribution = BigDecimal.ZERO,
                    privateContribution = BigDecimal.ZERO,
                    totalPartnerContribution = BigDecimal(3),
                    totalEligibleBudget = BigDecimal(30).setScale(2),
                    percentageOfTotalEligibleBudget = BigDecimal(100).setScale(2, RoundingMode.HALF_UP)
                )
            )
        )

        val workPackage = ProjectWorkPackageFull(
            id = 1L,
            workPackageNumber = 15,
            name = setOf(InputTranslation(SystemLanguage.EN, "name")),
            specificObjective = setOf(InputTranslation(SystemLanguage.EN, "objective")),
            objectiveAndAudience = setOf(InputTranslation(SystemLanguage.EN, "audience")),
            activities = listOf(
                WorkPackageActivity(
                    workPackageId = 1L,
                    activityNumber = 25,
                    title = setOf(InputTranslation(SystemLanguage.EN, "title")),
                    description = setOf(InputTranslation(SystemLanguage.EN, "description")),
                    startPeriod = 4,
                    endPeriod = 6,
                    deliverables = listOf(
                        WorkPackageActivityDeliverable(
                            deliverableNumber = 35,
                            period = 12,
                            deactivated = false,
                        ),
                    ),
                    deactivated = false,
                ),
            ),
            outputs = listOf(
                WorkPackageOutput(
                    workPackageId = 1L,
                    outputNumber = 16,
                    programmeOutputIndicatorId = 458L,
                    programmeOutputIndicatorIdentifier = "id",
                    targetValue = BigDecimal.TEN,
                    periodNumber = 16,
                    title = setOf(InputTranslation(SystemLanguage.EN, "title-out")),
                    description = setOf(InputTranslation(SystemLanguage.EN, "description-out")),
                    programmeOutputIndicatorName = setOf(
                        InputTranslation(
                            SystemLanguage.EN,
                            "programmeOutputIndicatorName"
                        )
                    ),
                    programmeOutputIndicatorMeasurementUnit = setOf(InputTranslation(SystemLanguage.EN, "measure")),
                    periodStartMonth = 7,
                    periodEndMonth = 9,
                    deactivated = false,
                ),
            ),
            investments = listOf(
                WorkPackageInvestment(
                    id = 744L,
                    investmentNumber = 14,
                    title = setOf(InputTranslation(SystemLanguage.EN, "investment-14")),
                    deactivated = false,
                    address = null,
                ),
            ),
            deactivated = false,
        )

        val report11wp = ProjectReportWorkPackage(
            id = 777L,
            number = 15,
            deactivated = false,
            specificObjective = emptySet(),
            specificStatus = ProjectReportWorkPlanStatus.Fully,
            specificExplanation = emptySet(),
            communicationObjective = emptySet(),
            communicationStatus = ProjectReportWorkPlanStatus.Partly,
            communicationExplanation = emptySet(),
            completed = true,
            description = emptySet(),
            activities = listOf(
                ProjectReportWorkPackageActivity(
                    id = 778L,
                    number = 25,
                    title = emptySet(),
                    deactivated = false,
                    startPeriod = null,
                    endPeriod = null,
                    status = ProjectReportWorkPlanStatus.Not,
                    progress = emptySet(),
                    attachment = null,
                    deliverables = emptyList(),
                    previousStatus = ProjectReportWorkPlanStatus.Not,
                    previousProgress = emptySet()
                )
            ),
            outputs = emptyList(),
            investments = emptyList(),
            previousCommunicationStatus = ProjectReportWorkPlanStatus.Partly,
            previousCompleted = true,
            previousSpecificStatus = ProjectReportWorkPlanStatus.Fully,
            previousSpecificExplanation = emptySet(),
            previousCommunicationExplanation = emptySet(),
            previousDescription = emptySet()
        )

        fun expectedToCreateModel(projectId: Long, created: ZonedDateTime) = ProjectReportCreateModel(
            reportBase = ProjectReportModel(
                id = 0L,
                reportNumber = 8,
                status = ProjectReportStatus.Draft,
                linkedFormVersion = "version",
                startDate = YESTERDAY,
                endDate = TOMORROW,
                deadlineId = null,
                type = ContractingDeadlineType.Both,
                finalReport = false,
                periodNumber = 4,
                reportingDate = YESTERDAY.minusDays(1),
                projectId = projectId,
                projectIdentifier = "proj-custom-iden",
                projectAcronym = "proj-acr",
                leadPartnerNameInOriginalLanguage = "lead-orig",
                leadPartnerNameInEnglish = "lead-en",
                spfPartnerId = null,
                createdAt = created,
                firstSubmission = null,
                lastReSubmission = null,
                verificationDate = null,
                verificationEndDate = null,
                amountRequested = BigDecimal.ZERO,
                totalEligibleAfterVerification = BigDecimal.ZERO,
                lastVerificationReOpening = null,
                riskBasedVerification = false,
                riskBasedVerificationDescription = null
            ),
            reportBudget = ProjectReportBudget(
                coFinancing = PreviouslyProjectReportedCoFinancing(
                    fundsSorted = listOf(
                        PreviouslyProjectReportedFund(
                            fundId = 410L,
                            total = BigDecimal.valueOf(1500),
                            previouslyReported = BigDecimal.valueOf(256),
                            previouslyVerified = BigDecimal.valueOf(104),
                            previouslyPaid = BigDecimal.valueOf(512),
                        ),
                    ),
                    totalPartner = BigDecimal.valueOf(13),
                    totalPublic = BigDecimal.valueOf(14),
                    totalAutoPublic = BigDecimal.valueOf(15),
                    totalPrivate = BigDecimal.valueOf(16),
                    totalSum = BigDecimal.valueOf(17),
                    previouslyReportedPartner = BigDecimal.valueOf(33),
                    previouslyReportedPublic = BigDecimal.valueOf(34),
                    previouslyReportedAutoPublic = BigDecimal.valueOf(35),
                    previouslyReportedPrivate = BigDecimal.valueOf(36),
                    previouslyReportedSum = BigDecimal.valueOf(37),
                    previouslyVerifiedPartner = BigDecimal.valueOf(53),
                    previouslyVerifiedPublic = BigDecimal.valueOf(54),
                    previouslyVerifiedAutoPublic = BigDecimal.valueOf(55),
                    previouslyVerifiedPrivate = BigDecimal.valueOf(56),
                    previouslyVerifiedSum = BigDecimal.valueOf(57),
                ),
                costCategorySetup = ReportCertificateCostCategory(
                    totalsFromAF = BudgetCostsCalculationResultFull(
                        staff = BigDecimal.valueOf(105),
                        office = BigDecimal.valueOf(115),
                        travel = BigDecimal.valueOf(125),
                        external = BigDecimal.valueOf(135),
                        equipment = BigDecimal.valueOf(145),
                        infrastructure = BigDecimal.valueOf(155),
                        other = BigDecimal.valueOf(165),
                        lumpSum = BigDecimal.valueOf(175),
                        unitCost = BigDecimal.valueOf(185),
                        spfCost = BigDecimal.valueOf(190),
                        sum = BigDecimal.valueOf(195),
                    ),
                    currentlyReported = BudgetCostsCalculationResultFull(
                        staff = BigDecimal.valueOf(106),
                        office = BigDecimal.valueOf(116),
                        travel = BigDecimal.valueOf(126),
                        external = BigDecimal.valueOf(136),
                        equipment = BigDecimal.valueOf(146),
                        infrastructure = BigDecimal.valueOf(156),
                        other = BigDecimal.valueOf(166),
                        lumpSum = BigDecimal.valueOf(176),
                        unitCost = BigDecimal.valueOf(186),
                        spfCost = BigDecimal.valueOf(191),
                        sum = BigDecimal.valueOf(196),
                    ),
                    previouslyReported = BudgetCostsCalculationResultFull(
                        staff = BigDecimal.valueOf(107),
                        office = BigDecimal.valueOf(117),
                        travel = BigDecimal.valueOf(127),
                        external = BigDecimal.valueOf(137),
                        equipment = BigDecimal.valueOf(147),
                        infrastructure = BigDecimal.valueOf(157),
                        other = BigDecimal.valueOf(167),
                        lumpSum = BigDecimal.valueOf(177),
                        unitCost = BigDecimal.valueOf(187),
                        spfCost = BigDecimal.valueOf(192),
                        sum = BigDecimal.valueOf(197),
                    ),
                    currentVerified = BudgetCostsCalculationResultFull(
                        staff = BigDecimal.valueOf(106),
                        office = BigDecimal.valueOf(116),
                        travel = BigDecimal.valueOf(126),
                        external = BigDecimal.valueOf(136),
                        equipment = BigDecimal.valueOf(146),
                        infrastructure = BigDecimal.valueOf(156),
                        other = BigDecimal.valueOf(166),
                        lumpSum = BigDecimal.valueOf(176),
                        unitCost = BigDecimal.valueOf(186),
                        spfCost = BigDecimal.valueOf(191),
                        sum = BigDecimal.valueOf(196),
                    ),
                    previouslyVerified = BudgetCostsCalculationResultFull(
                        staff = BigDecimal.valueOf(107),
                        office = BigDecimal.valueOf(117),
                        travel = BigDecimal.valueOf(127),
                        external = BigDecimal.valueOf(137),
                        equipment = BigDecimal.valueOf(147),
                        infrastructure = BigDecimal.valueOf(157),
                        other = BigDecimal.valueOf(167),
                        lumpSum = BigDecimal.valueOf(177),
                        unitCost = BigDecimal.valueOf(187),
                        spfCost = BigDecimal.valueOf(192),
                        sum = BigDecimal.valueOf(197),
                    )
                ),
                availableLumpSums = listOf(
                    ProjectReportLumpSum(
                        lumpSumId = 1L,
                        orderNr = 1,
                        period = 1,
                        total = BigDecimal.TEN,
                        previouslyReported = BigDecimal.ONE,
                        previouslyPaid = BigDecimal.ZERO,
                        previouslyVerified = BigDecimal.ONE,
                    )
                ),
                unitCosts = setOf(
                    ProjectReportUnitCostBase(
                        unitCostId = 1L,
                        numberOfUnits = BigDecimal.ONE,
                        totalCost = BigDecimal.TEN,
                        previouslyReported = BigDecimal.ONE,
                        previouslyVerified = BigDecimal.ONE,
                    )
                ),
                investments = listOf(
                    ProjectReportInvestment(
                        investmentId = 1L,
                        investmentNumber = 1,
                        workPackageNumber = 1,
                        title = emptySet(),
                        deactivated = false,
                        total = BigDecimal.TEN,
                        previouslyReported = BigDecimal.ONE,
                        previouslyVerified = BigDecimal.ONE,
                    )
                ),
                spfContributionClaims = emptyList(),
                budgetPerPartner = listOf(
                    ProjectPartnerBudgetPerFund(
                        partner = ProjectPartnerSummary(
                            id = 11L,
                            sortNumber = 6,
                            abbreviation = "LP abbr",
                            role = ProjectPartnerRole.LEAD_PARTNER,
                            country = "country-6",
                            region = null,
                            currencyCode = null,
                            active = true,
                            institutionName = "BOR"
                        ),
                        costType = ProjectPartnerCostType.Management,
                        budgetPerFund = setOf(
                            PartnerBudgetPerFund(
                                fund = ERDF_FUND.copy(id = 410L),
                                percentage = BigDecimal(80),
                                percentageOfTotal = BigDecimal(100).setScale(2),
                                value = BigDecimal.valueOf(1500)
                            )
                        ),
                        publicContribution = BigDecimal(3),
                        autoPublicContribution = BigDecimal.ZERO,
                        privateContribution = BigDecimal.ZERO,
                        totalPartnerContribution = BigDecimal(3),
                        totalEligibleBudget = BigDecimal(30).setScale(2),
                        percentageOfTotalEligibleBudget = BigDecimal(100).setScale(2, RoundingMode.HALF_UP)
                    )
                )
            ),
            workPackages = listOf(
                ProjectReportWorkPackageCreate(
                    workPackageId = 1L,
                    number = 15,
                    deactivated = false,
                    specificObjective = setOf(InputTranslation(SystemLanguage.EN, "objective")),
                    specificStatus = ProjectReportWorkPlanStatus.Fully,
                    communicationObjective = setOf(InputTranslation(SystemLanguage.EN, "audience")),
                    communicationStatus = ProjectReportWorkPlanStatus.Partly,
                    completed = true,
                    activities = listOf(
                        ProjectReportWorkPackageActivityCreate(
                            activityId = 0L,
                            number = 25,
                            title = setOf(InputTranslation(SystemLanguage.EN, "title")),
                            deactivated = false,
                            startPeriodNumber = 4,
                            endPeriodNumber = 6,
                            status = ProjectReportWorkPlanStatus.Not,
                            deliverables = listOf(
                                ProjectReportWorkPackageActivityDeliverableCreate(
                                    deliverableId = 0L,
                                    number = 35,
                                    title = emptySet(),
                                    deactivated = false,
                                    periodNumber = 12,
                                    previouslyReported = BigDecimal.valueOf(502),
                                    previousCurrentReport = BigDecimal.ZERO,
                                    previousProgress = emptySet(),
                                    progress = emptySet(),
                                    currentReport = BigDecimal.ZERO
                                ),
                            ),
                            previousProgress = emptySet(),
                            progress = emptySet(),
                            previousStatus = ProjectReportWorkPlanStatus.Not,
                        ),
                    ),
                    outputs = listOf(
                        ProjectReportWorkPackageOutputCreate(
                            number = 16,
                            title = setOf(InputTranslation(SystemLanguage.EN, "title-out")),
                            deactivated = false,
                            programmeOutputIndicatorId = 458L,
                            periodNumber = 16,
                            targetValue = BigDecimal.valueOf(10),
                            previouslyReported = BigDecimal.valueOf(845),
                            previousCurrentReport = BigDecimal.ZERO,
                            previousProgress = emptySet(),
                            progress = emptySet(),
                            currentReport = BigDecimal.ZERO
                        ),
                    ),
                    investments = listOf(
                        ProjectReportWorkPackageInvestmentCreate(
                            investmentId = 744L,
                            number = 14,
                            title = setOf(InputTranslation(SystemLanguage.EN, "investment-14")),
                            expectedDeliveryPeriod = null,
                            justificationExplanation = emptySet(),
                            justificationTransactionalRelevance = emptySet(),
                            justificationBenefits = emptySet(),
                            justificationPilot = emptySet(),
                            address = null,
                            risk = emptySet(),
                            documentation = emptySet(),
                            documentationExpectedImpacts = emptySet(),
                            ownershipSiteLocation = emptySet(),
                            ownershipRetain = emptySet(),
                            ownershipMaintenance = emptySet(),
                            deactivated = false,
                            previousProgress = emptySet(),
                            progress = emptySet(),
                            status = null
                        )
                    ),
                    previousCommunicationStatus = ProjectReportWorkPlanStatus.Partly,
                    previousCompleted = true,
                    previousSpecificStatus = ProjectReportWorkPlanStatus.Fully,
                    previousSpecificExplanation = emptySet(),
                    previousCommunicationExplanation = emptySet(),
                    specificExplanation = emptySet(),
                    communicationExplanation = emptySet(),
                    description = emptySet(),
                    previousDescription = emptySet(),
                ),
            ),
            targetGroups = listOf(
                ProjectRelevanceBenefit(
                    group = ProjectTargetGroupDTO.Hospitals,
                    specification = setOf(
                        InputTranslation(SystemLanguage.EN, "en"),
                        InputTranslation(SystemLanguage.DE, "de")
                    ),
                ),
                ProjectRelevanceBenefit(
                    group = ProjectTargetGroupDTO.CrossBorderLegalBody,
                    specification = setOf(
                        InputTranslation(SystemLanguage.EN, "en 2"),
                        InputTranslation(SystemLanguage.DE, "de 2")
                    ),
                ),
            ),
            partners = listOf(
                ProjectReportPartnerCreateModel(
                    partnerId = 11L,
                    partnerNumber = 6,
                    partnerAbbreviation = "LP abbr",
                    partnerRole = ProjectPartnerRole.LEAD_PARTNER,
                    country = "country-6",
                    previouslyReported = BigDecimal.valueOf(83L, 1),
                    partnerTotalEligibleBudget = BigDecimal.valueOf(3000, 2)
                )
            ),
            results = listOf(
                ProjectReportResultCreate(
                    resultNumber = 1,
                    deactivated = false,
                    periodNumber = 2,
                    programmeResultIndicatorId = null,
                    baseline = BigDecimal.valueOf(3),
                    targetValue = BigDecimal.valueOf(4),
                    previouslyReported = BigDecimal.valueOf(15L, 2),
                ),
            ),
            horizontalPrinciples = ProjectHorizontalPrinciples(
                sustainableDevelopmentCriteriaEffect = ProjectHorizontalPrinciplesEffect.PositiveEffects,
                equalOpportunitiesEffect = ProjectHorizontalPrinciplesEffect.Neutral,
                sexualEqualityEffect = ProjectHorizontalPrinciplesEffect.NegativeEffects,
            ),
        )
    }

    @MockK
    private lateinit var versionPersistence: ProjectVersionPersistence

    @MockK
    private lateinit var projectPersistence: ProjectPersistence

    @MockK
    private lateinit var projectPartnerPersistence: PartnerPersistence

    @MockK
    private lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    private lateinit var reportCreatePersistence: ProjectReportCreatePersistence

    @MockK
    private lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    private lateinit var projectWorkPackagePersistence: WorkPackagePersistence

    @MockK
    private lateinit var projectDescriptionPersistence: ProjectDescriptionPersistence

    @MockK
    private lateinit var projectReportIdentificationPersistence: ProjectReportIdentificationPersistence

    @MockK
    private lateinit var createProjectReportBudget: CreateProjectReportBudget

    @MockK
    private lateinit var projectResultPersistence: ProjectResultPersistence

    @MockK
    private lateinit var projectReportResultPersistence: ProjectReportResultPrinciplePersistence

    @MockK
    private lateinit var workPlanPersistence: ProjectReportWorkPlanPersistence

    @MockK
    private lateinit var deadlinePersistence: ContractingReportingPersistence

    @MockK
    private lateinit var callPersistence: CallPersistence

    @InjectMockKs
    lateinit var interactor: CreateProjectReport

    @BeforeEach
    fun reset() {
        clearMocks(
            versionPersistence,
            projectPersistence,
            projectPartnerPersistence,
            reportPersistence,
            reportCreatePersistence,
            auditPublisher,
            projectWorkPackagePersistence,
            projectReportIdentificationPersistence,
            createProjectReportBudget,
            projectResultPersistence,
            projectReportResultPersistence,
            deadlinePersistence,
        )
    }

    @ParameterizedTest(name = "createReportFor {0}")
    @EnumSource(
        value = ApplicationStatus::class,
        names = ["CONTRACTED", "IN_MODIFICATION", "MODIFICATION_SUBMITTED", "MODIFICATION_REJECTED", "CLOSED"]
    )
    fun createReportFor(status: ApplicationStatus) {
        val projectId = 54L + status.ordinal
        every { reportPersistence.countForProject(projectId) } returns 1
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns "version"
        every { projectPersistence.getProject(projectId, "version") } returns project(projectId, status)
        every { projectPersistence.getProjectPeriods(projectId, "version") } returns listOf(ProjectPeriod(4, 17, 22))
        every {
            reportPersistence.existsHavingTypeAndStatusIn(
                projectId, ContractingDeadlineType.Both,
                setOf(ProjectReportStatus.ReOpenSubmittedLast, ProjectReportStatus.VerificationReOpenedLast)
            )
        } returns emptyList()
        every { reportPersistence.getCurrentLatestReportFor(projectId) } returns currentLatestReport()
        every { projectPartnerPersistence.findTop50ByProjectId(projectId, "version") } returns listOf(leadPartner())
        every { projectDescriptionPersistence.getBenefits(projectId, "version") } returns projectRelevanceBenefits()
        val reports = listOf(
            ProjectReportStatusAndType(11L, ProjectReportStatus.Submitted, ContractingDeadlineType.Both),
        )
        every { reportPersistence.getSubmittedProjectReports(projectId) } returns reports
        every {
            projectWorkPackagePersistence.getWorkPackagesWithAllDataByProjectId(
                projectId,
                "version"
            )
        } returns listOf(workPackage)
        every { projectReportIdentificationPersistence.getSpendingProfileCumulative(setOf(11L)) } returns mapOf(
            11L to BigDecimal.valueOf(
                83L,
                1
            )
        )
        val slotInvestments = slot<List<PartnerReportInvestmentSummary>>()
        every {
            createProjectReportBudget.retrieveBudgetDataFor(
                projectId,
                "version",
                capture(slotInvestments),
                reports
            )
        } returns budget
        every { workPlanPersistence.getDeliverableCumulative(setOf(11L)) } returns
                mapOf(15 to mapOf(25 to mapOf(35 to BigDecimal.valueOf(502))))
        every { workPlanPersistence.getOutputCumulative(setOf(11L)) } returns
                mapOf(15 to mapOf(16 to BigDecimal.valueOf(845)))
        every { workPlanPersistence.getReportWorkPlanById(projectId, reportId = 11L) } returns listOf(report11wp)
        every { projectResultPersistence.getResultsForProject(projectId, "version") } returns listOf(projectResult())
        every { projectDescriptionPersistence.getProjectManagement(projectId, "version") } returns projectManagement
        every { callPersistence.getCallByProjectId(projectId) } returns mockk {
            every { isSpf() } returns false
        }
        every { projectReportResultPersistence.getResultCumulative(setOf(11L)) } returns mapOf(
            1 to BigDecimal.valueOf(
                15L,
                2
            )
        )

        val reportStored = slot<ProjectReportCreateModel>()
        every { reportCreatePersistence.createReportAndFillItToEmptyCertificates(capture(reportStored)) } returns projectReportModel(
            projectId
        )

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}

        val data = ProjectReportUpdate(
            startDate = YESTERDAY,
            endDate = TOMORROW,
            deadlineId = null,
            type = ContractingDeadlineType.Both,
            periodNumber = 4,
            reportingDate = YESTERDAY.minusDays(1),
            finalReport = false,
        )
        val returned = interactor.createReportFor(projectId, data)
        assertThat(returned).isEqualTo(
            expectedProjectReport(projectId).copy(createdAt = returned.createdAt)
        )
        assertThat(slotInvestments.captured).containsExactly(
            PartnerReportInvestmentSummary(
                744L,
                14,
                15,
                setOf(InputTranslation(SystemLanguage.EN, "investment-14")),
                false
            )
        )
        assertThat(reportStored.captured).isEqualTo(
            expectedToCreateModel(
                projectId,
                created = reportStored.captured.reportBase.createdAt
            )
        )

        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_REPORT_ADDED,
                project = AuditProject(projectId.toString(), "proj-custom-iden", "proj-acr"),
                entityRelatedId = 0L,
                description = "[proj-custom-iden] Project report PR.8 added",
            )
        )
    }

    @ParameterizedTest(name = "createReportFor spf but no partner {0}")
    @EnumSource(
        value = ApplicationStatus::class,
        names = ["CONTRACTED", "IN_MODIFICATION", "MODIFICATION_SUBMITTED", "MODIFICATION_REJECTED", "CLOSED"]
    )
    fun `createReportFor spf but no partner`(status: ApplicationStatus) {
        val projectId = 454L + status.ordinal
        every { reportPersistence.countForProject(projectId) } returns 1
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns "version"
        every { projectPersistence.getProject(projectId, "version") } returns project(projectId, status)
        every { projectPersistence.getProjectPeriods(projectId, "version") } returns listOf(ProjectPeriod(4, 17, 22))
        every {
            reportPersistence.existsHavingTypeAndStatusIn(
                projectId, ContractingDeadlineType.Both,
                setOf(ProjectReportStatus.ReOpenSubmittedLast, ProjectReportStatus.VerificationReOpenedLast)
            )
        } returns emptyList()
        every { reportPersistence.getCurrentLatestReportFor(projectId) } returns currentLatestReport()
        every { projectPartnerPersistence.findTop50ByProjectId(projectId, "version") } returns emptyList()
        every { projectDescriptionPersistence.getBenefits(projectId, "version") } returns null
        every { reportPersistence.getSubmittedProjectReports(projectId) } returns emptyList()
        every {
            projectWorkPackagePersistence.getWorkPackagesWithAllDataByProjectId(projectId, "version")
        } returns emptyList()
        every { projectReportIdentificationPersistence.getSpendingProfileCumulative(emptySet()) } returns emptyMap()

        every { projectResultPersistence.getResultsForProject(projectId, "version") } returns emptyList()
        every { projectDescriptionPersistence.getProjectManagement(projectId, "version") } returns null
        every { projectReportResultPersistence.getResultCumulative(emptySet()) } returns emptyMap()
        every { callPersistence.getCallByProjectId(projectId) } returns mockk {
            every { isSpf() } returns true
        }

        every { createProjectReportBudget.retrieveBudgetDataFor(projectId, version = "version", emptyList(), emptyList()) } returns mockk()

        val data = ProjectReportUpdate(
            startDate = YESTERDAY,
            endDate = TOMORROW,
            deadlineId = null,
            type = ContractingDeadlineType.Both,
            periodNumber = 4,
            reportingDate = YESTERDAY.minusDays(1),
            finalReport = false,
        )
        assertThrows<NoPartnerForSpfProject> { interactor.createReportFor(projectId, data) }
    }

    @ParameterizedTest(name = "createReportFor - forbidden because other reopened {0}")
    @EnumSource(
        value = ApplicationStatus::class,
        names = ["CONTRACTED", "IN_MODIFICATION", "MODIFICATION_SUBMITTED", "MODIFICATION_REJECTED", "CLOSED"],
    )
    fun `createReportFor - forbidden because other reopened`(status: ApplicationStatus) {
        val projectId = 354L + status.ordinal
        every { reportPersistence.countForProject(projectId) } returns 1
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns "version"
        every { projectPersistence.getProject(projectId, "version") } returns project(projectId, status)
        every { projectPersistence.getProjectPeriods(projectId, "version") } returns listOf(ProjectPeriod(4, 17, 22))
        every {
            reportPersistence.existsHavingTypeAndStatusIn(
                projectId, ContractingDeadlineType.Both,
                setOf(ProjectReportStatus.ReOpenSubmittedLast, ProjectReportStatus.VerificationReOpenedLast)
            )
        } returns listOf(1966, 1985)

        val data = ProjectReportUpdate(
            startDate = YESTERDAY,
            endDate = TOMORROW,
            deadlineId = null,
            type = ContractingDeadlineType.Both,
            periodNumber = 4,
            reportingDate = YESTERDAY.minusDays(1),
            finalReport = false,
        )
        val ex = assertThrows<LastReOpenedReportException> { interactor.createReportFor(projectId, data) }
        assertThat(ex.i18nMessage.i18nArguments).containsEntry("blockingReportNumbers", "PR.1966, PR.1985")
        assertThat(ex.i18nMessage.i18nKey).isEqualTo("use.case.create.project.report.reopened.report.exists")
    }

    @ParameterizedTest(name = "createReportFor - not contracted {0}")
    @EnumSource(
        value = ApplicationStatus::class,
        names = ["CONTRACTED", "IN_MODIFICATION", "MODIFICATION_SUBMITTED", "MODIFICATION_REJECTED", "CLOSED"],
        mode = EnumSource.Mode.EXCLUDE,
    )
    fun `createReportFor - not contracted`(status: ApplicationStatus) {
        val projectId = 154L + status.ordinal
        every { reportPersistence.countForProject(projectId) } returns 1
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns "version"
        every { projectPersistence.getProject(projectId, "version") } returns project(projectId, status)

        assertThrows<ReportCanBeCreatedOnlyWhenContractedException> { interactor.createReportFor(projectId, mockk()) }
    }

    @Test
    fun `createReportFor - max amounts of reports reached`() {
        val projectId = 254L
        every { reportPersistence.countForProject(projectId) } returns 100
        assertThrows<MaxAmountOfReportsReachedException> { interactor.createReportFor(projectId, mockk()) }
    }
}

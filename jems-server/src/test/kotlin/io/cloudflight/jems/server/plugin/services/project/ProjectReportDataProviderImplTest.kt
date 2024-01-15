package io.cloudflight.jems.server.plugin.services.project

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectHorizontalPrinciplesEffect
import io.cloudflight.jems.plugin.contract.models.common.InputTranslationData
import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.plugin.contract.models.project.sectionA.ProjectPeriodData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerRoleData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.management.ProjectHorizontalPrinciplesData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.management.ProjectHorizontalPrinciplesEffectData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.relevance.ProjectTargetGroupData
import io.cloudflight.jems.plugin.contract.models.report.partner.identification.ProjectPartnerReportPeriodData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.BudgetCostsCalculationResultFullData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateCoFinancingBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateCoFinancingBreakdownLineData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateCostCategoryBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateCostCategoryBreakdownLineData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateCostCategoryBreakdownLinePerPartnerData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateCostCategoryBreakdownPerPartnerData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateInvestmentBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateInvestmentBreakdownLineData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateLumpSumBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateLumpSumBreakdownLineData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateUnitCostBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateUnitCostBreakdownLineData
import io.cloudflight.jems.plugin.contract.models.report.project.identification.ContractingDeadlineTypeData
import io.cloudflight.jems.plugin.contract.models.report.project.identification.ProjectReportBaseData
import io.cloudflight.jems.plugin.contract.models.report.project.identification.ProjectReportData
import io.cloudflight.jems.plugin.contract.models.report.project.identification.ProjectReportIdentificationData
import io.cloudflight.jems.plugin.contract.models.report.project.identification.ProjectReportIdentificationTargetGroupData
import io.cloudflight.jems.plugin.contract.models.report.project.identification.ProjectReportPeriodData
import io.cloudflight.jems.plugin.contract.models.report.project.identification.ProjectReportSpendingProfileData
import io.cloudflight.jems.plugin.contract.models.report.project.identification.ProjectReportStatusData
import io.cloudflight.jems.plugin.contract.models.report.project.partnerCertificates.PartnerReportCertificateData
import io.cloudflight.jems.plugin.contract.models.report.project.projectResults.ProjectReportResultData
import io.cloudflight.jems.plugin.contract.models.report.project.projectResults.ProjectReportResultPrincipleData
import io.cloudflight.jems.plugin.contract.models.report.project.workPlan.ProjectReportWorkPackageActivityData
import io.cloudflight.jems.plugin.contract.models.report.project.workPlan.ProjectReportWorkPackageData
import io.cloudflight.jems.plugin.contract.models.report.project.workPlan.ProjectReportWorkPackageOutputData
import io.cloudflight.jems.plugin.contract.models.report.project.workPlan.ProjectReportWorkPackageOutputIndicatorSummaryData
import io.cloudflight.jems.plugin.contract.models.report.project.workPlan.ProjectReportWorkPackageStatusData
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.plugin.services.report.ProjectReportDataProviderImpl
import io.cloudflight.jems.server.plugin.services.report.toDataModel
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorSummary
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.model.ProjectHorizontalPrinciples
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import io.cloudflight.jems.server.project.service.partner.model.PartnerSubType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.certificate.PartnerReportCertificate
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.CertificateCoFinancingBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.CertificateCoFinancingBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.investment.CertificateInvestmentBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.investment.CertificateInvestmentBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.lumpSum.CertificateLumpSumBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.lumpSum.CertificateLumpSumBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner.PerPartnerCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.unitCost.CertificateUnitCostBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.unitCost.CertificateUnitCostBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentification
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentificationTargetGroup
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportSpendingProfile
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportProjectResult
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrinciple
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanStatus
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCertificateInvestmentsBreakdownInteractor.GetReportCertificateInvestmentCalculatorService
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCoFinancingBreakdown.GetReportCertificateCoFinancingBreakdownCalculator
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCostCategoryBreakdown.GetReportCertificateCostCategoryBreakdownCalculator
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportLumpSumBreakdown.GetReportCertificateLumpSumBreakdownCalculator
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportUnitCostBreakdown.GetReportCertificateUnitCostCalculatorService
import io.cloudflight.jems.server.project.service.report.project.financialOverview.perPartner.sumOf
import io.cloudflight.jems.server.project.service.report.project.identification.ProjectReportIdentificationPersistence
import io.cloudflight.jems.server.project.service.report.project.identification.getProjectReportIdentification.GetProjectReportIdentification
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.ProjectReportResultPrinciplePersistence
import io.cloudflight.jems.server.project.service.report.project.workPlan.ProjectReportWorkPlanPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class ProjectReportDataProviderImplTest : UnitTest() {

    @MockK
    private lateinit var projectReportPersistence: ProjectReportPersistence

    @MockK
    private lateinit var projectPersistence: ProjectPersistence

    @MockK
    private lateinit var getIdentificationPersistence: ProjectReportIdentificationPersistence

    @MockK
    private lateinit var getIdentificationService: GetProjectReportIdentification

    @MockK
    private lateinit var reportWorkPlanPersistence: ProjectReportWorkPlanPersistence

    @MockK
    private lateinit var projectReportResultPrinciplePersistence: ProjectReportResultPrinciplePersistence

    @MockK
    private lateinit var projectReportCertificatePersistence: ProjectReportCertificatePersistence

    @MockK
    private lateinit var partnerPersistence: PartnerPersistence

    @MockK
    private lateinit var getReportCertificateCoFinancingBreakdownCalculator: GetReportCertificateCoFinancingBreakdownCalculator

    @MockK
    private lateinit var getReportCertificateLumpSumBreakdownCalculator: GetReportCertificateLumpSumBreakdownCalculator

    @MockK
    private lateinit var getReportCertificateCostCategoryBreakdownCalculator: GetReportCertificateCostCategoryBreakdownCalculator

    @MockK
    private lateinit var reportCertificateCostCategoryPersistence: ProjectReportCertificateCostCategoryPersistence

    @MockK
    private lateinit var reportCertificateUnitCostCalculatorService: GetReportCertificateUnitCostCalculatorService

    @MockK
    private lateinit var reportCertificateInvestmentCalculatorService: GetReportCertificateInvestmentCalculatorService

    @InjectMockKs
    lateinit var dataProvider: ProjectReportDataProviderImpl

    companion object {
        private const val PROJECT_ID = 1L
        private const val REPORT_ID = 2L
        private const val PARTNER_ID = 3L
        private const val PROJ_IDENTIFIER = "Project Identifier"
        private const val PROJ_ACRONYM = "Project Acronym"
        private val startDate = LocalDate.now().minusDays(1)
        private val endDate = LocalDate.now().plusDays(3)
        private val reportingDate = LocalDate.now()
        private val createdAt = ZonedDateTime.now().minusDays(2)
        private val firstSubmission = ZonedDateTime.now().minusDays(1)
        private val verificationDate = ZonedDateTime.now()

//        Report Data

        private val projectPeriod = ProjectPeriod(
            number = 1,
            start = 2,
            end = 3
        )
        private val projectPeriodStart = ProjectPeriod(
            number = 2,
            start = 2,
            end = 3
        )
        private val projectPeriodEnd = ProjectPeriod(
            number = 2,
            start = 3,
            end = 4
        )
        private val periodReport = ProjectReportPeriodData(
            number = 1,
            start = 2,
            end = 3
        )

        private val expectedProjectPeriod = ProjectPeriodData(
            number = 1,
            start = 2,
            end = 3
        )
        private val expectedProjectPeriodStart = ProjectPeriodData(
            number = 2,
            start = 2,
            end = 3
        )
        private val expectedProjectPeriodEnd = ProjectPeriodData(
            number = 2,
            start = 3,
            end = 4
        )

        private val reportData = ProjectReportModel(
            id = REPORT_ID,
            reportNumber = 1,
            status = ProjectReportStatus.Draft,
            linkedFormVersion = "1.0",
            startDate = startDate,
            endDate = endDate,
            deadlineId = 1,
            type = ContractingDeadlineType.Content,
            periodNumber = 1,
            reportingDate = reportingDate,
            projectId = PROJECT_ID,
            projectIdentifier = PROJ_IDENTIFIER,
            projectAcronym = PROJ_ACRONYM,
            leadPartnerNameInOriginalLanguage = "Lead Partner Original",
            leadPartnerNameInEnglish = "Lead Partner EN",
            spfPartnerId = null,
            createdAt = createdAt,
            firstSubmission = firstSubmission,
            lastReSubmission = mockk(),
            verificationDate = verificationDate.toLocalDate(),
            totalEligibleAfterVerification = BigDecimal.ZERO,
            amountRequested = BigDecimal.ZERO,
            verificationEndDate = verificationDate,
            lastVerificationReOpening = mockk(),
            riskBasedVerification = false,
            riskBasedVerificationDescription = "Description"
        )

        private val expectedReportData = ProjectReportData(
            id = REPORT_ID,
            reportNumber = 1,
            status = ProjectReportStatusData.Draft,
            linkedFormVersion = "1.0",
            startDate = startDate,
            endDate = endDate,
            deadlineId = 1,
            type = ContractingDeadlineTypeData.Content,
            periodDetail = periodReport,
            reportingDate = reportingDate,
            projectId = PROJECT_ID,
            projectIdentifier = PROJ_IDENTIFIER,
            projectAcronym = PROJ_ACRONYM,
            leadPartnerNameInOriginalLanguage = "Lead Partner Original",
            leadPartnerNameInEnglish = "Lead Partner EN",
            createdAt = createdAt,
            firstSubmission = firstSubmission,
            verificationDate = verificationDate.toLocalDate()
        )

        //        Identification
        private val projectIdentificationTargetGroup = ProjectReportIdentificationTargetGroup(
            type = ProjectTargetGroup.InterestGroups,
            sortNumber = 1,
            description = setOf(InputTranslation(SystemLanguage.EN, "This is a dummy description"))
        )

        private val projectIdentificationTargetGroupData = ProjectReportIdentificationTargetGroupData(
            type = ProjectTargetGroupData.InterestGroups,
            sortNumber = 1,
            description = setOf(InputTranslationData(SystemLanguageData.EN, "This is a dummy description"))
        )
        private val projectPartnerPeriod = ProjectPartnerReportPeriod(
            number = 1,
            periodBudget = BigDecimal.ONE,
            periodBudgetCumulative = BigDecimal.TEN,
            start = 1,
            end = 3
        )

        private val projectPartnerPeriodData = ProjectPartnerReportPeriodData(
            number = 1,
            periodBudget = BigDecimal.ONE,
            periodBudgetCumulative = BigDecimal.TEN,
            start = 1,
            end = 3
        )

        private val spendingProfile = ProjectReportSpendingProfile(
            partnerRole = ProjectPartnerRole.PARTNER,
            partnerNumber = 1,
            periodDetail = projectPartnerPeriod,
            currentReport = BigDecimal.TEN,
            previouslyReported = BigDecimal.TEN,
            differenceFromPlan = BigDecimal.TEN,
            differenceFromPlanPercentage = BigDecimal.TEN,
            nextReportForecast = BigDecimal.TEN,
        )

        private val spendingProfileData = ProjectReportSpendingProfileData(
            partnerRole = ProjectPartnerRoleData.PARTNER,
            partnerNumber = 1,
            periodDetail = projectPartnerPeriodData,
            currentReport = BigDecimal.TEN,
            previouslyReported = BigDecimal.TEN,
            differenceFromPlan = BigDecimal.TEN,
            differenceFromPlanPercentage = BigDecimal.TEN,
            nextReportForecast = BigDecimal.TEN,
        )

        private val reportIdentification = ProjectReportIdentification(
            targetGroups = listOf(projectIdentificationTargetGroup),
            highlights = setOf(InputTranslation(SystemLanguage.EN, "This is a dummy description of highlight")),
            partnerProblems = setOf(InputTranslation(SystemLanguage.EN, "This is a dummy description")),
            deviations = setOf(InputTranslation(SystemLanguage.EN, "This is a dummy description")),
            spendingProfiles = listOf(
                spendingProfile
            )
        )

        val expectedReportIdentification = ProjectReportIdentificationData(
            targetGroups = listOf(projectIdentificationTargetGroupData),
            highlights = setOf(InputTranslationData(SystemLanguageData.EN, "This is a dummy description of highlight")),
            partnerProblems = setOf(InputTranslationData(SystemLanguageData.EN, "This is a dummy description")),
            deviations = setOf(InputTranslationData(SystemLanguageData.EN, "This is a dummy description")),
            spendingProfiles = listOf(
                spendingProfileData
            )
        )

//         Work plan

        private val specificObjective = setOf(InputTranslation(SystemLanguage.EN, "Specific Objective"))
        private val expectedSpecificObjective = setOf(InputTranslationData(SystemLanguageData.EN, "Specific Objective"))

        private val specificExplanation = setOf(InputTranslation(SystemLanguage.EN, "Specific Explanation"))
        private val expectedSpecificExplanation =
            setOf(InputTranslationData(SystemLanguageData.EN, "Specific Explanation"))

        private val communicationObjective = setOf(InputTranslation(SystemLanguage.EN, "Communication Objective"))
        private val expectedCommunicationObjective =
            setOf(InputTranslationData(SystemLanguageData.EN, "Communication Objective"))

        private val communicationExplanation = setOf(InputTranslation(SystemLanguage.EN, "Communication Explanation"))
        private val expectedCommunicationExplanation =
            setOf(InputTranslationData(SystemLanguageData.EN, "Communication Explanation"))

        private val description = setOf(InputTranslation(SystemLanguage.EN, "Description"))
        private val expectedDescription = setOf(InputTranslationData(SystemLanguageData.EN, "Description"))

        private val measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "Measurement Unit"))
        private val expectedMeasurementUnit = setOf(InputTranslationData(SystemLanguageData.EN, "Measurement Unit"))

        private val activity = ProjectReportWorkPackageActivity(
            id = 11L,
            number = 1,
            title = setOf(InputTranslation(SystemLanguage.EN, "Title of activity")),
            deactivated = false,
            startPeriod = projectPeriodStart,
            endPeriod = projectPeriodEnd,
            status = ProjectReportWorkPlanStatus.Fully,
            progress = setOf(InputTranslation(SystemLanguage.EN, "Progress")),
            attachment = null,
            deliverables = emptyList(),
            previousStatus = ProjectReportWorkPlanStatus.Fully,
            previousProgress = setOf(InputTranslation(SystemLanguage.EN, "Progress")),
        )
        private val expectedActivity = ProjectReportWorkPackageActivityData(
            id = 11L,
            number = 1,
            title = setOf(InputTranslationData(SystemLanguageData.EN, "Title of activity")),
            deactivated = false,
            startPeriod = expectedProjectPeriodStart,
            endPeriod = expectedProjectPeriodEnd,
            status = ProjectReportWorkPackageStatusData.Fully,
            progress = setOf(InputTranslationData(SystemLanguageData.EN, "Progress")),
            attachment = null,
            deliverables = emptyList()
        )

        private val outputIndicator = OutputIndicatorSummary(
            id = 13L,
            identifier = "Identifier",
            code = "Code",
            name = setOf(InputTranslation(SystemLanguage.EN, "Name")),
            programmePriorityCode = "123",
            measurementUnit = measurementUnit
        )
        private val expectedOutputIndicator = ProjectReportWorkPackageOutputIndicatorSummaryData(
            id = 13L,
            identifier = "Identifier",
            code = "Code",
            name = setOf(InputTranslationData(SystemLanguageData.EN, "Name")),
            programmePriorityCode = "123",
            measurementUnit = expectedMeasurementUnit
        )

        private val output = ProjectReportWorkPackageOutput(
            id = 12L,
            number = 1,
            title = setOf(InputTranslation(SystemLanguage.EN, "Title of output")),
            deactivated = false,
            outputIndicator = outputIndicator,
            period = projectPeriod,
            targetValue = BigDecimal.TEN,
            currentReport = BigDecimal.TEN,
            previouslyReported = BigDecimal.ONE,
            progress = setOf(InputTranslation(SystemLanguage.EN, "Progress")),
            attachment = null,
            previousProgress = setOf(InputTranslation(SystemLanguage.EN, "Progress")),
            previousCurrentReport = BigDecimal.TEN
        )
        private val expectedOutput = ProjectReportWorkPackageOutputData(
            id = 12L,
            number = 1,
            title = setOf(InputTranslationData(SystemLanguageData.EN, "Title of output")),
            deactivated = false,
            outputIndicator = expectedOutputIndicator,
            period = expectedProjectPeriod,
            targetValue = BigDecimal.TEN,
            currentReport = BigDecimal.TEN,
            previouslyReported = BigDecimal.ONE,
            progress = setOf(InputTranslationData(SystemLanguageData.EN, "Progress")),
            attachment = null
        )

        private val workPackage = ProjectReportWorkPackage(
            id = 10L,
            number = 1,
            deactivated = false,
            specificObjective = specificObjective,
            specificStatus = ProjectReportWorkPlanStatus.Fully,
            specificExplanation = specificExplanation,
            communicationObjective = communicationObjective,
            communicationStatus = ProjectReportWorkPlanStatus.Fully,
            communicationExplanation = communicationExplanation,
            completed = true,
            description = description,
            activities = listOf(activity),
            outputs = listOf(output),
            investments = emptyList(),
            previousSpecificStatus = ProjectReportWorkPlanStatus.Fully,
            previousCommunicationExplanation = communicationExplanation,
            previousCompleted = true,
            previousCommunicationStatus = ProjectReportWorkPlanStatus.Fully,
            previousSpecificExplanation = specificExplanation,
            previousDescription = description
        )
        private val expectedWorkPackage = ProjectReportWorkPackageData(
            id = 10L,
            number = 1,
            deactivated = false,
            specificObjective = expectedSpecificObjective,
            specificStatus = ProjectReportWorkPackageStatusData.Fully,
            specificExplanation = expectedSpecificExplanation,
            communicationObjective = expectedCommunicationObjective,
            communicationStatus = ProjectReportWorkPackageStatusData.Fully,
            communicationExplanation = expectedCommunicationExplanation,
            completed = true,
            description = expectedDescription,
            activities = listOf(expectedActivity),
            outputs = listOf(expectedOutput),
            investments = emptyList()
        )

//        Project results

        private val projectResult = ProjectReportProjectResult(
            resultNumber = 1,
            deactivated = false,
            programmeResultIndicatorId = 3,
            programmeResultIndicatorIdentifier = "Identifier",
            programmeResultIndicatorName = setOf(InputTranslation(SystemLanguage.EN, "NAME")),
            baseline = BigDecimal.ONE,
            targetValue = BigDecimal.TEN,
            achievedInReportingPeriod = BigDecimal.ONE,
            cumulativeValue = BigDecimal.ONE,
            periodDetail = projectPeriod,
            description = description,
            measurementUnit = measurementUnit,
            attachment = null,
        )
        private val expectedProjectResult = ProjectReportResultData(
            resultNumber = 1,
            deactivated = false,
            programmeResultIndicatorId = 3,
            programmeResultIndicatorIdentifier = "Identifier",
            programmeResultIndicatorName = setOf(InputTranslationData(SystemLanguageData.EN, "NAME")),
            baseline = BigDecimal.ONE,
            targetValue = BigDecimal.TEN,
            achievedInReportingPeriod = BigDecimal.ONE,
            cumulativeValue = BigDecimal.ONE,
            periodDetail = expectedProjectPeriod,
            description = expectedDescription,
            measurementUnit = expectedMeasurementUnit,
            attachment = null,
        )

        private val horizontalPrinciples = ProjectHorizontalPrinciples(
            sustainableDevelopmentCriteriaEffect = ProjectHorizontalPrinciplesEffect.PositiveEffects,
            equalOpportunitiesEffect = ProjectHorizontalPrinciplesEffect.Neutral,
            sexualEqualityEffect = ProjectHorizontalPrinciplesEffect.NegativeEffects
        )
        private val expectedHorizontalPrinciples = ProjectHorizontalPrinciplesData(
            sustainableDevelopmentCriteriaEffect = ProjectHorizontalPrinciplesEffectData.PositiveEffects,
            equalOpportunitiesEffect = ProjectHorizontalPrinciplesEffectData.Neutral,
            sexualEqualityEffect = ProjectHorizontalPrinciplesEffectData.NegativeEffects
        )

        private val projectResultPrinciple = ProjectReportResultPrinciple(
            projectResults = listOf(projectResult),
            horizontalPrinciples = horizontalPrinciples,
            sustainableDevelopmentDescription = description,
            equalOpportunitiesDescription = description,
            sexualEqualityDescription = description
        )
        private val expectedProjectResultPrinciple = ProjectReportResultPrincipleData(
            projectResults = listOf(expectedProjectResult),
            horizontalPrinciples = expectedHorizontalPrinciples,
            sustainableDevelopmentDescription = expectedDescription,
            equalOpportunitiesDescription = expectedDescription,
            sexualEqualityDescription = expectedDescription
        )

//        Partner certificates

        private val leadPartner = ProjectPartnerDetail(
            projectId = PROJECT_ID,
            id = PARTNER_ID,
            active = true,
            abbreviation = "A",
            role = ProjectPartnerRole.LEAD_PARTNER,
            nameInOriginalLanguage = "A",
            nameInEnglish = "A",
            createdAt = ZonedDateTime.now().minusDays(5),
            sortNumber = 1,
            partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
            partnerSubType = PartnerSubType.LARGE_ENTERPRISE,
            nace = NaceGroupLevel.A,
            otherIdentifierNumber = null,
            otherIdentifierDescription = emptySet(),
            pic = null,
            vat = "test vat",
            vatRecovery = ProjectPartnerVatRecovery.Yes,
            legalStatusId = null,
        )

        private val partnerCertificate = PartnerReportCertificate(
            partnerReportId = 1L,
            partnerReportNumber = 1,
            partnerId = PARTNER_ID,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerNumber = 1,
            totalEligibleAfterControl = BigDecimal.valueOf(100),
            controlEnd = verificationDate,
            projectReportId = REPORT_ID,
            projectReportNumber = 1
        )

        private val expectedPartnerCertificate = PartnerReportCertificateData(
            partnerReportId = 1L,
            partnerReportNumber = 1,
            partnerId = PARTNER_ID,
            partnerRole = ProjectPartnerRoleData.LEAD_PARTNER,
            partnerNumber = 1,
            totalEligibleAfterControl = BigDecimal.valueOf(100),
            controlEnd = verificationDate,
            projectReportId = REPORT_ID,
            projectReportNumber = 1
        )

        //        CoFinancing breakdown

        private fun getCoFinancingLine(fundId: Long?) =
            CertificateCoFinancingBreakdownLine(
                fundId = fundId,
                totalEligibleBudget = BigDecimal.TEN,
                previouslyReported = BigDecimal.ONE,
                previouslyPaid = BigDecimal.ONE,
                currentReport = BigDecimal.ONE,
                previouslyVerified = BigDecimal.valueOf(150L),
                currentVerified = BigDecimal.valueOf(15L),
                totalReportedSoFar = BigDecimal.ONE,
                totalReportedSoFarPercentage = BigDecimal.ONE,
                remainingBudget = BigDecimal.valueOf(5)
            )

        private fun getCoFinancingLineData(fundId: Long?) =
            CertificateCoFinancingBreakdownLineData(
                fundId = fundId,
                totalEligibleBudget = BigDecimal.TEN,
                previouslyReported = BigDecimal.ONE,
                previouslyPaid = BigDecimal.ONE,
                currentReport = BigDecimal.ONE,
                totalReportedSoFar = BigDecimal.ONE,
                totalReportedSoFarPercentage = BigDecimal.ONE,
                remainingBudget = BigDecimal.valueOf(5),
                currentVerified = BigDecimal.valueOf(15)
            )

        private val certificateCoFinancing = CertificateCoFinancingBreakdown(
            funds = listOf(getCoFinancingLine(1L)),
            partnerContribution = getCoFinancingLine(null),
            publicContribution = getCoFinancingLine(null),
            automaticPublicContribution = getCoFinancingLine(null),
            privateContribution = getCoFinancingLine(null),
            total = getCoFinancingLine(null),
        )
        private val expectedCertificateCoFinancing = CertificateCoFinancingBreakdownData(
            funds = listOf(getCoFinancingLineData(1L)),
            partnerContribution = getCoFinancingLineData(null),
            publicContribution = getCoFinancingLineData(null),
            automaticPublicContribution = getCoFinancingLineData(null),
            privateContribution = getCoFinancingLineData(null),
            total = getCoFinancingLineData(null),
        )

        //        Cost category breakdown
        private val costCategoryLine = CertificateCostCategoryBreakdownLine(
            totalEligibleBudget = BigDecimal.TEN,
            previouslyReported = BigDecimal.ONE,
            currentReport = BigDecimal.ONE,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.ONE,
            remainingBudget = BigDecimal.valueOf(5)
        )
        private val expectedCostCategoryLine = CertificateCostCategoryBreakdownLineData(
            totalEligibleBudget = BigDecimal.TEN,
            previouslyReported = BigDecimal.ONE,
            currentReport = BigDecimal.ONE,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.ONE,
            remainingBudget = BigDecimal.valueOf(5),
            currentVerified = BigDecimal.ZERO
        )

        private val certificateCostCategoryBreakdown = CertificateCostCategoryBreakdown(
            staff = costCategoryLine,
            office = costCategoryLine,
            travel = costCategoryLine,
            external = costCategoryLine,
            equipment = costCategoryLine,
            infrastructure = costCategoryLine,
            other = costCategoryLine,
            lumpSum = costCategoryLine,
            unitCost = costCategoryLine,
            spfCost = CertificateCostCategoryBreakdownLine(
                totalEligibleBudget = BigDecimal.valueOf(15),
                previouslyReported = BigDecimal.valueOf(16),
                currentReport = BigDecimal.valueOf(17),
                totalReportedSoFar = BigDecimal.valueOf(18),
                totalReportedSoFarPercentage = BigDecimal.valueOf(19),
                remainingBudget = BigDecimal.valueOf(20),
            ),
            total = costCategoryLine
        )
        private val expectedCertificateCostCategoryBreakdown = CertificateCostCategoryBreakdownData(
            staff = expectedCostCategoryLine,
            office = expectedCostCategoryLine,
            travel = expectedCostCategoryLine,
            external = expectedCostCategoryLine,
            equipment = expectedCostCategoryLine,
            infrastructure = expectedCostCategoryLine,
            other = expectedCostCategoryLine,
            lumpSum = expectedCostCategoryLine,
            unitCost = expectedCostCategoryLine,
            spfCost = CertificateCostCategoryBreakdownLineData(
                totalEligibleBudget = BigDecimal.valueOf(15),
                previouslyReported = BigDecimal.valueOf(16),
                currentReport = BigDecimal.valueOf(17),
                totalReportedSoFar = BigDecimal.valueOf(18),
                totalReportedSoFarPercentage = BigDecimal.valueOf(19),
                remainingBudget = BigDecimal.valueOf(20),
                currentVerified = BigDecimal.ZERO
            ),
            total = expectedCostCategoryLine
        )

//        Cost category per partner

        private val currentBudget = BudgetCostsCalculationResultFull(
            staff = BigDecimal.valueOf(19),
            office = BigDecimal.valueOf(18),
            travel = BigDecimal.valueOf(17),
            external = BigDecimal.valueOf(16),
            equipment = BigDecimal.valueOf(15),
            infrastructure = BigDecimal.valueOf(14),
            other = BigDecimal.valueOf(13),
            lumpSum = BigDecimal.valueOf(12),
            unitCost = BigDecimal.valueOf(11),
            spfCost = BigDecimal.valueOf(105, 1),
            sum = BigDecimal.valueOf(10),
        )
        private val deductedBudget = BudgetCostsCalculationResultFull(
            staff = BigDecimal.valueOf(10),
            office = BigDecimal.valueOf(15),
            travel = BigDecimal.valueOf(13),
            external = BigDecimal.valueOf(14),
            equipment = BigDecimal.valueOf(14),
            infrastructure = BigDecimal.valueOf(12),
            other = BigDecimal.valueOf(9),
            lumpSum = BigDecimal.valueOf(8),
            unitCost = BigDecimal.valueOf(7),
            spfCost = BigDecimal.valueOf(65, 1),
            sum = BigDecimal.valueOf(6),
        )

        private val expectedCurrentBudget = BudgetCostsCalculationResultFullData(
            staff = BigDecimal.valueOf(19),
            office = BigDecimal.valueOf(18),
            travel = BigDecimal.valueOf(17),
            external = BigDecimal.valueOf(16),
            equipment = BigDecimal.valueOf(15),
            infrastructure = BigDecimal.valueOf(14),
            other = BigDecimal.valueOf(13),
            lumpSum = BigDecimal.valueOf(12),
            unitCost = BigDecimal.valueOf(11),
            sum = BigDecimal.valueOf(10),
        )
        private val expectedDeductedBudget = BudgetCostsCalculationResultFullData(
            staff = BigDecimal.valueOf(10),
            office = BigDecimal.valueOf(15),
            travel = BigDecimal.valueOf(13),
            external = BigDecimal.valueOf(14),
            equipment = BigDecimal.valueOf(14),
            infrastructure = BigDecimal.valueOf(12),
            other = BigDecimal.valueOf(9),
            lumpSum = BigDecimal.valueOf(8),
            unitCost = BigDecimal.valueOf(7),
            sum = BigDecimal.valueOf(6),
        )

        private val perPartnerCostCategoryBreakdownLines = listOf(
            PerPartnerCostCategoryBreakdownLine(
                partnerId = PARTNER_ID,
                partnerNumber = 1,
                partnerAbbreviation = "Abbreviation",
                partnerRole = ProjectPartnerRole.LEAD_PARTNER,
                country = "Austria",
                officeAndAdministrationOnDirectCostsFlatRate = null,
                officeAndAdministrationOnStaffCostsFlatRate = 15,
                travelAndAccommodationOnStaffCostsFlatRate = 10,
                staffCostsFlatRate = 20,
                otherCostsOnStaffCostsFlatRate = null,
                current = currentBudget,
                deduction = deductedBudget
            )
        )
        private val expectedPerPartnerCostCategoryBreakdownLines = listOf(
            CertificateCostCategoryBreakdownLinePerPartnerData(
                partnerId = PARTNER_ID,
                partnerNumber = 1,
                partnerAbbreviation = "Abbreviation",
                partnerRole = ProjectPartnerRoleData.LEAD_PARTNER,
                country = "Austria",
                officeAndAdministrationOnDirectCostsFlatRate = null,
                officeAndAdministrationOnStaffCostsFlatRate = 15,
                travelAndAccommodationOnStaffCostsFlatRate = 10,
                staffCostsFlatRate = 20,
                otherCostsOnStaffCostsFlatRate = null,
                current = expectedCurrentBudget,
                deduction = expectedDeductedBudget
            )
        )

        private val expectedPerPartnerCostCategoryBreakdown = CertificateCostCategoryBreakdownPerPartnerData(
            partners = expectedPerPartnerCostCategoryBreakdownLines,
            totalCurrent = BudgetCostsCalculationResultFullData(
                staff = BigDecimal.valueOf(19),
                office = BigDecimal.valueOf(18),
                travel = BigDecimal.valueOf(17),
                external = BigDecimal.valueOf(16),
                equipment = BigDecimal.valueOf(15),
                infrastructure = BigDecimal.valueOf(14),
                other = BigDecimal.valueOf(13),
                lumpSum = BigDecimal.valueOf(12),
                unitCost = BigDecimal.valueOf(11),
                sum = BigDecimal.valueOf(10),
            ),
            totalDeduction = BudgetCostsCalculationResultFullData(
                staff = BigDecimal.valueOf(10),
                office = BigDecimal.valueOf(15),
                travel = BigDecimal.valueOf(13),
                external = BigDecimal.valueOf(14),
                equipment = BigDecimal.valueOf(14),
                infrastructure = BigDecimal.valueOf(12),
                other = BigDecimal.valueOf(9),
                lumpSum = BigDecimal.valueOf(8),
                unitCost = BigDecimal.valueOf(7),
                sum = BigDecimal.valueOf(6),
            ),
        )

//        Investment breakdown

        private val investmentBreakDownLine = CertificateInvestmentBreakdownLine(
            reportInvestmentId = 1L,
            investmentId = 2L,
            investmentNumber = 1,
            workPackageNumber = 1,
            title = setOf(InputTranslation(SystemLanguage.EN, "Title")),
            deactivated = false,
            totalEligibleBudget = BigDecimal.TEN,
            previouslyReported = BigDecimal.ONE,
            currentReport = BigDecimal.ONE,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.ONE,
            remainingBudget = BigDecimal.valueOf(5),
            previouslyVerified = BigDecimal.ONE,
            currentVerified = BigDecimal.ONE
        )
        private val expectedInvestmentBreakDownLine = CertificateInvestmentBreakdownLineData(
            reportInvestmentId = 1L,
            investmentId = 2L,
            investmentNumber = 1,
            workPackageNumber = 1,
            title = setOf(InputTranslationData(SystemLanguageData.EN, "Title")),
            deactivated = false,
            totalEligibleBudget = BigDecimal.TEN,
            previouslyReported = BigDecimal.ONE,
            currentReport = BigDecimal.ONE,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.ONE,
            remainingBudget = BigDecimal.valueOf(5)
        )
        private val certificateInvestmentBreakdown = CertificateInvestmentBreakdown(
            investments = listOf(investmentBreakDownLine),
            total = investmentBreakDownLine
        )
        private val expectedCertificateInvestmentBreakdown = CertificateInvestmentBreakdownData(
            investments = listOf(expectedInvestmentBreakDownLine),
            total = expectedInvestmentBreakDownLine
        )

        //        Lump sums breakdown
        private val lumpSumBreakdownLine = CertificateLumpSumBreakdownLine(
            reportLumpSumId = 1L,
            lumpSumId = 1L,
            name = setOf(InputTranslation(SystemLanguage.EN, "Name")),
            period = 1,
            orderNr = 1,
            totalEligibleBudget = BigDecimal.TEN,
            previouslyReported = BigDecimal.ONE,
            currentReport = BigDecimal.ONE,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.ONE,
            remainingBudget = BigDecimal.valueOf(5),
            previouslyPaid = BigDecimal.ONE,
            previouslyVerified = BigDecimal.ONE,
            currentVerified = BigDecimal.ONE
        )
        private val expectedLumpSumBreakdownLine = CertificateLumpSumBreakdownLineData(
            reportLumpSumId = 1L,
            lumpSumId = 1L,
            name = setOf(InputTranslationData(SystemLanguageData.EN, "Name")),
            period = 1,
            orderNr = 1,
            totalEligibleBudget = BigDecimal.TEN,
            previouslyReported = BigDecimal.ONE,
            currentReport = BigDecimal.ONE,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.ONE,
            remainingBudget = BigDecimal.valueOf(5),
            previouslyPaid = BigDecimal.ONE
        )
        private val certificateLumpSumsBreakdown = CertificateLumpSumBreakdown(
            lumpSums = listOf(lumpSumBreakdownLine),
            total = lumpSumBreakdownLine
        )
        private val expectedCertificateLumpSumsBreakdown = CertificateLumpSumBreakdownData(
            lumpSums = listOf(expectedLumpSumBreakdownLine),
            total = expectedLumpSumBreakdownLine
        )

        //        Unit cost breakdown

        private val unitCostBreakdownLine = CertificateUnitCostBreakdownLine(
            reportUnitCostId = 1L,
            unitCostId = 1L,
            name = setOf(InputTranslation(SystemLanguage.EN, "Name")),
            totalEligibleBudget = BigDecimal.TEN,
            previouslyReported = BigDecimal.ONE,
            currentReport = BigDecimal.ONE,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.ONE,
            remainingBudget = BigDecimal.valueOf(5),
            previouslyVerified = BigDecimal.ONE,
            currentVerified = BigDecimal.ONE
        )
        private val expectedUnitCostBreakdownLine = CertificateUnitCostBreakdownLineData(
            reportUnitCostId = 1L,
            unitCostId = 1L,
            name = setOf(InputTranslationData(SystemLanguageData.EN, "Name")),
            totalEligibleBudget = BigDecimal.TEN,
            previouslyReported = BigDecimal.ONE,
            currentReport = BigDecimal.ONE,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.ONE,
            remainingBudget = BigDecimal.valueOf(5),
        )
        private val certificateUnitCostBreakdown = CertificateUnitCostBreakdown(
            unitCosts = listOf(unitCostBreakdownLine),
            total = unitCostBreakdownLine
        )
        private val expectedCertificateUnitCostBreakdown = CertificateUnitCostBreakdownData(
            unitCosts = listOf(expectedUnitCostBreakdownLine),
            total = expectedUnitCostBreakdownLine
        )

    }

    @Test
    fun getGeneralData() {
        every {
            projectReportPersistence.getReportById(
                projectId = PROJECT_ID,
                reportId = REPORT_ID
            )
        } returns reportData
        every { projectPersistence.getProjectPeriods(PROJECT_ID, any()) } returns listOf(projectPeriod)
        assertThat(dataProvider.get(PROJECT_ID, reportId = REPORT_ID)).isEqualTo(expectedReportData)
    }

    @Test
    fun getAllProjectReportsBaseDataByProjectId() {
        val sequence = sequenceOf(
            ProjectReportBaseData(80L, "v1.0", 1),
            ProjectReportBaseData(81L, "v1.0", 2),
            ProjectReportBaseData(82L, "v1.0", 1),
        )

        every { projectReportPersistence.getAllProjectReportsBaseDataByProjectId(PROJECT_ID) } returns sequence
        assertThat(dataProvider.getAllProjectReportsBaseDataByProjectId(PROJECT_ID)).isEqualTo(sequence)
    }

    @Test
    fun getIdentification() {
        every {
            getIdentificationPersistence.getReportIdentification(
                projectId = PROJECT_ID,
                reportId = REPORT_ID
            )
        } returns reportIdentification
        every { getIdentificationService.getProjectReportSpendingProfiles(PROJECT_ID, REPORT_ID) } returns listOf(
            spendingProfile
        )
        assertThat(dataProvider.getIdentification(PROJECT_ID, REPORT_ID)).isEqualTo(
            expectedReportIdentification
        )
    }

    @Test
    fun getWorkPlan() {
        every { reportWorkPlanPersistence.getReportWorkPlanById(PROJECT_ID, REPORT_ID) } returns listOf(workPackage)
        assertThat(dataProvider.getWorkPlan(PROJECT_ID, REPORT_ID)).usingRecursiveComparison().isEqualTo(
            listOf(expectedWorkPackage)
        )
    }

    @Test
    fun getProjectResults() {
        every {
            projectReportResultPrinciplePersistence.getProjectResultPrinciples(
                PROJECT_ID,
                REPORT_ID
            )
        } returns projectResultPrinciple
        assertThat(dataProvider.getProjectResults(PROJECT_ID, REPORT_ID)).isEqualTo(expectedProjectResultPrinciple)
    }

    @Test
    fun getPartnerCertificates() {
        every { partnerPersistence.findTop50ByProjectId(PROJECT_ID) } returns listOf(leadPartner)

        every {
            projectReportCertificatePersistence.listCertificates(
                setOf(PARTNER_ID),
                Pageable.unpaged()
            ).content
        } returns listOf(partnerCertificate)
        assertThat(dataProvider.getPartnerCertificates(PROJECT_ID, REPORT_ID)).isEqualTo(
            listOf(
                expectedPartnerCertificate
            )
        )
    }

    @Test
    fun getCoFinancingOverview() {
        every {
            getReportCertificateCoFinancingBreakdownCalculator.get(
                PROJECT_ID,
                REPORT_ID
            )
        } returns certificateCoFinancing
        assertThat(dataProvider.getCoFinancingOverview(PROJECT_ID, REPORT_ID)).isEqualTo(expectedCertificateCoFinancing)
    }

    @Test
    fun getCostCategoryOverview() {
        every {
            getReportCertificateCostCategoryBreakdownCalculator.getSubmittedOrCalculateCurrent(PROJECT_ID, REPORT_ID)
        } returns certificateCostCategoryBreakdown
        assertThat(dataProvider.getCostCategoryOverview(PROJECT_ID, REPORT_ID)).isEqualTo(
            expectedCertificateCostCategoryBreakdown
        )
    }

    @Test
    fun getInvestmentOverview() {
        every {
            reportCertificateInvestmentCalculatorService.getSubmittedOrCalculateCurrent(PROJECT_ID, REPORT_ID)
        } returns certificateInvestmentBreakdown
        assertThat(dataProvider.getInvestmentOverview(PROJECT_ID, REPORT_ID)).isEqualTo(
            expectedCertificateInvestmentBreakdown
        )
    }

    @Test
    fun getLumpSumOverview() {
        every {
            getReportCertificateLumpSumBreakdownCalculator.getSubmittedOrCalculateCurrent(PROJECT_ID, REPORT_ID)
        } returns certificateLumpSumsBreakdown
        assertThat(dataProvider.getLumpSumOverview(PROJECT_ID, REPORT_ID)).isEqualTo(
            expectedCertificateLumpSumsBreakdown
        )
    }

    @Test
    fun getUnitCostOverview() {
        every {
            reportCertificateUnitCostCalculatorService.getSubmittedOrCalculateCurrent(PROJECT_ID, REPORT_ID)
        } returns certificateUnitCostBreakdown
        assertThat(dataProvider.getUnitCostOverview(PROJECT_ID, REPORT_ID)).isEqualTo(
            expectedCertificateUnitCostBreakdown
        )
    }

    @Test
    fun getCostCategoryOverviewPerPartner() {
        every {
            reportCertificateCostCategoryPersistence.getCostCategoriesPerPartner(PROJECT_ID, REPORT_ID)
        } returns perPartnerCostCategoryBreakdownLines
        assertThat(dataProvider.getCostCategoryOverviewPerPartner(PROJECT_ID, REPORT_ID)).isEqualTo(
            expectedPerPartnerCostCategoryBreakdown
        )
    }
}

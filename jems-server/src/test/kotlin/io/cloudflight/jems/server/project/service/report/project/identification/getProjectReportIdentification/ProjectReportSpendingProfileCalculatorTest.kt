package io.cloudflight.jems.server.project.service.report.project.identification.getProjectReportIdentification

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddress
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddressType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectPartnerReportIdentificationSummary
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentification
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentificationTargetGroup
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportSpendingProfile
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportSpendingProfileReportedValues
import io.cloudflight.jems.server.project.service.report.model.project.identification.SpendingProfileLine
import io.cloudflight.jems.server.project.service.report.model.project.identification.SpendingProfileTotal
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.project.service.report.project.identification.ProjectReportIdentificationPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectReportSpendingProfileCalculatorTest: UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val REPORT_ID = 2L

        private val identification = ProjectReportIdentification(
            targetGroups = listOf(
                ProjectReportIdentificationTargetGroup(
                    type = ProjectTargetGroup.CrossBorderLegalBody,
                    sortNumber = 1,
                    description = setOf(InputTranslation(SystemLanguage.EN, "description"))
                )
            ),
            highlights = setOf(
                InputTranslation(SystemLanguage.EN, "highlights EN"),
                InputTranslation(SystemLanguage.DE, "highlights DE")
            ),
            partnerProblems = setOf(),
            deviations = setOf(),
            spendingProfilePerPartner = ProjectReportSpendingProfile(emptyList(),  SpendingProfileTotal(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
            )
            )
        )

        private val leadPartnerReportIdentificationSummary = ProjectPartnerReportIdentificationSummary(
            id = REPORT_ID,
            reportNumber = 1,
            partnerNumber = 1,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerId = 10L,
            sumTotalEligibleAfterControl = BigDecimal(400),
            nextReportForecast = BigDecimal(250),
            periodDetail = ProjectPartnerReportPeriod(
                end = 1,
                start = 1,
                periodBudget = BigDecimal.ZERO,
                periodBudgetCumulative = BigDecimal.ZERO,
                number = 1
            ),
        )
        private val secondPartnerReportIdentificationSummary = ProjectPartnerReportIdentificationSummary(
            id = REPORT_ID,
            reportNumber = 1,
            partnerNumber = 2,
            partnerRole = ProjectPartnerRole.PARTNER,
            partnerId = 11L,
            sumTotalEligibleAfterControl = BigDecimal.ZERO,
            nextReportForecast = BigDecimal(260),
            periodDetail = ProjectPartnerReportPeriod(
                number = 4,
                periodBudget = BigDecimal.valueOf(290L),
                periodBudgetCumulative = BigDecimal.valueOf(830L),
                start = 10,
                end = 12,
            ),
        )



        private val expectedResult =  ProjectReportSpendingProfile(
            lines = listOf(
                SpendingProfileLine(
                    partnerRole = ProjectPartnerRole.LEAD_PARTNER,
                    partnerNumber = 1,
                    partnerCountry = "France FR",
                    partnerAbbreviation = "ABC",
                    currentReport = BigDecimal(400),
                    previouslyReported = BigDecimal(200),
                    totalEligibleBudget = BigDecimal(1000),
                    remainingBudget = BigDecimal(400),
                    totalReportedSoFar = BigDecimal(600),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(6000, 2),
                    periodDetail = ProjectPartnerReportPeriod(
                        end = 1,
                        start = 1,
                        periodBudget = BigDecimal.ZERO,
                        periodBudgetCumulative = BigDecimal.ZERO,
                        number = 1
                    ),
                    differenceFromPlan = BigDecimal.ZERO,
                    differenceFromPlanPercentage = BigDecimal.ZERO,
                    nextReportForecast = BigDecimal(250),
                ),
                SpendingProfileLine(
                    partnerRole = ProjectPartnerRole.PARTNER,
                    partnerNumber = 2,
                    partnerCountry = "Romania RO",
                    partnerAbbreviation = "DEF",
                    periodDetail = ProjectPartnerReportPeriod(
                        number = 4,
                        periodBudget = BigDecimal.valueOf(290L),
                        periodBudgetCumulative = BigDecimal.valueOf(830L),
                        start = 10,
                        end = 12,
                    ),
                    differenceFromPlan = BigDecimal.valueOf(130L),
                    differenceFromPlanPercentage = BigDecimal.valueOf(84_34L, 2),
                    nextReportForecast = BigDecimal.valueOf(260L),
                    totalEligibleBudget = BigDecimal(2350),
                    currentReport = BigDecimal.ZERO,
                    previouslyReported = BigDecimal.valueOf(700L),
                    totalReportedSoFar = BigDecimal.valueOf(700L),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(29_79L, 2),
                    remainingBudget = BigDecimal.valueOf(1650L),
                )
            ),
            total = SpendingProfileTotal(
                periodBudget = BigDecimal.valueOf(290L),
                periodBudgetCumulative = BigDecimal.valueOf(830L),
                differenceFromPlan = BigDecimal.valueOf(-470L),
                differenceFromPlanPercentage = BigDecimal.valueOf(156_63L, 2),
                nextReportForecast = BigDecimal.valueOf(510L),
                totalEligibleBudget = BigDecimal.valueOf(3350L),
                currentReport = BigDecimal.valueOf(400L),
                previouslyReported = BigDecimal.valueOf(900L),
                totalReportedSoFar = BigDecimal.valueOf(1300L),
                totalReportedSoFarPercentage = BigDecimal.valueOf(38_81L, 2),
                remainingBudget = BigDecimal.valueOf(2050L)
            )
        )

    }

    @MockK
    private lateinit var projectReportIdentification: ProjectReportIdentificationPersistence

    @MockK
    private lateinit var projectReportCertificatePersistence: ProjectReportCertificatePersistence

    @MockK
    private lateinit var projectReportPersistence: ProjectReportPersistence

    @MockK
    private lateinit var  partnerPersistence: PartnerPersistence

    @InjectMockKs
    private lateinit var calculator: ProjectReportSpendingProfileCalculator

    @BeforeEach
    fun reset() {
        clearMocks(projectReportIdentification, projectReportCertificatePersistence, projectReportPersistence, partnerPersistence)
    }

    @Test
    fun getIdentification() {
        every { projectReportIdentification.getReportIdentification(PROJECT_ID, REPORT_ID) } returns identification

        val report = mockk<ProjectReportModel>()
        every { report.status } returns ProjectReportStatus.Draft
        every { report.linkedFormVersion } returns "1.0"
        every { projectReportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns report

        val leadPartner = mockk<ProjectPartnerDetail>()
        every { leadPartner.id } returns 10L
        every { leadPartner.sortNumber } returns 1
        every { leadPartner.role } returns ProjectPartnerRole.LEAD_PARTNER
        every { leadPartner.nameInEnglish } returns "ABC"
        every { leadPartner.addresses } returns listOf(
            ProjectPartnerAddress(
                type = ProjectPartnerAddressType.Organization,
                country = "France FR"
            )
        )

        val partner2 = mockk<ProjectPartnerDetail>()
        every { partner2.id } returns 11L
        every { partner2.sortNumber } returns 2
        every { partner2.role } returns ProjectPartnerRole.PARTNER
        every { partner2.nameInEnglish } returns "DEF"
        every { partner2.addresses } returns listOf(
            ProjectPartnerAddress(
                type = ProjectPartnerAddressType.Organization,
                country = "Romania RO"
            )
        )

        every { partnerPersistence.findTop50ByProjectId(PROJECT_ID, any()) } returns listOf(leadPartner, partner2)
        every { projectReportIdentification.getSpendingProfileReportedValues(REPORT_ID) } returns
                listOf(
                    ProjectReportSpendingProfileReportedValues(
                        10L,
                        BigDecimal(200),
                        BigDecimal.ZERO,
                        BigDecimal(1000)
                    ),
                    ProjectReportSpendingProfileReportedValues(
                        11L,
                        BigDecimal(700),
                        BigDecimal.ZERO,
                        BigDecimal(2350)
                    )
                )
        every { projectReportCertificatePersistence.getIdentificationSummariesOfProjectReport(REPORT_ID) } returns
                listOf(leadPartnerReportIdentificationSummary, secondPartnerReportIdentificationSummary)


        Assertions.assertThat(calculator.getProjectReportSpendingProfiles(PROJECT_ID, REPORT_ID)).isEqualTo(expectedResult)
    }

    @Test
    fun `partner - missing totalEligible will show correct values for non dependent fields`() {
        every { projectReportIdentification.getReportIdentification(PROJECT_ID, REPORT_ID) } returns identification

        val report = mockk<ProjectReportModel>()
        every { report.status } returns ProjectReportStatus.Draft
        every { report.linkedFormVersion } returns "1.0"
        every { projectReportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns report

        val leadPartner = mockk<ProjectPartnerDetail>()
        every { leadPartner.id } returns 10L
        every { leadPartner.sortNumber } returns 1
        every { leadPartner.role } returns ProjectPartnerRole.LEAD_PARTNER
        every { leadPartner.nameInEnglish } returns "ABC"
        every { leadPartner.addresses } returns listOf(
            ProjectPartnerAddress(
                type = ProjectPartnerAddressType.Organization,
                country = "France FR"
            )
        )

        val partner2 = mockk<ProjectPartnerDetail>()
        every { partner2.id } returns 11L
        every { partner2.sortNumber } returns 2
        every { partner2.role } returns ProjectPartnerRole.PARTNER
        every { partner2.nameInEnglish } returns "PP2 name in EN"
        every { partner2.addresses } returns emptyList()

        every { partnerPersistence.findTop50ByProjectId(PROJECT_ID, any()) } returns listOf(leadPartner, partner2)
        every { projectReportIdentification.getSpendingProfileReportedValues(REPORT_ID) } returns
                listOf(
                    ProjectReportSpendingProfileReportedValues(partnerId = 10L, previouslyReported = BigDecimal.valueOf(200),
                        currentlyReported = BigDecimal.ZERO, partnerTotalEligibleBudget =  BigDecimal.ZERO),
                    ProjectReportSpendingProfileReportedValues(partnerId = 11L, previouslyReported = BigDecimal.ZERO,
                        currentlyReported = BigDecimal.ZERO, partnerTotalEligibleBudget =  BigDecimal.ZERO),
                )
        every { projectReportCertificatePersistence.getIdentificationSummariesOfProjectReport(REPORT_ID) } returns
                listOf(leadPartnerReportIdentificationSummary, secondPartnerReportIdentificationSummary)


        val expectedResult = ProjectReportSpendingProfile(
            lines = listOf(
                SpendingProfileLine(
                    partnerRole = ProjectPartnerRole.LEAD_PARTNER,
                    partnerNumber = 1,
                    partnerCountry = "France FR",
                    partnerAbbreviation = "ABC",
                    periodDetail = ProjectPartnerReportPeriod(
                        end = 1,
                        start = 1,
                        periodBudget = BigDecimal.ZERO,
                        periodBudgetCumulative = BigDecimal.ZERO,
                        number = 1
                    ),
                    differenceFromPlan = BigDecimal.ZERO,
                    differenceFromPlanPercentage = BigDecimal.ZERO,
                    nextReportForecast = BigDecimal(250),
                    totalEligibleBudget = BigDecimal.ZERO,
                    currentReport = BigDecimal(400),
                    previouslyReported = BigDecimal(200),
                    totalReportedSoFar = BigDecimal(600),
                    totalReportedSoFarPercentage = null,
                    remainingBudget = BigDecimal.valueOf(-600L),
                ),
                SpendingProfileLine(
                    partnerRole = ProjectPartnerRole.PARTNER,
                    partnerNumber = 2,
                    partnerCountry = "N/A",
                    partnerAbbreviation = "PP2 name in EN",
                    periodDetail = ProjectPartnerReportPeriod(
                        number = 4,
                        periodBudget = BigDecimal.valueOf(290L),
                        periodBudgetCumulative = BigDecimal.valueOf(830L),
                        start = 10,
                        end = 12,
                    ),
                    differenceFromPlan = BigDecimal.valueOf(830L),
                    differenceFromPlanPercentage = BigDecimal.valueOf(0L, 2),
                    nextReportForecast = BigDecimal(260L),
                    totalEligibleBudget = BigDecimal.ZERO,
                    currentReport = BigDecimal.ZERO,
                    previouslyReported = BigDecimal.ZERO,
                    totalReportedSoFar = BigDecimal.ZERO,
                    totalReportedSoFarPercentage = BigDecimal.valueOf(100L),
                    remainingBudget = BigDecimal.ZERO,
                ),
            ),
            total = SpendingProfileTotal(
                periodBudget = BigDecimal.valueOf(290L),
                periodBudgetCumulative = BigDecimal.valueOf(830L),
                differenceFromPlan = BigDecimal.valueOf(230L),
                differenceFromPlanPercentage = BigDecimal.valueOf(72_29L, 2),
                nextReportForecast = BigDecimal.valueOf(510L),
                totalEligibleBudget = BigDecimal.ZERO,
                currentReport = BigDecimal.valueOf(400L),
                previouslyReported = BigDecimal.valueOf(200L),
                totalReportedSoFar = BigDecimal.valueOf(600L),
                totalReportedSoFarPercentage = null,
                remainingBudget = BigDecimal.valueOf(-600L),
            )
        )

        Assertions.assertThat(calculator.getProjectReportSpendingProfiles(PROJECT_ID, REPORT_ID)).isEqualTo(expectedResult)
    }
}

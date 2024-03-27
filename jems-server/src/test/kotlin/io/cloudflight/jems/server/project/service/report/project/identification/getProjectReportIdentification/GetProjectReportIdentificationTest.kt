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
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class GetProjectReportIdentificationTest : UnitTest() {

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
            ))
        )


        private val spendingProfiles =  ProjectReportSpendingProfile(
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


        private val expectedResult = ProjectReportIdentification(
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
            spendingProfilePerPartner = spendingProfiles
        )

    }

    @MockK
    private lateinit var projectReportIdentification: ProjectReportIdentificationPersistence

    @MockK
    private lateinit var calculator: ProjectReportSpendingProfileCalculator

    @InjectMockKs
    private lateinit var interactor: GetProjectReportIdentification

    @BeforeEach
    fun reset() {
        clearMocks(projectReportIdentification)
    }

    @Test
    fun getIdentification() {
        every { projectReportIdentification.getReportIdentification(PROJECT_ID, REPORT_ID) } returns identification
        every { calculator.getProjectReportSpendingProfiles(PROJECT_ID, REPORT_ID) } returns spendingProfiles

        assertThat(interactor.getIdentification(PROJECT_ID, REPORT_ID)).isEqualTo(expectedResult)
    }
}

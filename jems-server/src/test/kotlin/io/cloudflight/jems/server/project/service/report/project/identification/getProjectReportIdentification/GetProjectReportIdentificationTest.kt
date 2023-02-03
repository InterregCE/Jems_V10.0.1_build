package io.cloudflight.jems.server.project.service.report.project.identification.getProjectReportIdentification

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentification
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentificationTargetGroup
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportSpendingProfileReportedValues
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportSpendingProfile
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectPartnerReportIdentificationSummary
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

internal class GetProjectReportIdentificationTest: UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val REPORT_ID = 2L

        private val projectReportSpendingProfile = ProjectReportSpendingProfile(
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerNumber = 1,
            periodDetail = null,
            currentReport = BigDecimal(400),
            previouslyReported = BigDecimal(200),
            differenceFromPlan = BigDecimal.ZERO,
            differenceFromPlanPercentage = BigDecimal.ZERO,
            nextReportForecast = BigDecimal(250)
        )

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
            spendingProfiles = listOf(projectReportSpendingProfile)
        )

        private val partnerReportIdentificationSummary = ProjectPartnerReportIdentificationSummary(
            id = REPORT_ID,
            reportNumber = 1,
            partnerNumber = 1,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerId = 10L,
            sumTotalEligibleAfterControl = BigDecimal(200),
            nextReportForecast = BigDecimal(250),
            periodDetail = null
        )

    }

    @MockK
    private lateinit var projectReportIdentification: ProjectReportIdentificationPersistence

    @MockK
    private lateinit var projectReportCertificatePersistence: ProjectReportCertificatePersistence

    @MockK
    private lateinit var projectReportPersistence: ProjectReportPersistence

    @InjectMockKs
    lateinit var interactor: GetProjectReportIdentification

    @BeforeEach
    fun reset() {
        clearMocks(projectReportIdentification, projectReportCertificatePersistence, projectReportPersistence)
    }

    @Test
    fun getIdentification() {
        every { projectReportIdentification.getReportIdentification(PROJECT_ID, REPORT_ID) } returns identification
        every { projectReportIdentification.getSpendingProfileReportedValues(REPORT_ID) } returns
            listOf(ProjectReportSpendingProfileReportedValues(10L, BigDecimal(200), BigDecimal.ZERO))
        every { projectReportCertificatePersistence.getIdentificationSummariesOfProjectReport(REPORT_ID) } returns
            listOf(partnerReportIdentificationSummary)

        val report = mockk<ProjectReportModel>()
        every { report.status } returns ProjectReportStatus.Draft
        every { projectReportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns report

        assertThat(interactor.getIdentification(PROJECT_ID, REPORT_ID)).isEqualTo(identification)
    }
}

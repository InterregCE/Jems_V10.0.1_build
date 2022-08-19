package io.cloudflight.jems.server.project.repository.report.identification

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.identification.*
import io.cloudflight.jems.server.project.repository.report.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.report.model.identification.*
import io.cloudflight.jems.server.project.service.report.model.identification.control.ProjectPartnerControlReportChange
import io.cloudflight.jems.server.project.service.report.model.identification.control.ReportFileFormat
import io.cloudflight.jems.server.project.service.report.model.identification.control.ReportType
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Optional

class ProjectReportIdentificationPersistenceProviderTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 362L

        private val YESTERDAY = LocalDate.now().minusDays(1)
        private val TOMORROW = LocalDate.now().plusDays(1)

        private fun dummyEntity(reportEntity: ProjectPartnerReportEntity) = ProjectPartnerReportIdentificationEntity(
            reportEntity = reportEntity,
            startDate = YESTERDAY,
            endDate = TOMORROW,
            periodNumber = 7,
            nextReportForecast = BigDecimal.valueOf(3),
            formatOriginals = false,
            formatCopy = true,
            formatElectronic = true,
            type = ReportType.FinalReport,
            translatedValues = mutableSetOf(),
        ).apply {
            translatedValues.add(
                ProjectPartnerReportIdentificationTranslEntity(
                    TranslationId(this, SystemLanguage.EN),
                    summary = "summary EN",
                    problemsAndDeviations = "p&d EN",
                    spendingDeviations = "sd EN",
                )
            )
        }

        private fun dummyPeriod(report: ProjectPartnerReportEntity, periodNumber: Int) = ProjectPartnerReportBudgetPerPeriodEntity(
            id = ProjectPartnerReportBudgetPerPeriodId(report, periodNumber),
            periodBudget = BigDecimal.valueOf(15),
            periodBudgetCumulative = BigDecimal.valueOf(30),
            startMonth = periodNumber * 2 - 1,
            endMonth = periodNumber * 2,
        )

        private fun dummyTargetGroupEntity(identification: ProjectPartnerReportIdentificationEntity) = ProjectPartnerReportIdentificationTargetGroupEntity(
            type = ProjectTargetGroup.BusinessSupportOrganisation,
            reportIdentificationEntity = identification,
            sortNumber = 4,
            translatedValues = mutableSetOf(),
        ).apply {
            translatedValues.add(
                ProjectPartnerReportIdentificationTargetGroupTranslEntity(
                    TranslationId(this, SystemLanguage.EN),
                    specification = "specification EN",
                    description = "desc EN",
                )
            )
        }

        private val expectedIdentification = ProjectPartnerReportIdentification(
            startDate = YESTERDAY,
            endDate = TOMORROW,
            summary = setOf(InputTranslation(SystemLanguage.EN, "summary EN")),
            problemsAndDeviations = setOf(InputTranslation(SystemLanguage.EN, "p&d EN")),
            spendingDeviations = setOf(InputTranslation(SystemLanguage.EN, "sd EN")),
            targetGroups = listOf(
                ProjectPartnerReportIdentificationTargetGroup(
                    type = ProjectTargetGroup.BusinessSupportOrganisation,
                    sortNumber = 4,
                    specification = setOf(InputTranslation(SystemLanguage.EN, "specification EN")),
                    description = setOf(InputTranslation(SystemLanguage.EN, "desc EN")),
                ),
            ),
            spendingProfile = ProjectPartnerReportSpendingProfile(
                periodDetail = ProjectPartnerReportPeriod(7, BigDecimal.valueOf(15), BigDecimal.valueOf(30), 13, 14),
                currentReport = BigDecimal.ZERO,
                previouslyReported = BigDecimal.ZERO /* cannot be fetched, is calculated later */,
                differenceFromPlan = BigDecimal.ZERO /* cannot be fetched, is calculated later */,
                differenceFromPlanPercentage = BigDecimal.ZERO,
                nextReportForecast = BigDecimal.valueOf(3),
            ),
            controllerFormats = setOf(ReportFileFormat.Copy, ReportFileFormat.Electronic),
            type = ReportType.FinalReport,
        )

        private val newData = UpdateProjectPartnerReportIdentification(
            startDate = YESTERDAY.minusDays(1),
            endDate = TOMORROW.plusDays(1),
            period = 3,
            summary = setOf(InputTranslation(SystemLanguage.EN, "summary new EN")),
            problemsAndDeviations = setOf(InputTranslation(SystemLanguage.EN, "p&d new EN")),
            targetGroups = listOf(
                setOf(InputTranslation(SystemLanguage.EN, "desc new EN")),
            ),
            nextReportForecast = BigDecimal.valueOf(45),
            spendingDeviations = setOf(InputTranslation(SystemLanguage.SK, "sd new SK")),
        )

        private val expectedNewData = ProjectPartnerReportIdentification(
            startDate = YESTERDAY.minusDays(1),
            endDate = TOMORROW.plusDays(1),
            summary = setOf(InputTranslation(SystemLanguage.EN, "summary new EN")),
            problemsAndDeviations = setOf(InputTranslation(SystemLanguage.EN, "p&d new EN")),
            spendingDeviations = setOf(InputTranslation(SystemLanguage.SK, "sd new SK")),
            targetGroups = emptyList(),
            spendingProfile = ProjectPartnerReportSpendingProfile(
                periodDetail = ProjectPartnerReportPeriod(3, BigDecimal.valueOf(15), BigDecimal.valueOf(30), 5, 6),
                currentReport = BigDecimal.ZERO,
                previouslyReported = BigDecimal.ZERO,
                differenceFromPlan = BigDecimal.ZERO,
                differenceFromPlanPercentage = BigDecimal.ZERO,
                nextReportForecast = BigDecimal.valueOf(45),
            ),
            controllerFormats = emptySet(),
            type = ReportType.PartnerReport,
        )

    }

    @MockK
    lateinit var reportRepository: ProjectPartnerReportRepository

    @MockK
    lateinit var identificationRepository: ProjectPartnerReportIdentificationRepository

    @MockK
    lateinit var identificationTargetGroupRepository: ProjectPartnerReportIdentificationTargetGroupRepository

    @MockK
    lateinit var reportBudgetPerPeriodRepository: ProjectPartnerReportBudgetPerPeriodRepository

    @InjectMockKs
    lateinit var persistence: ProjectReportIdentificationPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(identificationRepository)
        clearMocks(identificationTargetGroupRepository)
    }

    @Test
    fun getPartnerReportIdentification() {
        val report = mockk<ProjectPartnerReportEntity>()
        val identification = dummyEntity(report)
        every {
            identificationRepository.findByReportEntityIdAndReportEntityPartnerId(reportId = 18L, partnerId = PARTNER_ID)
        } returns Optional.of(identification)
        every { identificationTargetGroupRepository.findAllByReportIdentificationEntityOrderBySortNumber(identification) } returns
            listOf(dummyTargetGroupEntity(identification))
        every { reportBudgetPerPeriodRepository.findByIdReportIdAndIdPeriodNumber(reportId = 18L, 7) } returns dummyPeriod(report, 7)

        assertThat(persistence.getPartnerReportIdentification(PARTNER_ID, reportId = 18L).get())
            .isEqualTo(expectedIdentification)
    }

    @Test
    fun `getPartnerReportIdentification - not existing`() {
        every { identificationRepository.findByReportEntityIdAndReportEntityPartnerId(reportId = 20L, partnerId = PARTNER_ID) } returns Optional.empty()
        assertThat(persistence.getPartnerReportIdentification(PARTNER_ID, reportId = 20L)).isNotPresent
        verify(exactly = 0) { identificationTargetGroupRepository.findAllByReportIdentificationEntityOrderBySortNumber(any()) }
    }

    @Test
    fun updatePartnerReportIdentification() {
        val report = mockk<ProjectPartnerReportEntity>()
        val identification = dummyEntity(report)
        val targetGroup = dummyTargetGroupEntity(identification)
        every {
            identificationRepository.findByReportEntityIdAndReportEntityPartnerId(reportId = 24L, partnerId = PARTNER_ID)
        } returns Optional.of(identification)
        every { identificationTargetGroupRepository.findAllByReportIdentificationEntityOrderBySortNumber(identification) } returns
            listOf(targetGroup)
        every { reportBudgetPerPeriodRepository.findByIdReportIdAndIdPeriodNumber(reportId = 24L, 3) } returns dummyPeriod(report, 3)

        persistence.updatePartnerReportIdentification(PARTNER_ID, reportId = 24L, newData)

        assertThat(identification.startDate).isEqualTo(YESTERDAY.minusDays(1))
        assertThat(identification.endDate).isEqualTo(TOMORROW.plusDays(1))
        assertThat(identification.periodNumber).isEqualTo(3)
        assertThat(identification.translatedValues.map { Pair(it.language(), it.summary) })
            .containsExactly(Pair(SystemLanguage.EN, "summary new EN"), Pair(SystemLanguage.SK, null))
        assertThat(identification.translatedValues.map { Pair(it.language(), it.problemsAndDeviations) })
            .containsExactly(Pair(SystemLanguage.EN, "p&d new EN"), Pair(SystemLanguage.SK, null))
        assertThat(identification.translatedValues.map { Pair(it.language(), it.spendingDeviations) })
            .containsExactly(Pair(SystemLanguage.EN, null), Pair(SystemLanguage.SK, "sd new SK"))

        assertThat(targetGroup.type).isEqualTo(ProjectTargetGroup.BusinessSupportOrganisation)
        assertThat(targetGroup.translatedValues.map { Pair(it.language(), it.description) })
            .containsExactly(Pair(SystemLanguage.EN, "desc new EN"))

        verify(exactly = 0) { identificationRepository.save(any()) }
    }

    @Test
    fun `updatePartnerReportIdentification - not existing`() {
        val report = mockk<ProjectPartnerReportEntity>()
        every { identificationRepository.findByReportEntityIdAndReportEntityPartnerId(reportId = 28L, partnerId = PARTNER_ID) } returns Optional.empty()
        every { reportRepository.findByIdAndPartnerId(id = 28L, PARTNER_ID) } returns report
        every { identificationRepository.save(any()) } returnsArgument 0
        val slotEntity = slot<ProjectPartnerReportIdentificationEntity>()
        every { identificationTargetGroupRepository.findAllByReportIdentificationEntityOrderBySortNumber(capture(slotEntity)) } returns emptyList()
        every { reportBudgetPerPeriodRepository.findByIdReportIdAndIdPeriodNumber(reportId = 28L, 3) } returns dummyPeriod(report, 3)

        assertThat(persistence.updatePartnerReportIdentification(PARTNER_ID, reportId = 28L, newData)).isEqualTo(expectedNewData)

        verify(exactly = 1) { identificationRepository.save(any()) }

        assertThat(slotEntity.captured.reportEntity).isEqualTo(report)
    }

    @Test
    fun getAvailablePeriods() {
        val report = mockk<ProjectPartnerReportEntity>()
        every { reportBudgetPerPeriodRepository.findAllByIdReportPartnerIdAndIdReportIdOrderByIdPeriodNumber(PARTNER_ID, reportId = 38L) } returns
            mutableListOf(
                ProjectPartnerReportBudgetPerPeriodEntity(
                    id = ProjectPartnerReportBudgetPerPeriodId(report, 1),
                    periodBudget = BigDecimal.ONE,
                    periodBudgetCumulative = BigDecimal.TEN,
                    startMonth = 1,
                    endMonth = 3,
                )
            )

        assertThat(persistence.getAvailablePeriods(PARTNER_ID, reportId = 38L)).containsExactly(
            ProjectPartnerReportPeriod(number = 1, periodBudget = BigDecimal.ONE, periodBudgetCumulative = BigDecimal.TEN, 1, 3)
        )
    }

    @Test
    fun updatePartnerControlReportIdentification() {
        val reportId = 98L
        val identification = dummyEntity(mockk())
        every { identificationRepository.findByReportEntityIdAndReportEntityPartnerId(reportId = reportId, PARTNER_ID) } returns
            Optional.of(identification)
        every { identificationTargetGroupRepository.findAllByReportIdentificationEntityOrderBySortNumber(identification) } returns emptyList()
        every { reportBudgetPerPeriodRepository.findByIdReportIdAndIdPeriodNumber(reportId = reportId, 7) } returns
            dummyPeriod(identification.reportEntity, 7)

        val change = ProjectPartnerControlReportChange(
            controllerFormats = setOf(ReportFileFormat.Originals, ReportFileFormat.Copy),
            type = ReportType.PartnerReport,
        )
        val result = persistence.updatePartnerControlReportIdentification(PARTNER_ID, reportId = reportId, change)
        assertThat(result.controllerFormats).containsExactly(ReportFileFormat.Originals, ReportFileFormat.Copy)
        assertThat(result.type).isEqualTo(ReportType.PartnerReport)
    }

    @Test
    fun `updatePartnerControlReportIdentification - not existing`() {
        every { identificationRepository.findByReportEntityIdAndReportEntityPartnerId(reportId = -1L, PARTNER_ID) } returns
            Optional.empty()

        val change = ProjectPartnerControlReportChange(emptySet(), ReportType.PartnerReport)
        assertThrows<ResourceNotFoundException> {
            persistence.updatePartnerControlReportIdentification(PARTNER_ID, reportId = -1L, change)
        }
    }

}

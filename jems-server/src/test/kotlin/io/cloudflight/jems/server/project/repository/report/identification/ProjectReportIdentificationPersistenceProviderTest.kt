package io.cloudflight.jems.server.project.repository.report.identification

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationTargetGroupEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationTargetGroupTranslEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationTranslEntity
import io.cloudflight.jems.server.project.repository.report.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportIdentificationTargetGroup
import io.cloudflight.jems.server.project.service.report.model.identification.UpdateProjectPartnerReportIdentification
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
            translatedValues = mutableSetOf(),
        ).apply {
            translatedValues.add(
                ProjectPartnerReportIdentificationTranslEntity(
                    TranslationId(this, SystemLanguage.EN),
                    summary = "summary EN",
                    problemsAndDeviations = "p&d EN",
                )
            )
        }

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
            period = 7,
            summary = setOf(InputTranslation(SystemLanguage.EN, "summary EN")),
            problemsAndDeviations = setOf(InputTranslation(SystemLanguage.EN, "p&d EN")),
            targetGroups = listOf(
                ProjectPartnerReportIdentificationTargetGroup(
                    type = ProjectTargetGroup.BusinessSupportOrganisation,
                    sortNumber = 4,
                    specification = setOf(InputTranslation(SystemLanguage.EN, "specification EN")),
                    description = setOf(InputTranslation(SystemLanguage.EN, "desc EN")),
                ),
            ),
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
        )

        private val expectedNewData = ProjectPartnerReportIdentification(
            startDate = YESTERDAY.minusDays(1),
            endDate = TOMORROW.plusDays(1),
            period = 3,
            summary = setOf(InputTranslation(SystemLanguage.EN, "summary new EN")),
            problemsAndDeviations = setOf(InputTranslation(SystemLanguage.EN, "p&d new EN")),
            targetGroups = emptyList(),
        )

    }

    @MockK
    lateinit var reportRepository: ProjectPartnerReportRepository

    @MockK
    lateinit var identificationRepository: ProjectPartnerReportIdentificationRepository

    @MockK
    lateinit var identificationTargetGroupRepository: ProjectPartnerReportIdentificationTargetGroupRepository

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

        persistence.updatePartnerReportIdentification(PARTNER_ID, reportId = 24L, newData)

        assertThat(identification.startDate).isEqualTo(YESTERDAY.minusDays(1))
        assertThat(identification.endDate).isEqualTo(TOMORROW.plusDays(1))
        assertThat(identification.periodNumber).isEqualTo(3)
        assertThat(identification.translatedValues.map { Pair(it.language(), it.summary) })
            .containsExactly(Pair(SystemLanguage.EN, "summary new EN"))
        assertThat(identification.translatedValues.map { Pair(it.language(), it.problemsAndDeviations) })
            .containsExactly(Pair(SystemLanguage.EN, "p&d new EN"))

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

        assertThat(persistence.updatePartnerReportIdentification(PARTNER_ID, reportId = 28L, newData)).isEqualTo(expectedNewData)

        verify(exactly = 1) { identificationRepository.save(any()) }

        assertThat(slotEntity.captured.reportEntity).isEqualTo(report)
    }

}

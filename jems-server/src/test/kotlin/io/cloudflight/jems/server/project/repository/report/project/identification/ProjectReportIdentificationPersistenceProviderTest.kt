package io.cloudflight.jems.server.project.repository.report.project.identification

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportIdentificationTargetGroupEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportIdentificationTargetGroupTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportSpendingProfileEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportSpendingProfileId
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.project.base.ProjectReportRepository
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentification
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentificationTargetGroup
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentificationUpdate
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportSpendingProfileReportedValues
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

internal class ProjectReportIdentificationPersistenceProviderTest: UnitTest() {

    companion object {

        private val identification = ProjectReportIdentification(
            targetGroups = listOf(
                ProjectReportIdentificationTargetGroup(
                    type = ProjectTargetGroup.BusinessSupportOrganisation,
                    sortNumber = 1,
                    description = setOf(InputTranslation(SystemLanguage.EN, "description"))
                )
            ),
            highlights = setOf(),
            partnerProblems = setOf(),
            deviations = setOf(),
            spendingProfiles = listOf()
        )

        private val projectReportEntity = ProjectReportEntity(
            id = 1L,
            projectId = 99L,
            number = 1,
            status = ProjectReportStatus.Draft,
            applicationFormVersion = "3.0",
            startDate = LocalDate.now().minusDays(1),
            endDate = null,

            type = ContractingDeadlineType.Both,
            deadline = mockk(),
            reportingDate = mockk(),
            periodNumber = 4,
            projectIdentifier = "projectIdentifier",
            projectAcronym = "projectAcronym",
            leadPartnerNameInOriginalLanguage = "nameInOriginalLanguage",
            leadPartnerNameInEnglish = "nameInEnglish",

            createdAt = ZonedDateTime.now().minusWeeks(1),
            firstSubmission = ZonedDateTime.now().minusYears(1),
            verificationDate = null,
        )

        private val identificationUpdate = ProjectReportIdentificationUpdate(
            targetGroups = listOf(
                setOf(
                    InputTranslation(SystemLanguage.EN, "description"),
                )),
            highlights = setOf(),
            partnerProblems = setOf(),
            deviations = setOf()
        )

        private val translationId =  TranslationId<ProjectReportIdentificationTargetGroupEntity>(
            sourceEntity = mockk(),
            language = SystemLanguage.EN
        )

        private fun projectReportIdentificationTargetGroupTranslEntity() = ProjectReportIdentificationTargetGroupTranslEntity(
            translationId = translationId,
            description = "description"
        )

        private fun projectReportIdentificationTargetGroupEntity() = ProjectReportIdentificationTargetGroupEntity(
            id = 1L,
            projectReportEntity = projectReportEntity,
            type = ProjectTargetGroup.BusinessSupportOrganisation,
            sortNumber = 1,
            translatedValues = mutableSetOf(projectReportIdentificationTargetGroupTranslEntity())
        )

    }

    @MockK
    private lateinit var projectReportRepository: ProjectReportRepository
    @MockK
    private lateinit var targetGroupRepository: ProjectReportIdentificationTargetGroupRepository
    @MockK
    private lateinit var spendingProfileRepository: ProjectReportSpendingProfileRepository
    @MockK
    private lateinit var projectPartnerReportRepository: ProjectPartnerReportRepository


    @InjectMockKs
    private lateinit var persistence: ProjectReportIdentificationPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(projectReportRepository, targetGroupRepository, spendingProfileRepository, projectPartnerReportRepository)
    }

    @Test
    fun getReportIdentification() {
        every {projectReportRepository.getByIdAndProjectId(1L, 99L)} returns projectReportEntity
        every {targetGroupRepository.findAllByProjectReportEntityOrderBySortNumber(projectReportEntity)} returns
            listOf(projectReportIdentificationTargetGroupEntity())

        assertThat(persistence.getReportIdentification(99L, reportId = 1L)).isEqualTo(identification)
    }

    @Test
    fun getSpendingProfileReportedValues() {
        every { spendingProfileRepository.findAllByIdProjectReportIdOrderByPartnerNumber(5L) } returns listOf(
            ProjectReportSpendingProfileEntity(
                id = ProjectReportSpendingProfileId(mockk(), 45L),
                partnerNumber = 15,
                partnerAbbreviation = "abbr",
                partnerRole = ProjectPartnerRole.PARTNER,
                country = "CNTR",
                previouslyReported = BigDecimal.TEN,
                currentlyReported = BigDecimal.ONE,
            )
        )
        assertThat(persistence.getSpendingProfileReportedValues(5L)).containsExactly(
            ProjectReportSpendingProfileReportedValues(
                partnerId = 45L,
                previouslyReported = BigDecimal.TEN,
                currentlyReported = BigDecimal.ONE,
            )
        )
    }

    @Test
    fun getSpendingProfileCumulative() {
        every { spendingProfileRepository.findCumulativeForReportIds(setOf(7L)) } returns listOf(
            Pair(24L, BigDecimal.ONE)
        )
        assertThat(persistence.getSpendingProfileCumulative(setOf(7L))).containsExactlyEntriesOf(
            mapOf(24L to BigDecimal.ONE)
        )
    }

    @Test
    fun updateReportIdentification() {
        every {projectReportRepository.getByIdAndProjectId(1L, 99L)} returns projectReportEntity
        every {targetGroupRepository.findAllByProjectReportEntityOrderBySortNumber(projectReportEntity)} returns
            listOf(projectReportIdentificationTargetGroupEntity())

        assertThat(persistence.updateReportIdentification(99L, reportId = 1L, identificationUpdate)).isEqualTo(identification)
    }

    @Test
    fun getSpendingProfileCurrentValues() {
        every { projectPartnerReportRepository.findTotalAfterControlPerPartner(9L) } returns listOf(
            Pair(35L, BigDecimal.TEN)
        )
        assertThat(persistence.getSpendingProfileCurrentValues(9L)).containsExactlyEntriesOf(
            mapOf(35L to BigDecimal.TEN)
        )
    }

    @Test
    fun updateSpendingProfile() {
        val report = mockk<ProjectReportEntity>()
        every { projectReportRepository.getById(15L) } returns report

        val existing514 = ProjectReportSpendingProfileEntity(
            ProjectReportSpendingProfileId(mockk(), 514L),
            partnerNumber = 17,
            partnerAbbreviation = "not-important",
            partnerRole = ProjectPartnerRole.PARTNER,
            country = "nice country",
            previouslyReported = BigDecimal(4),
            currentlyReported = BigDecimal(8),
        )
        every { spendingProfileRepository.findAllByIdProjectReportIdOrderByPartnerNumber(15L) } returns listOf(existing514)
        val slotSaved = slot<ProjectReportSpendingProfileEntity>()
        every { spendingProfileRepository.save(capture(slotSaved)) } returnsArgument 0

        persistence.updateSpendingProfile(15L, mapOf(
            514L to BigDecimal.valueOf(400),
            515L to BigDecimal.valueOf(300),
        ))

        // updated:
        assertThat(existing514.currentlyReported).isEqualTo(BigDecimal.valueOf(400))
        // created:
        assertThat(slotSaved.captured.id).isEqualTo(ProjectReportSpendingProfileId(report, 515L))
        assertThat(slotSaved.captured.partnerNumber).isZero()
        assertThat(slotSaved.captured.partnerAbbreviation).isEmpty()
        assertThat(slotSaved.captured.partnerRole).isEqualTo(ProjectPartnerRole.PARTNER)
        assertThat(slotSaved.captured.country).isNull()
        assertThat(slotSaved.captured.previouslyReported).isZero()
        assertThat(slotSaved.captured.currentlyReported).isEqualTo(BigDecimal.valueOf(300))
    }

}

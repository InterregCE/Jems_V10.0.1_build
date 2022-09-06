package io.cloudflight.jems.server.project.repository.report.contribution

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.minio.MinioStorage
import io.cloudflight.jems.server.project.entity.report.contribution.ProjectPartnerReportContributionEntity
import io.cloudflight.jems.server.project.entity.report.file.ReportProjectFileEntity
import io.cloudflight.jems.server.project.repository.report.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.file.ProjectReportFileRepository
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.report.model.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.contribution.update.UpdateProjectPartnerReportContributionExisting
import io.cloudflight.jems.server.project.service.report.model.contribution.withoutCalculations.ProjectPartnerReportEntityContribution
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
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
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.UUID

class ProjectReportContributionPersistenceProviderTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 380L
        private val HISTORY_CONTRIBUTION_UUID = UUID.randomUUID()

        private val dummyAttachment = ReportProjectFileEntity(
            id = 870L,
            projectId = 10L,
            partnerId = PARTNER_ID,
            path = "",
            minioBucket = "some_bucket",
            minioLocation = "",
            name = "some_file.txt",
            type = mockk(),
            size = 1785,
            user = mockk(),
            uploaded = ZonedDateTime.now(),
            description = "example description",
        )

        private val contributionEntity = ProjectPartnerReportContributionEntity(
            id = 989L,
            reportEntity = mockk(),
            sourceOfContribution = "source text",
            legalStatus = ProjectPartnerContributionStatus.Public,
            idFromApplicationForm = 200L,
            historyIdentifier = HISTORY_CONTRIBUTION_UUID,
            createdInThisReport = true,
            amount = BigDecimal.TEN,
            previouslyReported = BigDecimal.ONE,
            currentlyReported = BigDecimal.ZERO,
            attachment = dummyAttachment,
        )

        private val expectedContribution = ProjectPartnerReportEntityContribution(
            id = 989L,
            sourceOfContribution = "source text",
            legalStatus = ProjectPartnerContributionStatus.Public,
            idFromApplicationForm = 200L,
            historyIdentifier = HISTORY_CONTRIBUTION_UUID,
            createdInThisReport = true,
            amount = BigDecimal.TEN,
            previouslyReported = BigDecimal.ONE,
            currentlyReported = BigDecimal.ZERO,
            attachment = ProjectReportFileMetadata(dummyAttachment.id, dummyAttachment.name, dummyAttachment.uploaded),
        )

        private fun contributionEntity(id: Long, current: BigDecimal) = ProjectPartnerReportContributionEntity(
            id = id,
            reportEntity = mockk(),
            sourceOfContribution = "source text",
            legalStatus = ProjectPartnerContributionStatus.Public,
            idFromApplicationForm = id + 1000L,
            historyIdentifier = UUID.randomUUID(),
            createdInThisReport = true,
            amount = BigDecimal.TEN,
            previouslyReported = BigDecimal.ONE,
            currentlyReported = current,
            attachment = dummyAttachment,
        )

        private val dummyCreate = CreateProjectPartnerReportContribution(
            sourceOfContribution = "source text",
            legalStatus = ProjectPartnerContributionStatus.Public,
            idFromApplicationForm = 200L,
            historyIdentifier = HISTORY_CONTRIBUTION_UUID,
            createdInThisReport = true,
            amount = BigDecimal.TEN,
            previouslyReported = BigDecimal.ONE,
            currentlyReported = BigDecimal.ZERO,
        )

    }

    @MockK
    lateinit var reportRepository: ProjectPartnerReportRepository

    @MockK
    lateinit var reportContributionRepository: ProjectPartnerReportContributionRepository

    @MockK
    lateinit var reportFileRepository: ProjectReportFileRepository

    @MockK
    lateinit var minioStorage: MinioStorage

    @InjectMockKs
    lateinit var persistence: ProjectReportContributionPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(reportRepository)
        clearMocks(reportContributionRepository)
        clearMocks(reportFileRepository)
        clearMocks(minioStorage)
    }

    @Test
    fun getPartnerReportContribution() {
        every {
            reportContributionRepository.findAllByReportEntityIdAndReportEntityPartnerIdOrderById(reportId = 22L, partnerId = PARTNER_ID)
        } returns listOf(contributionEntity)

        assertThat(persistence.getPartnerReportContribution(PARTNER_ID, reportId = 22L))
            .containsExactly(expectedContribution)
    }

    @Test
    fun existsByContributionId() {
        val reportId = 25L
        every { reportContributionRepository
            .existsByReportEntityPartnerIdAndReportEntityIdAndId(contribId = 30L, reportId = reportId, partnerId = PARTNER_ID)
        } returns false

        assertThat(persistence.existsByContributionId(PARTNER_ID, reportId, contributionId = 30L)).isFalse
    }

    @Test
    fun getAllContributionsForReportIds() {
        every {
            reportContributionRepository.findAllByReportEntityIdInOrderByReportEntityIdAscIdAsc(reportIds = setOf(36L))
        } returns listOf(contributionEntity)

        assertThat(persistence.getAllContributionsForReportIds(reportIds = setOf(36L)))
            .containsExactly(expectedContribution)
    }

    @Test
    fun deleteByIds() {
        every { reportContributionRepository.findAllById(setOf(contributionEntity.id)) } returns listOf(contributionEntity)
        every { minioStorage.deleteFile(dummyAttachment.minioBucket, dummyAttachment.minioLocation) } answers { }
        every { reportFileRepository.delete(dummyAttachment) } answers { }
        every { reportContributionRepository.deleteAll(listOf(contributionEntity)) } answers { }

        persistence.deleteByIds(ids = setOf(contributionEntity.id))

        verify(exactly = 1) { minioStorage.deleteFile(dummyAttachment.minioBucket, dummyAttachment.minioLocation) }
        verify(exactly = 1) { reportFileRepository.delete(dummyAttachment) }
        verify(exactly = 1) { reportContributionRepository.deleteAll(listOf(contributionEntity)) }
    }

    @Test
    fun updateExisting() {
        val contribution_80 = contributionEntity(80L, BigDecimal.ZERO)
        val contribution_81 = contributionEntity(81L, BigDecimal.ZERO)

        val ids = slot<Set<Long>>()
        every { reportContributionRepository.findAllById(capture(ids)) } returns listOf(
            contribution_80,
            contribution_81,
        )

        persistence.updateExisting(
            setOf(
                // not existing
                UpdateProjectPartnerReportContributionExisting(-1L, BigDecimal.TEN),
                // existing
                UpdateProjectPartnerReportContributionExisting(81L, BigDecimal.ONE, "source new", ProjectPartnerContributionStatus.Private),
            )
        )

        assertThat(contribution_80.currentlyReported).isEqualTo(BigDecimal.ZERO)
        assertThat(contribution_81.currentlyReported).isEqualTo(BigDecimal.ONE)
        assertThat(contribution_81.sourceOfContribution).isEqualTo("source new")
        assertThat(contribution_81.legalStatus).isEqualTo(ProjectPartnerContributionStatus.Private)
    }

    @Test
    fun addNew() {
        every { reportRepository.getById(70L) } returns mockk()
        val contribSlot = slot<Iterable<ProjectPartnerReportContributionEntity>>()
        every { reportContributionRepository.saveAll(capture(contribSlot)) } returnsArgument 0

        persistence.addNew(70L, listOf(dummyCreate))

        assertThat(contribSlot.captured).hasSize(1)
        with(contribSlot.captured.first()) {
            assertThat(sourceOfContribution).isEqualTo("source text")
            assertThat(legalStatus).isEqualTo(ProjectPartnerContributionStatus.Public)
            assertThat(idFromApplicationForm).isEqualTo(200L)
            assertThat(historyIdentifier).isEqualTo(HISTORY_CONTRIBUTION_UUID)
            assertThat(createdInThisReport).isEqualTo(true)
            assertThat(amount).isEqualTo(BigDecimal.TEN)
            assertThat(previouslyReported).isEqualTo(BigDecimal.ONE)
            assertThat(currentlyReported).isEqualTo(BigDecimal.ZERO)
            assertThat(attachment).isNull()
        }
    }
}

package io.cloudflight.jems.server.project.repository.report.contribution

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.report.contribution.ProjectPartnerReportContributionEntity
import io.cloudflight.jems.server.project.repository.report.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.report.model.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.contribution.update.UpdateProjectPartnerReportContributionExisting
import io.cloudflight.jems.server.project.service.report.model.contribution.withoutCalculations.ProjectPartnerReportEntityContribution
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
import java.util.UUID

class ProjectReportContributionPersistenceProviderTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 380L
        private val HISTORY_CONTRIBUTION_UUID = UUID.randomUUID()

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

    @InjectMockKs
    lateinit var persistence: ProjectReportContributionPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(reportRepository)
        clearMocks(reportContributionRepository)
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
    fun getAllContributionsForReportIds() {
        every {
            reportContributionRepository.findAllByReportEntityIdInOrderByReportEntityIdAscIdAsc(reportIds = setOf(36L))
        } returns listOf(contributionEntity)

        assertThat(persistence.getAllContributionsForReportIds(reportIds = setOf(36L)))
            .containsExactly(expectedContribution)
    }

    @Test
    fun deleteByIds() {
        val ids = slot<Set<Long>>()
        every { reportContributionRepository.deleteAllById(capture(ids)) } answers { }

        persistence.deleteByIds(ids = setOf(40L, 42L))
        assertThat(ids.captured).containsExactly(40L, 42L)
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
        }
    }
}

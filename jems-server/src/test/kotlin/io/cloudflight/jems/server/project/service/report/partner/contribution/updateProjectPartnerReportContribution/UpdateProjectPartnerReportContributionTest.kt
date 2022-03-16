package io.cloudflight.jems.server.project.service.report.partner.contribution.updateProjectPartnerReportContribution

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.report.model.contribution.ProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.contribution.ProjectPartnerReportContributionData
import io.cloudflight.jems.server.project.service.report.model.contribution.ProjectPartnerReportContributionOverview
import io.cloudflight.jems.server.project.service.report.model.contribution.ProjectPartnerReportContributionRow
import io.cloudflight.jems.server.project.service.report.model.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.contribution.update.UpdateProjectPartnerReportContributionCustom
import io.cloudflight.jems.server.project.service.report.model.contribution.update.UpdateProjectPartnerReportContributionExisting
import io.cloudflight.jems.server.project.service.report.model.contribution.update.UpdateProjectPartnerReportContributionWrapper
import io.cloudflight.jems.server.project.service.report.model.contribution.withoutCalculations.ProjectPartnerReportEntityContribution
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectReportContributionPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.UUID

internal class UpdateProjectPartnerReportContributionTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 489L

        private val OLD_VALUE = 1L.toBigDecimal()
        private val NEW_VALUE = 4L.toBigDecimal()

        private val CONTRIB_1_ID = UUID.randomUUID()
        private val CONTRIB_2_ID = UUID.randomUUID()
        private val CONTRIB_3_ID = UUID.randomUUID()
        private val CONTRIB_4_ID = UUID.randomUUID()

        private val MAX_NUMBER = BigDecimal.valueOf(999_999_999_99, 2)

        private val oldContribution = ProjectPartnerReportEntityContribution(
            id = 45L,
            sourceOfContribution = "source public 1",
            legalStatus = ProjectPartnerContributionStatus.Public,
            idFromApplicationForm = 200L,
            historyIdentifier = CONTRIB_1_ID,
            createdInThisReport = false,
            amount = 5L.toBigDecimal(),
            previouslyReported = 2L.toBigDecimal(),
            currentlyReported = OLD_VALUE,
        )

        private val toBeDeletedUnsuccessfully = ProjectPartnerReportEntityContribution(
            id = 46L,
            sourceOfContribution = "to be deleted but cannot be because created sooner",
            legalStatus = ProjectPartnerContributionStatus.Public,
            idFromApplicationForm = null,
            historyIdentifier = CONTRIB_2_ID,
            createdInThisReport = false,
            amount = 15L.toBigDecimal(),
            previouslyReported = 3L.toBigDecimal(),
            currentlyReported = 3L.toBigDecimal(),
        )

        private val toBeDeleted = ProjectPartnerReportEntityContribution(
            id = 47L,
            sourceOfContribution = "to be deleted",
            legalStatus = ProjectPartnerContributionStatus.Public,
            idFromApplicationForm = null,
            historyIdentifier = UUID.randomUUID(),
            createdInThisReport = true,
            amount = 100L.toBigDecimal(),
            previouslyReported = 0L.toBigDecimal(),
            currentlyReported = 0L.toBigDecimal(),
        )

        private val oldContributionFromThisReport = ProjectPartnerReportEntityContribution(
            id = 48L,
            sourceOfContribution = "this value will be updated",
            legalStatus = ProjectPartnerContributionStatus.Public,
            idFromApplicationForm = null,
            historyIdentifier = CONTRIB_3_ID,
            createdInThisReport = true,
            amount = BigDecimal.ZERO,
            previouslyReported = BigDecimal.ZERO,
            currentlyReported = BigDecimal.ZERO,
        )

        private val newContribution = ProjectPartnerReportEntityContribution(
            id = 45L,
            sourceOfContribution = "source public 1",
            legalStatus = ProjectPartnerContributionStatus.Public,
            idFromApplicationForm = 200L,
            historyIdentifier = CONTRIB_1_ID,
            createdInThisReport = false,
            amount = 5L.toBigDecimal(),
            previouslyReported = 2L.toBigDecimal(),
            currentlyReported = NEW_VALUE,
        )

        private val createdContribution = ProjectPartnerReportEntityContribution(
            id = 0L,
            sourceOfContribution = "source private created now",
            legalStatus = ProjectPartnerContributionStatus.Private,
            idFromApplicationForm = null,
            historyIdentifier = CONTRIB_4_ID,
            createdInThisReport = true,
            amount = BigDecimal.ZERO,
            previouslyReported = BigDecimal.ZERO,
            currentlyReported = 75L.toBigDecimal(),
        )

        private val oldContributionFromThisReportUpdated = ProjectPartnerReportEntityContribution(
            id = 48L,
            sourceOfContribution = "this value has been updated",
            legalStatus = ProjectPartnerContributionStatus.Private,
            idFromApplicationForm = null,
            historyIdentifier = CONTRIB_3_ID,
            createdInThisReport = true,
            amount = BigDecimal.ZERO,
            previouslyReported = BigDecimal.ZERO,
            currentlyReported = BigDecimal.ONE,
        )

        private val expectedContribution1 = ProjectPartnerReportContribution(
            id = 45L,
            sourceOfContribution = "source public 1",
            legalStatus = ProjectPartnerContributionStatus.Public,
            createdInThisReport = false,
            numbers = ProjectPartnerReportContributionRow(
                amount = 5L.toBigDecimal(),
                previouslyReported = 2L.toBigDecimal(),
                currentlyReported = NEW_VALUE,
                totalReportedSoFar = 6L.toBigDecimal(),
            ),
        )

        private val expectedContribution2 = ProjectPartnerReportContribution(
            id = 46L,
            sourceOfContribution = "to be deleted but cannot be because created sooner",
            legalStatus = ProjectPartnerContributionStatus.Public,
            createdInThisReport = false,
            numbers = ProjectPartnerReportContributionRow(
                amount = 15L.toBigDecimal(),
                previouslyReported = 3L.toBigDecimal(),
                currentlyReported = 3L.toBigDecimal(),
                totalReportedSoFar = 6L.toBigDecimal(),
            ),
        )

        private val expectedContribution3 = ProjectPartnerReportContribution(
            id = 0L, // created
            sourceOfContribution = "source private created now",
            legalStatus = ProjectPartnerContributionStatus.Private,
            createdInThisReport = true,
            numbers = ProjectPartnerReportContributionRow(
                amount = BigDecimal.ZERO,
                previouslyReported = BigDecimal.ZERO,
                currentlyReported = 75L.toBigDecimal(),
                totalReportedSoFar = 75L.toBigDecimal(),
            ),
        )

        private val expectedContribution4 = ProjectPartnerReportContribution(
            id = 48L,
            sourceOfContribution = "this value has been updated",
            legalStatus = ProjectPartnerContributionStatus.Private,
            createdInThisReport = true,
            numbers = ProjectPartnerReportContributionRow(
                amount = BigDecimal.ZERO,
                previouslyReported = BigDecimal.ZERO,
                currentlyReported = BigDecimal.ONE,
                totalReportedSoFar = BigDecimal.ONE,
            ),
        )

        private val expectedOverview = ProjectPartnerReportContributionOverview(
            public = ProjectPartnerReportContributionRow(
                amount = 20L.toBigDecimal(),
                previouslyReported = 5L.toBigDecimal(),
                currentlyReported = 7L.toBigDecimal(),
                totalReportedSoFar = 12L.toBigDecimal(),
            ),
            automaticPublic = ProjectPartnerReportContributionRow(
                amount = 0L.toBigDecimal(),
                previouslyReported = 0L.toBigDecimal(),
                currentlyReported = 0L.toBigDecimal(),
                totalReportedSoFar = 0L.toBigDecimal(),
            ),
            private = ProjectPartnerReportContributionRow(
                amount = 0L.toBigDecimal(),
                previouslyReported = 0L.toBigDecimal(),
                currentlyReported = 76L.toBigDecimal(),
                totalReportedSoFar = 76L.toBigDecimal(),
            ),
            total = ProjectPartnerReportContributionRow(
                amount = 20L.toBigDecimal(),
                previouslyReported = 5L.toBigDecimal(),
                currentlyReported = 83L.toBigDecimal(),
                totalReportedSoFar = 88L.toBigDecimal(),
            ),
        )

        private val toUpdateModelFromAf = UpdateProjectPartnerReportContributionExisting(
            id = 45L,
            currentlyReported = NEW_VALUE,
            sourceOfContribution = "source public 1" /* so this was not changed */,
            legalStatus = ProjectPartnerContributionStatus.Public /* so this was not changed */,
        )

        private val toUpdateModelFromPreviousReport = UpdateProjectPartnerReportContributionExisting(
            id = 48L,
            currentlyReported = BigDecimal.ONE,
            sourceOfContribution = "this value has been updated",
            legalStatus = ProjectPartnerContributionStatus.Private,
        )
    }

    @MockK
    lateinit var reportContributionPersistence: ProjectReportContributionPersistence

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var updateContribution: UpdateProjectPartnerReportContribution

    @BeforeEach
    fun setup() {
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
            AppInputValidationException(emptyMap())
    }

    @Test
    fun update() {
        every { reportContributionPersistence.getPartnerReportContribution(partnerId = PARTNER_ID, reportId = 8L) } returnsMany listOf(
            listOf(oldContribution, toBeDeletedUnsuccessfully, toBeDeleted, oldContributionFromThisReport),
            listOf(newContribution, toBeDeletedUnsuccessfully, createdContribution, oldContributionFromThisReportUpdated),
        )

        val slotToDelete = slot<Set<Long>>()
        val slotToUpdate = slot<Collection<UpdateProjectPartnerReportContributionExisting>>()
        val slotToCreate = slot<List<CreateProjectPartnerReportContribution>>()
        every { reportContributionPersistence.deleteByIds(capture(slotToDelete)) } answers { }
        every { reportContributionPersistence.updateExisting(capture(slotToUpdate)) } answers { }
        every { reportContributionPersistence.addNew(8L, capture(slotToCreate)) } answers { }

        val changes = UpdateProjectPartnerReportContributionWrapper(
            toBeUpdated = setOf(toUpdateModelFromAf, toUpdateModelFromPreviousReport),
            toBeDeletedIds = setOf(-1L /*will not fail*/, 46L /*will not be done*/, 47L /*will be done*/),
            toBeCreated = listOf(
                UpdateProjectPartnerReportContributionCustom(
                    sourceOfContribution = "source private created now",
                    legalStatus = ProjectPartnerContributionStatus.Private,
                    BigDecimal.TEN,
                ),
            )
        )

        assertThat(updateContribution.update(PARTNER_ID, reportId = 8L, changes)).isEqualTo(
            ProjectPartnerReportContributionData(
                contributions = listOf(expectedContribution1, expectedContribution2, expectedContribution3, expectedContribution4),
                overview = expectedOverview,
            )
        )

        verify(exactly = 1) { reportContributionPersistence.deleteByIds(any()) }
        assertThat(slotToDelete.captured).containsExactly(47L)

        verify(exactly = 1) { reportContributionPersistence.updateExisting(any()) }
        assertThat(slotToUpdate.captured).containsExactly(toUpdateModelFromAf, toUpdateModelFromPreviousReport)

        verify(exactly = 1) { reportContributionPersistence.addNew(8L, any()) }
        assertThat(slotToCreate.captured).hasSize(1)
        with(slotToCreate.captured.first()) {
            assertThat(sourceOfContribution).isEqualTo("source private created now")
            assertThat(legalStatus).isEqualTo(ProjectPartnerContributionStatus.Private)
            assertThat(idFromApplicationForm).isNull()
            assertThat(historyIdentifier).isNotNull
            assertThat(createdInThisReport).isTrue
            assertThat(amount).isZero
            assertThat(previouslyReported).isZero
            assertThat(currentlyReported).isEqualByComparingTo(10L.toBigDecimal())
        }
    }

    @Test
    fun `update - wrong inputs`() {
        val bigValue = BigDecimal.valueOf(999_999_999_991, 3)
        val minusValue = BigDecimal.valueOf(-1, 5)

        val slotValue = mutableListOf<BigDecimal>()
        val slotValueName = mutableListOf<String>()
        val slotString = mutableListOf<String>()
        val slotStringName = mutableListOf<String>()
        every { generalValidator.maxLength(capture(slotString), any(), capture(slotStringName)) } returns mapOf("" to I18nMessage(""))
        every { generalValidator.numberBetween(capture(slotValue), any(), any(), capture(slotValueName)) } returns mapOf("" to I18nMessage(""))

        assertThrows<AppInputValidationException> {
            updateContribution.update(
                PARTNER_ID, reportId = 10L, UpdateProjectPartnerReportContributionWrapper(
                    toBeUpdated = setOf(
                        toUpdateModelFromAf.copy(currentlyReported = bigValue, sourceOfContribution = getStringOfLength(256))
                    ),
                    toBeDeletedIds = emptySet(),
                    toBeCreated = listOf(
                        UpdateProjectPartnerReportContributionCustom(
                            sourceOfContribution = getStringOfLength(256),
                            legalStatus = ProjectPartnerContributionStatus.AutomaticPublic,
                            currentlyReported = BigDecimal.valueOf(-1, 5),
                        )
                    ),
                )
            )
        }

        verify(exactly = 2) { generalValidator.maxLength(any<String>(), 255, any()) }
        verify(exactly = 2) { generalValidator.numberBetween(any(), BigDecimal.ZERO, MAX_NUMBER, any()) }

        assertThat(slotValue).containsExactly(minusValue, bigValue)
        assertThat(slotValueName).containsExactly("new.currentlyReported[0]", "currentlyReported[0]")
        assertThat(slotStringName).containsExactly("new.sourceOfContribution[0]", "sourceOfContribution[0]")
    }

    @Test
    fun `update - max contributions reached`() {
        val ids = 1..25
        every { reportContributionPersistence.getPartnerReportContribution(partnerId = PARTNER_ID, reportId = 14L) } returns
            ids.map { oldContribution.copy(id = it.toLong()) }

        val toAdd = UpdateProjectPartnerReportContributionCustom(
            sourceOfContribution = "",
            legalStatus = ProjectPartnerContributionStatus.AutomaticPublic,
            currentlyReported = BigDecimal.ONE,
        )

        assertThrows<MaxAmountOfContributionsReachedException> {
            updateContribution.update(PARTNER_ID, 14L, UpdateProjectPartnerReportContributionWrapper(
                toBeUpdated = emptySet(),
                toBeDeletedIds = emptySet(),
                toBeCreated = listOf(toAdd),
            ))
        }
    }
}

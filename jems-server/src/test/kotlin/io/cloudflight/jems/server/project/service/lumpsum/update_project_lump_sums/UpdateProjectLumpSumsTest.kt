package io.cloudflight.jems.server.project.service.lumpsum.update_project_lump_sums

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.ZonedDateTime

internal class UpdateProjectLumpSumsTest : UnitTest() {

    companion object {
        private const val PROJECT_ID: Long = 4L
        private const val PROGRAMME_LUMP_SUM_ID: Long = 19L

        private val lumpSum = ProjectLumpSum(
            programmeLumpSumId = PROGRAMME_LUMP_SUM_ID,
            period = 2,
            lumpSumContributions = listOf(
                ProjectPartnerLumpSum(partnerId = 1, amount = BigDecimal.ONE),
                ProjectPartnerLumpSum(partnerId = 2, amount = BigDecimal.TEN),
            )
        )

        private fun callSettings(lumpSums: List<ProgrammeLumpSum>) = ProjectCallSettings(
            callId = 7,
            callName = "call 7",
            startDate = ZonedDateTime.now(),
            endDate = ZonedDateTime.now(),
            endDateStep1 = null,
            lengthOfPeriod = 6,
            isAdditionalFundAllowed = false,
            flatRates = emptySet(),
            lumpSums = lumpSums,
            unitCosts = listOf(),
            stateAids = emptyList(),
            applicationFormFieldConfigurations = mutableSetOf(),
            preSubmissionCheckPluginKey = null
        )

        private val periods = listOf(
            ProjectPeriod(number = 1, start = 1, end = 6),
            ProjectPeriod(number = 2, start = 7, end = 12),
            ProjectPeriod(number = 3, start = 13, end = 18),
        )
    }

    @MockK
    lateinit var veryBigLumpSumList: List<ProjectLumpSum>

    @MockK
    lateinit var persistence: ProjectLumpSumPersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    lateinit var updateProjectLumpSums: UpdateProjectLumpSums

    @Test
    fun `updateLumpSums - everything OK - splitting allowed`() {
        val programmeLumpSum = ProgrammeLumpSum(id = PROGRAMME_LUMP_SUM_ID, splittingAllowed = true)
        every { projectPersistence.getProjectCallSettings(PROJECT_ID) } returns
            callSettings(lumpSums = listOf(programmeLumpSum))
        every { projectPersistence.getProjectPeriods(PROJECT_ID) } returns periods

        every { persistence.updateLumpSums(PROJECT_ID, any()) } returnsArgument 1

        assertThat(updateProjectLumpSums.updateLumpSums(PROJECT_ID, listOf(lumpSum))).containsExactly(lumpSum.copy())
    }

    @Test
    fun `updateLumpSums - everything OK - splitting not allowed`() {
        val programmeLumpSum = ProgrammeLumpSum(id = PROGRAMME_LUMP_SUM_ID, splittingAllowed = false)
        val lumpSumNonSplittable = lumpSum.copy(
            lumpSumContributions = listOf(
                ProjectPartnerLumpSum(partnerId = 1, amount = BigDecimal.ZERO),
                ProjectPartnerLumpSum(partnerId = 2, amount = BigDecimal.TEN),
            )
        )
        every { projectPersistence.getProjectCallSettings(PROJECT_ID) } returns
            callSettings(lumpSums = listOf(programmeLumpSum))
        every { projectPersistence.getProjectPeriods(PROJECT_ID) } returns periods

        every { persistence.updateLumpSums(PROJECT_ID, any()) } returnsArgument 1

        assertThat(updateProjectLumpSums.updateLumpSums(PROJECT_ID, listOf(lumpSumNonSplittable))).containsExactly(
            lumpSumNonSplittable.copy()
        )
    }

    @Test
    fun `updateLumpSums - empty is OK`() {
        val programmeLumpSum = ProgrammeLumpSum(id = PROGRAMME_LUMP_SUM_ID, splittingAllowed = true)
        every { projectPersistence.getProjectCallSettings(PROJECT_ID) } returns
            callSettings(lumpSums = listOf(programmeLumpSum))
        every { projectPersistence.getProjectPeriods(PROJECT_ID) } returns periods

        every { persistence.updateLumpSums(PROJECT_ID, any()) } returnsArgument 1

        assertThat(updateProjectLumpSums.updateLumpSums(PROJECT_ID, emptyList())).isEmpty()
    }

    @Test
    fun `updateLumpSums - splitting not allowed`() {
        val programmeLumpSum = ProgrammeLumpSum(id = PROGRAMME_LUMP_SUM_ID, splittingAllowed = false)
        every { projectPersistence.getProjectCallSettings(PROJECT_ID) } returns
            callSettings(lumpSums = listOf(programmeLumpSum))

        every { persistence.updateLumpSums(PROJECT_ID, any()) } returnsArgument 1

        val ex = assertThrows<I18nValidationException> {
            updateProjectLumpSums.updateLumpSums(PROJECT_ID, listOf(lumpSum))
        }
        assertThat(ex.i18nKey).isEqualTo("project.lumpSum.splitting.not.allowed")
    }

    @Test
    fun `updateLumpSums - period number out of bounds`() {
        val programmeLumpSum = ProgrammeLumpSum(id = PROGRAMME_LUMP_SUM_ID, splittingAllowed = true)
        every { projectPersistence.getProjectCallSettings(PROJECT_ID) } returns
            callSettings(lumpSums = listOf(programmeLumpSum))
        every { projectPersistence.getProjectPeriods(PROJECT_ID) } returns listOf(
            ProjectPeriod(
                number = 1,
                start = 1,
                end = 6
            )
        )

        every { persistence.updateLumpSums(PROJECT_ID, any()) } returnsArgument 1

        val ex = assertThrows<I18nValidationException> {
            updateProjectLumpSums.updateLumpSums(PROJECT_ID, listOf(lumpSum))
        }
        assertThat(ex.i18nKey).isEqualTo("project.lumpSum.period.does.not.exist")
    }

    @Test
    fun `updateLumpSums - period number is optional`() {
        val programmeLumpSum = ProgrammeLumpSum(id = PROGRAMME_LUMP_SUM_ID, splittingAllowed = true)
        every { projectPersistence.getProjectCallSettings(PROJECT_ID) } returns
            callSettings(lumpSums = listOf(programmeLumpSum))
        every { projectPersistence.getProjectPeriods(PROJECT_ID) } returns periods

        val lumpSumWithoutPeriod = lumpSum.copy(period = null)
        every { persistence.updateLumpSums(PROJECT_ID, any()) } returnsArgument 1

        assertThat(updateProjectLumpSums.updateLumpSums(PROJECT_ID, listOf(lumpSumWithoutPeriod)))
            .containsExactly(lumpSumWithoutPeriod.copy())
    }

    @Test
    fun `updateLumpSums - reached max allowed amount`() {
        every { veryBigLumpSumList.size } returns 51
        val ex = assertThrows<I18nValidationException> {
            updateProjectLumpSums.updateLumpSums(PROJECT_ID, veryBigLumpSumList)
        }
        assertThat(ex.i18nKey).isEqualTo("project.lumpSum.max.allowed.reached")
    }

}

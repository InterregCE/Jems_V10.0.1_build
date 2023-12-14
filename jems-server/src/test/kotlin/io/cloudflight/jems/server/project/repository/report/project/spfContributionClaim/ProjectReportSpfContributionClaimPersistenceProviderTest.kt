package io.cloudflight.jems.server.project.repository.report.project.spfContributionClaim

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundTranslationEntity
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportSpfContributionClaimEntity
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.ProjectReportSpfContributionClaim
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectReportSpfContributionClaimPersistenceProviderTest: UnitTest() {

    companion object {
        private const val REPORT_ID = 10L
        private const val FUND_ID = 5L

        private val reportEntity = mockk<ProjectReportEntity>()
        private val programmeFundEntity = ProgrammeFundEntity(
            id = FUND_ID,
            selected = true,
            type = ProgrammeFundType.OTHER,
            translatedValues = mutableSetOf(
                ProgrammeFundTranslationEntity(
                    translationId = TranslationId(mockk(), SystemLanguage.EN),
                    abbreviation = "EN abbr",
                    description = "EN desc"
                )
            )
        )

        private fun entity() = ProjectReportSpfContributionClaimEntity(
            id = 5L,
            reportEntity = reportEntity,
            amountFromAf = BigDecimal.valueOf(80L),
            applicationFormPartnerContributionId = null,
            currentlyReported = BigDecimal.TEN,
            legalStatus = null,
            previouslyReported = BigDecimal.ZERO,
            programmeFund = programmeFundEntity,
            sourceOfContribution = null
        )

        private val model = ProjectReportSpfContributionClaim(
            id = 5L,
            currentlyReported = BigDecimal.TEN,
            legalStatus = null,
            previouslyReported = BigDecimal.ZERO,
            programmeFund = ProgrammeFund(
                id = FUND_ID,
                selected = true,
                type = ProgrammeFundType.OTHER,
                abbreviation = setOf(InputTranslation(SystemLanguage.EN, translation = "EN abbr")),
                description = setOf(InputTranslation(SystemLanguage.EN, translation = "EN desc")),
            ),
            sourceOfContribution = null,
            amountInAf = BigDecimal.valueOf(80L),
            idFromApplicationForm = null,
            reportId = REPORT_ID
        )
    }

    @MockK
    lateinit var spfContributionClaimRepository: ProjectReportSpfContributionClaimRepository

    @InjectMockKs
    lateinit var reportSpfContributionClaimPersistence: ProjectReportSpfContributionClaimPersistenceProvider

    @Test
    fun getSpfContributionClaimsFor() {
        every { spfContributionClaimRepository.getAllByReportEntityIdIn(setOf(REPORT_ID)) } returns listOf(entity())
        every { reportEntity.id } returns REPORT_ID

        Assertions.assertThat(
            reportSpfContributionClaimPersistence.getSpfContributionClaimsFor(REPORT_ID)
        ).isEqualTo(listOf(model))
    }

    @Test
    fun updateContributionClaimReportedAmount() {
        every { spfContributionClaimRepository.getAllByReportEntityIdIn(setOf(REPORT_ID)) } returns listOf(entity())
        every { reportEntity.id } returns REPORT_ID

        val toUpdate = mapOf(5L to BigDecimal.valueOf(999L))

        Assertions.assertThat(
            reportSpfContributionClaimPersistence.updateContributionClaimReportedAmount(REPORT_ID, toUpdate)
        ).isEqualTo(listOf(model.copy(currentlyReported = BigDecimal.valueOf(999L))))
    }

    @Test
    fun resetSpfContributionClaims() {
        val entity = entity()
        every { spfContributionClaimRepository.getAllByReportEntityIdIn(setOf(REPORT_ID)) } returns listOf(entity)
        reportSpfContributionClaimPersistence.resetSpfContributionClaims(REPORT_ID)
        assertThat(entity.currentlyReported).isZero()
    }

}

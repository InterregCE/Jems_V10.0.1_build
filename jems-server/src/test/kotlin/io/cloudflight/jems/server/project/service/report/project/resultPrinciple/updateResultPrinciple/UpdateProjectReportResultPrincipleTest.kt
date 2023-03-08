package io.cloudflight.jems.server.project.service.report.project.resultPrinciple.updateResultPrinciple

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrinciple
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrincipleUpdate
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.ProjectReportResultPrinciplePersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class UpdateProjectReportResultPrincipleTest : UnitTest() {

    @MockK
    private lateinit var projectReportResultPrinciplePersistence: ProjectReportResultPrinciplePersistence

    @MockK
    private lateinit var projectReportPersistence: ProjectReportPersistence

    @InjectMockKs
    private lateinit var interactor: UpdateProjectReportResultPrinciple

    @RelaxedMockK
    private lateinit var generalValidatorService: GeneralValidatorService

    @BeforeEach
    fun reset() {
        clearMocks(projectReportResultPrinciplePersistence, projectReportPersistence, generalValidatorService)

        every { generalValidatorService.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidatorService.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
            AppInputValidationException(emptyMap())

        every { generalValidatorService.maxLength(any<Set<InputTranslation>>(), any(), any()) } answers {
            firstArg<Set<InputTranslation>>().associate { it.translation!! to I18nMessage("") }
        }
        every { generalValidatorService.numberBetween(any<BigDecimal>(), any(), any(), any()) } answers {
            val actual = firstArg<BigDecimal>()
            val min = secondArg<BigDecimal>()
            val max = secondArg<BigDecimal>()
            if (actual in min..max) mapOf() else mapOf(actual.toString() to I18nMessage(""))
        }
    }

    @Test
    fun update() {
        val projectId = 15L
        val reportId = 17L
        val resultPrincipleUpdate = mockk<ProjectReportResultPrincipleUpdate> {
            every { sustainableDevelopmentDescription } returns setOf()
            every { equalOpportunitiesDescription } returns setOf()
            every { sexualEqualityDescription } returns setOf()
            every { projectResults } returns mapOf()
        }
        val resultPrinciple = mockk<ProjectReportResultPrinciple>()
        val projectReport = mockk<ProjectReportModel> {
            every { status } returns ProjectReportStatus.Draft
        }

        every { projectReportPersistence.getReportById(projectId, reportId) } returns projectReport
        every { projectReportResultPrinciplePersistence.updateProjectReportResultPrinciple(projectId, reportId, resultPrincipleUpdate) } returns resultPrinciple

        Assertions.assertThat(interactor.update(projectId, reportId, resultPrincipleUpdate)).isEqualTo(resultPrinciple)
    }

    @Test
    fun `update - invalid principle description`() {
        val projectId = 16L
        val reportId = 18L
        val resultPrincipleUpdate = mockk<ProjectReportResultPrincipleUpdate> {
            every { sustainableDevelopmentDescription } returns setOf(InputTranslation(SystemLanguage.EN, String(CharArray(2001) { '0' })))
            every { equalOpportunitiesDescription } returns setOf()
            every { sexualEqualityDescription } returns setOf()
            every { projectResults } returns mapOf()
        }

        assertThrows<AppInputValidationException> { interactor.update(projectId, reportId, resultPrincipleUpdate) }
    }

    @Test
    fun `update - invalid result description`() {
        val projectId = 16L
        val reportId = 18L
        val resultPrincipleUpdate = mockk<ProjectReportResultPrincipleUpdate> {
            every { sustainableDevelopmentDescription } returns setOf()
            every { equalOpportunitiesDescription } returns setOf()
            every { sexualEqualityDescription } returns setOf()
            every { projectResults } returns mapOf(1 to mockk {
                every { description } returns setOf(InputTranslation(SystemLanguage.EN, String(CharArray(2001) { '0' })))
                every { currentValue } returns BigDecimal.TEN
            })
        }

        assertThrows<AppInputValidationException> { interactor.update(projectId, reportId, resultPrincipleUpdate) }
    }

    @Test
    fun `update - invalid result currentValue`() {
        val projectId = 16L
        val reportId = 18L
        val resultPrincipleUpdate = mockk<ProjectReportResultPrincipleUpdate> {
            every { sustainableDevelopmentDescription } returns setOf()
            every { equalOpportunitiesDescription } returns setOf()
            every { sexualEqualityDescription } returns setOf()
            every { projectResults } returns mapOf(1 to mockk {
                every { description } returns setOf()
                every { currentValue } returns BigDecimal.valueOf(1_000_000_000_00, 2)
            })
        }

        assertThrows<AppInputValidationException> { interactor.update(projectId, reportId, resultPrincipleUpdate) }
    }
}

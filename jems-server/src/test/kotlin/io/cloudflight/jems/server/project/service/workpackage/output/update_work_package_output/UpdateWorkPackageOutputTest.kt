package io.cloudflight.jems.server.project.service.workpackage.output.update_work_package_output

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.math.BigDecimal

class UpdateWorkPackageOutputTest: UnitTest() {

    companion object {
        private val testOutput = WorkPackageOutput(
            workPackageId = 1L,
            title = setOf(InputTranslation(SystemLanguage.EN, "Test")),
            description = setOf(InputTranslation(SystemLanguage.EN, "Test")),
            periodNumber = 10,
            programmeOutputIndicatorId = 7L,
            targetValue = BigDecimal.ONE,
            deactivated = false,
        )
    }

    @MockK lateinit var mockedList: List<WorkPackageOutput>

    @MockK lateinit var projectPersistence: ProjectPersistence
    @MockK lateinit var persistence: WorkPackagePersistence

    @InjectMockKs
    private lateinit var updateOutputInteractor: UpdateWorkPackageOutput

    @Test
    fun `delete work package outputs from a work package`() {
        every { persistence.updateWorkPackageOutputs(1L, any()) } returns emptyList()
        assertThat(updateOutputInteractor.updateOutputsForWorkPackage(1L, 1L, emptyList())).isEmpty()
    }

    @ParameterizedTest(name = "update - valid {0}")
    @EnumSource(value = ApplicationStatus::class, names = [
        "CONTRACTED", "IN_MODIFICATION", "MODIFICATION_SUBMITTED", "MODIFICATION_REJECTED"
    ], mode = EnumSource.Mode.EXCLUDE)
    fun `update - valid`(status: ApplicationStatus) {
        val projectId = status.ordinal.toLong()
        every { projectPersistence.getApplicantAndStatusById(projectId).projectStatus } returns status

        every { persistence.updateWorkPackageOutputs(2L, any()) } returnsArgument 1
        assertThat(updateOutputInteractor.updateOutputsForWorkPackage(projectId, 2L, listOf(testOutput)))
            .containsExactly(testOutput)
    }

    @ParameterizedTest(name = "update after contracted - valid {0}")
    @EnumSource(value = ApplicationStatus::class, names = [
        "CONTRACTED", "IN_MODIFICATION", "MODIFICATION_SUBMITTED", "MODIFICATION_REJECTED"
    ])
    fun `update after contracted - valid`(status: ApplicationStatus) {
        val projectId = status.ordinal.toLong()
        every { projectPersistence.getApplicantAndStatusById(projectId).projectStatus } returns status

        every { persistence.getWorkPackageOutputsForWorkPackage(3L, projectId = projectId) } returns
            listOf(
                testOutput.copy(workPackageId = 3, outputNumber = 1, deactivated = false)
            )

        every { persistence.updateWorkPackageOutputsAfterContracted(3L, any()) } returnsArgument 1
        assertThat(updateOutputInteractor.updateOutputsForWorkPackage(projectId, 3L, listOf(
            testOutput.copy(workPackageId = 3, outputNumber = 1, deactivated = true),
            testOutput.copy(workPackageId = 3, outputNumber = 2, deactivated = false)
        ))).containsExactly(
            testOutput.copy(workPackageId = 3, outputNumber = 1, deactivated = true),
            testOutput.copy(workPackageId = 3, outputNumber = 2, deactivated = false),
        )
    }

    @Test
    fun `update - too many outputs`() {
        every { mockedList.size } returns 11
        val ex = assertThrows<I18nValidationException> {
            updateOutputInteractor.updateOutputsForWorkPackage(1L, 3L, mockedList)
        }
        assertThat(ex.i18nKey).isEqualTo("project.workPackage.outputs.max.allowed.reached")
    }

    @Test
    fun `update - invalid target value`() {
        assertTargetValueThrowException(BigDecimal.valueOf(999_999_999_990, 3))
        assertTargetValueThrowException(BigDecimal.valueOf(999_999_999_991, 3))
        assertTargetValueThrowException(BigDecimal.valueOf(1_000_000_000_00, 2))
        assertTargetValueThrowException(BigDecimal.valueOf(-1, 2))
    }

    private fun assertTargetValueThrowException(value: BigDecimal) {
        val toBeSaved = listOf(testOutput.copy(targetValue = value))
        val exception = assertThrows<I18nValidationException> { updateOutputInteractor.updateOutputsForWorkPackage(1L, 10L, toBeSaved) }
        assertThat(exception.i18nKey).isEqualTo("project.workPackage.targetValue.not.valid")
    }

}

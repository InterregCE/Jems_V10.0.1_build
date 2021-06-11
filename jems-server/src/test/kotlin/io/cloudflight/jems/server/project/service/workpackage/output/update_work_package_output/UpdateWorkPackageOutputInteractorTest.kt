package io.cloudflight.jems.server.project.service.workpackage.output.update_work_package_output

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutputTranslatedValue
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class UpdateWorkPackageOutputInteractorTest: UnitTest() {

    companion object {
        private val testOutput = WorkPackageOutput(
            workPackageId = 1L,
            translatedValues = setOf(WorkPackageOutputTranslatedValue(SystemLanguage.EN, "Test")),
            periodNumber = 10,
            programmeOutputIndicatorId = 7L,
            targetValue = BigDecimal.ONE,
        )
    }

    @MockK
    lateinit var mockedList: List<WorkPackageOutput>

    @MockK
    lateinit var persistence: WorkPackagePersistence

    @InjectMockKs
    private lateinit var updateOutputInteractor: UpdateWorkPackageOutput

    @Test
    fun `delete work package outputs from a work package`() {
        every { persistence.updateWorkPackageOutputs(1L, any()) } returns emptyList()
        assertThat(updateOutputInteractor.updateOutputsForWorkPackage(1L, 1L, emptyList())).isEmpty()
    }

    @Test
    fun `update - valid`() {
        every { persistence.updateWorkPackageOutputs(2L, any()) } returnsArgument 1
        assertThat(updateOutputInteractor.updateOutputsForWorkPackage(1L, 2L, listOf(testOutput)))
            .containsExactly(testOutput)
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

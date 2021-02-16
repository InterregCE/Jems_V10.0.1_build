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

class UpdateWorkPackageOutputInteractorTest: UnitTest() {

    companion object {
        private val testOutput = WorkPackageOutput(
            translatedValues = setOf(WorkPackageOutputTranslatedValue(SystemLanguage.EN, "Test")),
            periodNumber = 10,
            programmeOutputIndicatorId = 7L,
            targetValue = "target_value",
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
        assertThat(updateOutputInteractor.updateOutputsForWorkPackage(1L, emptyList())).isEmpty()
    }

    @Test
    fun `update - valid`() {
        every { persistence.updateWorkPackageOutputs(2L, any()) } returnsArgument 1
        assertThat(updateOutputInteractor.updateOutputsForWorkPackage(2L, listOf(testOutput)))
            .containsExactly(testOutput)
    }

    @Test
    fun `update - too many outputs`() {
        every { mockedList.size } returns 11
        val ex = assertThrows<I18nValidationException> {
            updateOutputInteractor.updateOutputsForWorkPackage(3L, mockedList)
        }
        assertThat(ex.i18nKey).isEqualTo("project.workPackage.outputs.max.allowed.reached")
    }

}

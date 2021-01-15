package io.cloudflight.jems.server.workpackage.service.workpackageoutput.update_work_package_output

import io.cloudflight.jems.api.programme.dto.SystemLanguage
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutputTranslatedValue
import io.cloudflight.jems.server.project.service.workpackage.output.update_work_package_output.UpdateWorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.output.update_work_package_output.UpdateWorkPackageOutputInteractor
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateWorkPackageOutputInteractorTest {

    companion object {


        private val testWorkPackageOutput1 = WorkPackageOutput(
            outputNumber = 1,
            translatedValues = setOf(WorkPackageOutputTranslatedValue(SystemLanguage.EN, "Test"))
        )


        private val testWorkPackageOutput2 = WorkPackageOutput(
            outputNumber = 2,
            translatedValues = setOf(WorkPackageOutputTranslatedValue(SystemLanguage.EN, "Test"))
        )


        private val updateTestWorkPackageOutput1 = WorkPackageOutput(
            outputNumber = 1,
            translatedValues = setOf(WorkPackageOutputTranslatedValue(SystemLanguage.EN, "Test updated"))
        )


        private val updateTestWorkPackageOutput2 = WorkPackageOutput(
            outputNumber = 2,
            translatedValues = setOf(WorkPackageOutputTranslatedValue(SystemLanguage.EN, "Test updated"))
        )

        private val workPackageOutputsUpdated =
            mutableListOf<WorkPackageOutput>(testWorkPackageOutput1, testWorkPackageOutput2)
        private val workPackageOutputsToUpdate =
            mutableListOf<WorkPackageOutput>(updateTestWorkPackageOutput1, updateTestWorkPackageOutput2)

    }

    @MockK
    lateinit var persistence: WorkPackagePersistence

    private lateinit var updateWorkPackageOutputInteractor: UpdateWorkPackageOutputInteractor

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        updateWorkPackageOutputInteractor = UpdateWorkPackageOutput(persistence)
    }

    @Test
    fun `delete work package outputs from a work package`() {
        every { persistence.updateWorkPackageOutputs(any(), any()) } returns emptyList<WorkPackageOutput>()
        Assertions.assertThat(updateWorkPackageOutputInteractor.updateWorkPackageOutputs(1L, emptyList())).isEmpty()
    }

    @Test
    fun `update `() {
        every { persistence.updateWorkPackageOutputs(1L, any()) } returns workPackageOutputsUpdated
        Assertions.assertThat(
            updateWorkPackageOutputInteractor.updateWorkPackageOutputs(
                1L,
                workPackageOutputsToUpdate,
            )
        ).isEqualTo(workPackageOutputsUpdated)
    }

}
